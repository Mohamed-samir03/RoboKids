package com.graduationproject.robokidsapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.data.model.Child
import com.graduationproject.robokidsapp.util.getChildAvatarFormAssets

class ChildsAdapter(val context: Context, val onItemClickListener: OnItemClickListener) :
    Adapter<ChildsAdapter.ChildViewHolder>() {

    var listChild: MutableList<Child> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        return ChildViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.custom_layout_child, parent, false), onItemClickListener
        )
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val child = listChild[position]
        holder.bind(child)
    }

    override fun getItemCount(): Int {
        return listChild.size
    }


    fun updateList(list: MutableList<Child>) {
        this.listChild = list
        notifyDataSetChanged()
    }

    inner class ChildViewHolder(itemView: View, onItemClickListener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val childImage = itemView.findViewById<ImageView>(R.id.img_childs)
        val childName = itemView.findViewById<TextView>(R.id.tv_kids_name)

        init {
            itemView.setOnClickListener {
                onItemClickListener.onItemClick(listChild[adapterPosition])
            }
        }

        fun bind(child: Child) {
            childName.text = child.childName
            val drawable = getChildAvatarFormAssets(child.childAvatar, context)
            childImage.setImageDrawable(drawable)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(child: Child)
    }

}