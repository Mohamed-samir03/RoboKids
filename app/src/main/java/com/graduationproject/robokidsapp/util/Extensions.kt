package com.graduationproject.robokidsapp.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.io.IOException
import java.io.InputStream


fun View.hide(){
    visibility = View.GONE
}

fun View.show(){
    visibility = View.VISIBLE
}

fun View.disable(){
    isEnabled = false
}

fun View.enabled(){
    isEnabled = true
}

fun Fragment.toast(msg: String?){
    Toast.makeText(requireContext(),msg,Toast.LENGTH_LONG).show()
}


// get avatar from file assets
fun getChildAvatarFormAssets(avatarNum: Int,context: Context): Drawable? {
    val drawableName = "child_avatar/avatar$avatarNum.png"
    var istr: InputStream? = null
    try {
        istr = context.assets.open(drawableName)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return Drawable.createFromStream(istr, null)
}