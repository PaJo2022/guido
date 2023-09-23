package com.guido.app.data.file


import com.dhandadekho.mobile.utils.Resource
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(private val firebaseStorage: FirebaseStorage) : FileRepository {

    override fun addImagesForBusiness(image: ByteArray): Flow<Resource<String>> {
        return callbackFlow {
            val imageRef = firebaseStorage.getReference(UUID.randomUUID().toString())
            imageRef.putBytes(image).addOnSuccessListener { taskSnapshot->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    trySend(Resource.Success(imageUrl))
                }
            }.addOnFailureListener {error->
                cancel(error.message.toString())
            }

            awaitClose{close()}
        }
    }


}