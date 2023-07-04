package com.graduationproject.robokidsapp.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import com.graduationproject.robokidsapp.data.model.Parent
import com.graduationproject.robokidsapp.util.Resource

interface AuthRepository {

    val currentUser: FirebaseUser?

    suspend fun registerParent(
        email: String,
        password: String,
        parent: Parent,
        result: (Resource<String>) -> Unit
    )

    suspend fun insertParent(parent: Parent, result: (Resource<String>) -> Unit)

    suspend fun loginParent(
        email: String,
        password: String,
        result: (Resource<String>) -> Unit
    )

    suspend fun forgetPassword(email: String, result: (Resource<String>) -> Unit)


    suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        context: Context ,
        result: (Resource<String>) -> Unit
    )

    fun logout(result: () -> Unit)

}
