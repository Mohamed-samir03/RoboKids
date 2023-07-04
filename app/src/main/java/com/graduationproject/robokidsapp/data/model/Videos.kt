package com.graduationproject.robokidsapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Videos(val videoName:String ="", val videoImage:String ="" , val videoUrl:String = ""):
    Parcelable