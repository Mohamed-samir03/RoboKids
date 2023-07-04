package com.graduationproject.robokidsapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.graduationproject.robokidsapp.R

class NotificationAdapter(
    val context: Context,
    val listNotification: ArrayList<String>
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.custom_layout_notification_child, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notify = listNotification[position]
        holder.bind(notify)
    }

    override fun getItemCount(): Int {
        return listNotification.size
    }


    class NotificationViewHolder(itemView: View) : ViewHolder(itemView) {
        val tvNotification = itemView.findViewById<TextView>(R.id.tv_notification)!!

        fun bind(notify:String){
            tvNotification.text = notify
        }
    }
}