package com.graduationproject.robokidsapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.graduationproject.robokidsapp.data.model.ImageContent
import com.graduationproject.robokidsapp.data.model.Videos
import com.graduationproject.robokidsapp.util.Resource

class ContentRepositoryImpl(val database: FirebaseFirestore,val storage : FirebaseStorage) : ContentRepository{

    override val storageInstance: FirebaseStorage
        get() = storage


    override suspend fun getMusicContentData(result: (Resource<ArrayList<Videos>>) -> Unit) {

        database.collection("Content")
            .document("EntertainmentContent")
            .collection("Music").addSnapshotListener { value, error ->
                if (error != null) {
                    result.invoke(Resource.Failure(error.message))
                    return@addSnapshotListener
                }
                val listVideos = ArrayList<Videos>()
                var video: Videos
                value!!.documents.forEach { document ->
                    video = document.toObject(Videos::class.java)!!
                    listVideos.add(video)
                }

                result.invoke(Resource.Success(listVideos))
            }

    }

    override suspend fun getFilmsContentData(result: (Resource<ArrayList<Videos>>) -> Unit) {
        database.collection("Content")
            .document("EntertainmentContent")
            .collection("Stories").addSnapshotListener { value, error ->
                if (error != null) {
                    result.invoke(Resource.Failure(error.message))
                    return@addSnapshotListener
                }

                val listVideos = ArrayList<Videos>()
                var video: Videos
                value!!.documents.forEach { document ->
                    video = document.toObject(Videos::class.java)!!
                    listVideos.add(video)
                }

                result.invoke(Resource.Success(listVideos))
            }
    }

    override suspend fun getPronunciationContentData(
        contentType: String,
        result: (Resource<ArrayList<ImageContent>>) -> Unit
    ) {
        database.collection("Content")
            .document("EducationalContent")
            .collection("PronunciationLetters")
            .document(contentType).collection("Data")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    result.invoke(Resource.Failure(error.message))
                    return@addSnapshotListener
                }

                val listImage = ArrayList<ImageContent>()
                var image: ImageContent
                value!!.documents.forEach { document ->
                    image = document.toObject(ImageContent::class.java)!!
                    listImage.add(image)
                }

                result.invoke(Resource.Success(listImage))
            }
    }

    override suspend fun getWhiteboardContentData(result: (Resource<ArrayList<ImageContent>>) -> Unit) {
        database.collection("Content")
            .document("EducationalContent")
            .collection("Whiteboard")
            .document("ImageKnow").collection("Data")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    result.invoke(Resource.Failure(error.message))
                    return@addSnapshotListener
                }

                val listImage = ArrayList<ImageContent>()
                var image: ImageContent
                value!!.documents.forEach { document ->
                    image = document.toObject(ImageContent::class.java)!!
                    listImage.add(image)
                }

                result.invoke(Resource.Success(listImage))
            }
    }

    override suspend fun getQuizContentData(
        contentType: String,
        result: (Resource<ArrayList<ImageContent>>) -> Unit
    ) {
        database.collection("Content")
            .document("EducationalContent")
            .collection("Quiz")
            .document("Connect")
            .collection(contentType)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    result.invoke(Resource.Failure(error.message))
                    return@addSnapshotListener
                }

                val listImage = ArrayList<ImageContent>()
                var image: ImageContent
                value!!.documents.forEach { document ->
                    image = document.toObject(ImageContent::class.java)!!
                    listImage.add(image)
                }

                result.invoke(Resource.Success(listImage))
            }
    }


}