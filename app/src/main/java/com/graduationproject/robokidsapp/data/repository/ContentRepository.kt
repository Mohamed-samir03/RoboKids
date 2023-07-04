package com.graduationproject.robokidsapp.data.repository

import com.google.firebase.storage.FirebaseStorage
import com.graduationproject.robokidsapp.data.model.ImageContent
import com.graduationproject.robokidsapp.data.model.Videos
import com.graduationproject.robokidsapp.util.Resource

interface ContentRepository {

    val storageInstance:FirebaseStorage

    suspend fun getMusicContentData(result: (Resource<ArrayList<Videos>>) -> Unit)

    suspend fun getFilmsContentData(result: (Resource<ArrayList<Videos>>) -> Unit)

    suspend fun getPronunciationContentData(contentType:String,result: (Resource<ArrayList<ImageContent>>) -> Unit)

    suspend fun getWhiteboardContentData(result: (Resource<ArrayList<ImageContent>>) -> Unit)

    suspend fun getQuizContentData(contentType:String,result: (Resource<ArrayList<ImageContent>>) -> Unit)

}