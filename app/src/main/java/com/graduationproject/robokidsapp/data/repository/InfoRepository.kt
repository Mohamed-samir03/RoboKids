package com.graduationproject.robokidsapp.data.repository

import com.google.firebase.auth.FirebaseUser
import com.graduationproject.robokidsapp.data.model.Child
import com.graduationproject.robokidsapp.data.model.Parent
import com.graduationproject.robokidsapp.data.model.Report
import com.graduationproject.robokidsapp.util.Resource


interface InfoRepository {

    val currentUser: FirebaseUser?

    suspend fun getParentData(result: (Resource<Parent>) -> Unit)

    suspend fun updateParentInfo(parent: Parent, result: (Resource<String>) -> Unit)

    suspend fun getChildren(result: (Resource<List<Child>>) -> Unit)

    suspend fun insertChild(
        child: Child,
        listReport: ArrayList<Report>,
        result: (Resource<String>) -> Unit
    )

    suspend fun deleteChild(child: Child, result: (Resource<Child>) -> Unit)

    suspend fun updateChild(child: Child, result: (Resource<String>) -> Unit)


    suspend fun insertReports(
        childId: String,
        listReport: ArrayList<Report>,
        result: (Resource<String>) -> Unit
    )

    suspend fun updateReports(
        childId: String,
        dayId: String,
        report: Report,
        result: (Resource<String>) -> Unit
    )

    suspend fun getReports(childId: String, result: (Resource<ArrayList<Report>>) -> Unit)

}