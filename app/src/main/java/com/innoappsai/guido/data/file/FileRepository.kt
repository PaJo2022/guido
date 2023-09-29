package com.innoappsai.guido.data.file

import com.dhandadekho.mobile.utils.Resource

interface FileRepository {
    suspend fun storeImageToServer(image: ByteArray,folderPath:String) : Resource<String>
}