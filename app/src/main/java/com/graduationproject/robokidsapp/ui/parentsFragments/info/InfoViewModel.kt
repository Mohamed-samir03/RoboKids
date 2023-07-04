package com.graduationproject.robokidsapp.ui.parentsFragments.info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graduationproject.robokidsapp.data.model.Child
import com.graduationproject.robokidsapp.data.model.Parent
import com.graduationproject.robokidsapp.data.model.Report
import com.graduationproject.robokidsapp.data.repository.InfoRepository
import com.graduationproject.robokidsapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(
    val repository: InfoRepository
) : ViewModel() {

    private val _getParent = MutableLiveData<Resource<Parent>>()
    val getParent: LiveData<Resource<Parent>> get() = _getParent

    private val _updateParent = MutableLiveData<Resource<String>>()
    val updateParent: LiveData<Resource<String>> get() = _updateParent

    private val _insertChild = MutableLiveData<Resource<String>>()
    val insertChild: LiveData<Resource<String>> get() = _insertChild

    private val _getChildren = MutableLiveData<Resource<List<Child>>>()
    val getChildren: LiveData<Resource<List<Child>>> get() = _getChildren

    private val _deleteChild = MutableLiveData<Resource<Child>>()
    val deleteChild: LiveData<Resource<Child>> get() = _deleteChild

    private val _updateChild = MutableLiveData<Resource<String>>()
    val updateChild: LiveData<Resource<String>> get() = _updateChild

    private val _getReports = MutableLiveData<Resource<ArrayList<Report>>>()
    val getReports: LiveData<Resource<ArrayList<Report>>> get() = _getReports

    private val _updateReports = MutableLiveData<Resource<String>>()
    val updateReports: LiveData<Resource<String>> get() = _updateReports


    val currentParent = repository.currentUser

    fun getParentData() = viewModelScope.launch {
        _getParent.value = Resource.Loading

        repository.getParentData {
            _getParent.value = it
        }
    }

    fun updateParentInfo(parent: Parent) = viewModelScope.launch {
        _updateParent.value = Resource.Loading

        repository.updateParentInfo(parent) {
            _updateParent.value = it
        }
    }


    fun insertChild(child: Child, listReport: ArrayList<Report>) = viewModelScope.launch {
        _insertChild.value = Resource.Loading

        repository.insertChild(child, listReport) {
            _insertChild.value = it
        }
    }


    fun getChildren() = viewModelScope.launch {
        _getChildren.value = Resource.Loading

        repository.getChildren {
            _getChildren.value = it
        }
    }


    fun deleteChild(child: Child) = viewModelScope.launch {
        _deleteChild.value = Resource.Loading

        repository.deleteChild(child) {
            _deleteChild.value = it
        }
    }

    fun updateChild(child: Child) = viewModelScope.launch {
        _updateChild.value = Resource.Loading

        repository.updateChild(child) {
            _updateChild.value = it
        }
    }


    fun getReports(childId: String) = viewModelScope.launch {
        _getReports.value = Resource.Loading

        repository.getReports(childId) {
            _getReports.value = it
        }
    }


    fun updateReports(childId: String, dayId: String, report: Report) = viewModelScope.launch {
        _updateReports.value = Resource.Loading

        repository.updateReports(childId, dayId, report) {
            _updateReports.value = it
        }
    }

}