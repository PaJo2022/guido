package com.guido.app.data.file

import com.dhandadekho.mobile.utils.Resource
import kotlinx.coroutines.flow.Flow

interface FileRepository {
    suspend fun addImagesForBusiness(image : ByteArray) : Resource<String>
}