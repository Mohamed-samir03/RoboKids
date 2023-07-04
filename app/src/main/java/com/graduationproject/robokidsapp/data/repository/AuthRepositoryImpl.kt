package com.graduationproject.robokidsapp.data.repository

import android.content.Context
import android.content.res.Resources
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.data.model.Parent
import com.graduationproject.robokidsapp.util.Constants.Companion.PARENT_TABLE
import com.graduationproject.robokidsapp.util.Resource
import kotlinx.coroutines.tasks.await


class AuthRepositoryImpl(
    val auth: FirebaseAuth,
    val database: FirebaseFirestore
) : AuthRepository {

    override val currentUser: FirebaseUser? get() = auth.currentUser

    override suspend fun registerParent(
        email: String,
        password: String,
        parent: Parent,
        result: (Resource<String>) -> Unit
    ) {
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            insertParent(parent) { resource ->
                when (resource) {
                    is Resource.Success -> {
                        result.invoke(Resource.Success("Registration successfully!"))
                    }
                    is Resource.Failure -> {
                        result.invoke(Resource.Failure(resource.error))
                    }
                    else -> {}
                }
            }
        } catch (e: Exception) {
            result.invoke(Resource.Failure(e.message))
        }
    }

    override suspend fun insertParent(parent: Parent, result: (Resource<String>) -> Unit) {
        val document = database.collection(PARENT_TABLE).document(currentUser?.uid!!)
        parent.id = currentUser?.uid!!
        document.set(parent).addOnSuccessListener {
            result.invoke(Resource.Success("user is updated"))
        }.addOnFailureListener {
            result.invoke(Resource.Failure(it.message))
        }
    }

    override suspend fun loginParent(
        email: String,
        password: String,
        result: (Resource<String>) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            result.invoke(Resource.Success("Logged in successfully!"))
        }.addOnFailureListener {
            result.invoke(Resource.Failure(it.message))
        }
    }


    override suspend fun forgetPassword(email: String, result: (Resource<String>) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnSuccessListener {
            result.invoke(Resource.Success(email))
        }.addOnFailureListener {
            result.invoke(Resource.Failure(it.message))
        }
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        context: Context ,
        result: (Resource<String>) -> Unit
    ) {
        val credential = EmailAuthProvider.getCredential(currentUser?.email!!, oldPassword)

        currentUser?.reauthenticate(credential)?.addOnSuccessListener {
            if(oldPassword != newPassword){
                currentUser?.updatePassword(newPassword)?.addOnSuccessListener {

                    result.invoke(Resource.Success(
                        context.getString(R.string.password_update)
                    ))

                    auth.signOut()

                }?.addOnFailureListener {
                    result.invoke(Resource.Failure("Failure: "+ it.message))
                }
            }else{
                result.invoke(Resource.Failure(
                    context.getString(R.string.different_password)
                ))
            }
        }?.addOnFailureListener {
            result.invoke(Resource.Failure(
                context.getString(R.string.password_match)
            ))
        }
    }


    override fun logout(result: () -> Unit) {
        auth.signOut()
        result.invoke()
    }
}




