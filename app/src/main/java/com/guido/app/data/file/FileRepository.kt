package com.guido.app.data.file

import com.dhandadekho.mobile.utils.Resource
import kotlinx.coroutines.flow.Flow

interface FileRepository {
    fun addImagesForBusiness(image : ByteArray) : Flow<Resource<String>>
}