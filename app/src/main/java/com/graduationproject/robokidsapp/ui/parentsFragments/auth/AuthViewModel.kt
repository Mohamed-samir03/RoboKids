package com.graduationproject.robokidsapp.ui.parentsFragments.auth

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.graduationproject.robokidsapp.data.model.Parent
import com.graduationproject.robokidsapp.data.repository.AuthRepository
import com.graduationproject.robokidsapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val repository: AuthRepository
) : ViewModel() {

    private val _register = MutableLiveData<Resource<String>>()
    val register: LiveData<Resource<String>> get() = _register

    private val _login = MutableLiveData<Resource<String>>()
    val login: LiveData<Resource<String>> get() = _login

    private val _forgotPassword = MutableLiveData<Resource<String>>()
    val forgotPassword: LiveData<Resource<String>> get() = _forgotPassword

    private val _changPassword = MutableLiveData<Resource<String>>()
    val changePassword: LiveData<Resource<String>> get() = _changPassword


    val currentUser: FirebaseUser? get() = repository.currentUser


    fun registerParent(email: String, password: String, parent: Parent) = viewModelScope.launch {
        _register.value = Resource.Loading
        repository.registerParent(email, password, parent) {
            _register.value = it
        }
    }


    fun loginParent(email: String, password: String) = viewModelScope.launch {
        _login.value = Resource.Loading
        repository.loginParent(email, password) {
            _login.value = it
        }
    }


    fun forgotPassword(email: String) = viewModelScope.launch {
        _forgotPassword.value = Resource.Loading

        repository.forgetPassword(email) {
            _forgotPassword.value = it
        }
    }


    fun changePassword(oldPassword: String, newPassword: String, context: Context) = viewModelScope.launch {
            _changPassword.value = Resource.Loading

            repository.changePassword(oldPassword, newPassword, context) {
                _changPassword.value = it
            }
        }


    fun logout(result: () -> Unit) {
        repository.logout(result)
    }

}

