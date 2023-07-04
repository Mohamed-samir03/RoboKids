package com.graduationproject.robokidsapp.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.databinding.CustomLayoutChildAvatarBinding

class AdapterChildAvatar(
    val onAvatarClickListener: OnAvatarClickListener,
    val selectedAvatar: Int
) :
    RecyclerView.Adapter<AdapterChildAvatar.ChildAvatarViewHolder>() {

    var listAvatar: ArrayList<Drawable> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildAvatarViewHolder {
        val binding = CustomLayoutChildAvatarBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChildAvatarViewHolder(binding)
    }


    var selectedPosition = selectedAvatar
    var lastSelectedPosition = selectedAvatar
    override fun onBindViewHolder(holder: ChildAvatarViewHolder, position: Int) {
        val avatar = listAvatar[position]
        holder.bind(avatar)

        holder.itemView.setOnClickListener {
            lastSelectedPosition = selectedPosition
            selectedPosition = holder.bindingAdapterPosition
            notifyItemChanged(lastSelectedPosition)
            notifyItemChanged(selectedPosition)

            onAvatarClickListener.onItemClick(holder.bindingAdapterPosition)
        }

        if (selectedPosition == holder.bindingAdapterPosition) {
            holder.binding.ivChildAvatar.setBackgroundResource(R.drawable.bg_child_avatar)
        } else {
            holder.binding.ivChildAvatar.setBackgroundResource(R.drawable.bg_select_gender_default)
        }

    }

    override fun getItemCount(): Int {
        return listAvatar.size
    }


    fun updateList(list: ArrayList<Drawable>) {
        this.listAvatar = list
        notifyDataSetChanged()
    }

    inner class ChildAvatarViewHolder(val binding: CustomLayoutChildAvatarBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(drawable: Drawable) {
            binding.ivChildAvatar.setImageDrawable(drawable)
        }
    }

    interface OnAvatarClickListener {
        fun onItemClick(avatarNum: Int)
    }

}