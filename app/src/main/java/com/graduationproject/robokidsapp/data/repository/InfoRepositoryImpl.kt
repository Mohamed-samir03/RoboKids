package com.graduationproject.robokidsapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.graduationproject.robokidsapp.data.model.Child
import com.graduationproject.robokidsapp.data.model.Parent
import com.graduationproject.robokidsapp.data.model.Report
import com.graduationproject.robokidsapp.util.Constants.Companion.Child_TABLE
import com.graduationproject.robokidsapp.util.Constants.Companion.PARENT_TABLE
import com.graduationproject.robokidsapp.util.Constants.Companion.REPORTS_TABLE
import com.graduationproject.robokidsapp.util.Resource
import com.graduationproject.robokidsapp.util.toast
import kotlinx.coroutines.tasks.await
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class InfoRepositoryImpl(
    val auth: FirebaseAuth,
    val database: FirebaseFirestore
) : InfoRepository {

    override val currentUser: FirebaseUser? get() = auth.currentUser


    override suspend fun getParentData(result: (Resource<Parent>) -> Unit) {
        database.collection(PARENT_TABLE)
            .document(currentUser?.uid!!).addSnapshotListener { value, error ->
                if (error != null) {
                    result.invoke(Resource.Failure(error.message))
                    return@addSnapshotListener
                }

                val parent = value?.toObject<Parent>()

                result(Resource.Success(parent!!))

            }
    }

    override suspend fun updateParentInfo(parent: Parent, result: (Resource<String>) -> Unit) {
        database.collection(PARENT_TABLE)
            .document(currentUser?.uid!!).update(
                mapOf(
                    "name" to parent.name,
                    "gender" to parent.gender,
                    "countryCode" to parent.countryCode,
                    "birth_date" to parent.birth_date
                )
            ).addOnSuccessListener {
                result(Resource.Success("Data Inserted"))
            }.addOnFailureListener {
                result(Resource.Failure(it.message))
            }
    }


    override suspend fun getChildren(result: (Resource<List<Child>>) -> Unit) {
        // we will get data from firebase
        val query = database.collection(PARENT_TABLE)
            .document(currentUser?.uid!!)
            .collection(Child_TABLE)
            .orderBy("createDate", Query.Direction.ASCENDING)

        query.addSnapshotListener { value, error ->
            if (error != null) {
                result.invoke(Resource.Failure(error.message))
                return@addSnapshotListener
            }

            val listChildren = arrayListOf<Child>()
            var child: Child
            value!!.documents.forEach { document ->
                child = document.toObject(Child::class.java)!!
                listChildren.add(child)
            }
            result.invoke(Resource.Success(listChildren))
        }
    }


    override suspend fun insertChild(
        child: Child,
        listReport: ArrayList<Report>,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(PARENT_TABLE)
            .document(currentUser?.uid!!)
            .collection(Child_TABLE)
            .document()
        child.id = document.id

        try {
            document.set(child).await()

            insertReports(child.id, listReport) { resource ->
                when (resource) {
                    is Resource.Failure -> {
                        result(Resource.Failure(resource.error))
                    }
                    is Resource.Success -> {
                        result(Resource.Success("Child Data Inserted!"))
                    }
                    else -> {}
                }
            }
        } catch (ex: Exception) {
            result(Resource.Failure(ex.message))
        }
    }


    override suspend fun deleteChild(child: Child, result: (Resource<Child>) -> Unit) {
        database.collection(PARENT_TABLE)
            .document(currentUser?.uid!!)
            .collection(Child_TABLE)
            .document(child.id).delete().addOnSuccessListener {
                result(Resource.Success(child))
            }.addOnFailureListener {
                result(Resource.Failure(it.message))
            }
    }

    override suspend fun updateChild(child: Child, result: (Resource<String>) -> Unit) {
        database.collection(PARENT_TABLE)
            .document(currentUser?.uid!!)
            .collection(Child_TABLE)
            .document(child.id).set(child).addOnSuccessListener {
                result(Resource.Success("Child Updated!"))
            }.addOnFailureListener {
                result(Resource.Failure(it.message))
            }
    }


    override suspend fun insertReports(
        childId: String,
        listReport: ArrayList<Report>,
        result: (Resource<String>) -> Unit
    ) {
        for (i in 0..6) {
            val document = database.collection(PARENT_TABLE)
                .document(currentUser?.uid!!)
                .collection(Child_TABLE)
                .document(childId).collection(REPORTS_TABLE)
                .document()

            listReport[i].id = document.id

            document.set(listReport[i]).addOnSuccessListener {
                result(Resource.Success("Report Inserted!"))
            }.addOnFailureListener {
                result(Resource.Failure(it.message))
            }
        }
    }


    override suspend fun updateReports(
        childId: String,
        dayId: String,
        report: Report,
        result: (Resource<String>) -> Unit) {
        database.collection(PARENT_TABLE)
            .document(currentUser?.uid!!)
            .collection(Child_TABLE)
            .document(childId).collection(REPORTS_TABLE)
            .document(dayId).set(report).addOnSuccessListener {
                result(Resource.Success("Report Updated!"))
            }.addOnFailureListener {
                result.invoke(Resource.Failure(it.message))
            }
    }


    override suspend fun getReports(
        childId: String,
        result: (Resource<ArrayList<Report>>) -> Unit) {
        val query = database.collection(PARENT_TABLE)
            .document(currentUser?.uid!!)
            .collection(Child_TABLE)
            .document(childId).collection(REPORTS_TABLE)
            .orderBy("dayDate", Query.Direction.DESCENDING)

        query.addSnapshotListener { value, error ->
            if (error != null) {
                result.invoke(Resource.Failure(error.message))
                return@addSnapshotListener
            }

            val listReports = ArrayList<Report>()
            var report: Report
            value!!.documents.forEach { document ->
                report = document.toObject(Report::class.java)!!
                listReports.add(report)
            }

            result.invoke(Resource.Success(listReports))
        }
    }


}
