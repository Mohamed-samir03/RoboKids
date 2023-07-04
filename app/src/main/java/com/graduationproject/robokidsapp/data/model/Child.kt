package com.graduationproject.robokidsapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Child(
    var id: String = "",
    val childName: String = "",
    val childAvatar: Int = 0,
    val gender: String = "",
    val age: Int = 0,
    var childPassword: String = "",
    val createDate: Date = Date()
) : Parcelable
