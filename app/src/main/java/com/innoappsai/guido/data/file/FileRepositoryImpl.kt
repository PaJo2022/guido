package com.innoappsai.guido.data.file


import com.dhandadekho.mobile.utils.Resource
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FileRepositoryImpl @Inject constructor(private val firebaseStorage: FirebaseStorage) : FileRepository {

    override suspend fun addImagesForBusiness(image: ByteArray): Resource<String> {
        return suspendCoroutine {continuation->
            val imageRef = firebaseStorage.getReference(UUID.randomUUID().toString())
            imageRef.putBytes(image).addOnSuccessListener { taskSnapshot->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    continuation.resume(Resource.Success(imageUrl))
                }
            }.addOnFailureListener {error->
                continuation.resume(Resource.Error(Exception(error.message.toString())))
            }


        }
    }


}