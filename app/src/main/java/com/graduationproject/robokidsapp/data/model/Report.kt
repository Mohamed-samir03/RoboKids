package com.graduationproject.robokidsapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Report(
    var id: String = "",
    var dayName: String = "",
    var dayDate: Date = Date(),
    val educationalTime: String = "0",
    val entertainmentTime: String = "0",
    val totalTime: String = "0",
    val notifications: ArrayList<String> = arrayListOf()
):Parcelable

