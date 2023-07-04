package com.graduationproject.robokidsapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.data.model.Child
import com.graduationproject.robokidsapp.data.model.Videos
import com.jackandphantom.carouselrecyclerview.view.ReflectionImageView
import dagger.hilt.android.AndroidEntryPoint

class VideoAdapter(val context: Context,val storage:FirebaseStorage, val onItemClickListener: OnItemClickListener):Adapter<VideoAdapter.VideoViewHolder>() {

    var listVideos: MutableList<Videos> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_layout_videos,parent,false) , onItemClickListener)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = listVideos[position]
        holder.bind(video)
    }

    override fun getItemCount(): Int {
        return listVideos.size
    }

    fun updateList(list: MutableList<Videos>) {
        this.listVideos = list
        notifyDataSetChanged()
    }

    inner class VideoViewHolder(itemView: View, onItemClickListener: OnItemClickListener): RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onItemClickListener.onItemClick(listVideos[adapterPosition])
            }
        }

        val poster = itemView.findViewById<ReflectionImageView>(R.id.img_video)!!

        fun bind(videos: Videos){
           // poster.setImageResource(R.drawable.videos)
            //Toast.makeText(context, ""+videos.videoName, Toast.LENGTH_SHORT).show()
            if(videos.videoImage.isNotEmpty()){
                Glide.with(context)
                    .load(videos.videoImage)
                    .placeholder(R.drawable.films)
                    .into(poster)
            }

        }

    }

    interface OnItemClickListener{
        fun onItemClick(video : Videos)
    }
}