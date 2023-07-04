package com.graduationproject.robokidsapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.data.model.Content
import pl.droidsonroids.gif.GifImageView

class ContentAdapter(val context: Context, val listContent:ArrayList<Content>, val onItemClickListener: OnItemClickListener):
    Adapter<ContentAdapter.ContentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        return ContentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_layout_contents, parent, false), onItemClickListener)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        val content = listContent[position]
        holder.bind(content)
    }

    override fun getItemCount(): Int {
        return listContent.size
    }



    inner class ContentViewHolder(itemView: View, onItemClickListener: OnItemClickListener):ViewHolder(itemView){
        init {
            itemView.setOnClickListener {
                onItemClickListener.onItemClick(adapterPosition)
            }
        }

        val nameContent = itemView.findViewById<TextView>(R.id.tv_content_name)!!
        val imageContent = itemView.findViewById<GifImageView>(R.id.img_content)!!
        fun bind(content: Content){
            if(content.contentName == "Pronunciation"){
                nameContent.text = context.getString(R.string.Pronunciation)
            }else if(content.contentName == "Board"){
                nameContent.text = context.getString(R.string.Board)
            }else if(content.contentName == "Questions"){
                nameContent.text = context.getString(R.string.Questions)
            }else if(content.contentName == "Films"){
                nameContent.text = context.getString(R.string.Films)
            }else if(content.contentName == "Music"){
                nameContent.text = context.getString(R.string.Music)
            }else if(content.contentName == "Gaming"){
                nameContent.text = context.getString(R.string.Gaming)
            } else{
                nameContent.text = content.contentName
            }
            imageContent.setImageResource(content.contentImage)
        }

    }


    interface OnItemClickListener{
        fun onItemClick(position:Int)
    }
}