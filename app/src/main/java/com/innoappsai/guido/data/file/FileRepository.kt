package com.innoappsai.guido.data.file

import com.dhandadekho.mobile.utils.Resource

interface FileRepository {
    suspend fun addImagesForBusiness(image : ByteArray) : Resource<String>
}