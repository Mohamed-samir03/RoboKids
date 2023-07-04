package com.graduationproject.robokidsapp.ui.kidsFragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduationproject.robokidsapp.data.model.ImageContent
import com.graduationproject.robokidsapp.data.model.Parent
import com.graduationproject.robokidsapp.data.model.Videos
import com.graduationproject.robokidsapp.data.repository.ContentRepository
import com.graduationproject.robokidsapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContentViewModel@Inject constructor(
    val repository: ContentRepository
):ViewModel() {

    val storageInstance = repository.storageInstance

    private val _musicContent = MutableLiveData<Resource<ArrayList<Videos>>>()
    val musicContent: LiveData<Resource<ArrayList<Videos>>> get() = _musicContent

    private val _filmsContent = MutableLiveData<Resource<ArrayList<Videos>>>()
    val filmsContent: LiveData<Resource<ArrayList<Videos>>> get() = _filmsContent

    private val _pronunciationContent = MutableLiveData<Resource<ArrayList<ImageContent>>>()
    val pronunciationContent: LiveData<Resource<ArrayList<ImageContent>>> get() = _pronunciationContent

    private val _whiteboardContent = MutableLiveData<Resource<ArrayList<ImageContent>>>()
    val whiteboardContent: LiveData<Resource<ArrayList<ImageContent>>> get() = _whiteboardContent

    private val _quizContent = MutableLiveData<Resource<ArrayList<ImageContent>>>()
    val quizContent: LiveData<Resource<ArrayList<ImageContent>>> get() = _quizContent

    fun getMusicContent() = viewModelScope.launch {
        _musicContent.value = Resource.Loading

        repository.getMusicContentData {
            _musicContent.value = it
        }
    }

    fun getFilmsContent() = viewModelScope.launch {
        _filmsContent.value = Resource.Loading

        repository.getFilmsContentData {
            _filmsContent.value = it
        }
    }

    fun getPronunciationContent(contentType:String) = viewModelScope.launch {
        _pronunciationContent.value = Resource.Loading

        repository.getPronunciationContentData(contentType) {
            _pronunciationContent.value = it
        }
    }

    fun getWhiteboardContent() = viewModelScope.launch {
        _whiteboardContent.value = Resource.Loading

        repository.getWhiteboardContentData {
            _whiteboardContent.value = it
        }
    }

    fun getQuizContent(contentType:String) = viewModelScope.launch {
        _quizContent.value = Resource.Loading

        repository.getQuizContentData(contentType) {
            _quizContent.value = it
        }
    }

}