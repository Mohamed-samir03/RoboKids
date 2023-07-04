package com.graduationproject.robokidsapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.data.model.Child
import com.graduationproject.robokidsapp.databinding.CustomLayoutReportsKidsBinding
import com.graduationproject.robokidsapp.ui.parentsFragments.info.ParentsHomeFragment
import com.graduationproject.robokidsapp.ui.parentsFragments.info.ParentsHomeFragmentDirections
import com.graduationproject.robokidsapp.util.Constants.Companion.EDIT_KIDS
import com.graduationproject.robokidsapp.util.getChildAvatarFormAssets


class ReportsKidsAdapter(
    val context: Context,
    val onItemClickListener: OnItemClickListener,
    val onDeleteClicked: (Int, Child) -> Unit
) : Adapter<ReportsKidsAdapter.ReportsViewHolder>() {

    var listChild: MutableList<Child> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportsViewHolder {
        return ReportsViewHolder(
            CustomLayoutReportsKidsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onItemClickListener
        )
    }

    override fun onBindViewHolder(holder: ReportsViewHolder, position: Int) {
        val child = listChild[position]

        holder.bind(child)

        holder.optionChild.setOnClickListener {
            showOptionKids(it, position, child)
        }

        holder.btnReports.setOnClickListener {
            val action = ParentsHomeFragmentDirections.actionParentsHomeFragmentToKidsReportsFragment(child.id)
            ParentsHomeFragment.mNavController.navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return listChild.size
    }


    inner class ReportsViewHolder(
        val binding: CustomLayoutReportsKidsBinding,
        onItemClickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                onItemClickListener.onItemClick(bindingAdapterPosition)
            }
        }

        val nameChild = binding.tvReportsKidsName
        val imageChild = binding.ivReportsKidsImage
        val optionChild = binding.ivKidsOptions
        val btnReports = binding.tvReportsKids


        fun bind(child: Child) {
            nameChild.text = child.childName
            val imageDrawable = getChildAvatarFormAssets(child.childAvatar, context)
            imageChild.setImageDrawable(imageDrawable)   // عدل يا حبيب قلبي
        }
    }


    fun updateList(list: MutableList<Child>) {
        this.listChild = list
        notifyDataSetChanged()
    }


    private fun showOptionKids(v: View, position: Int, child: Child) {
        val popup = PopupMenu(context, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.option_kids_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.editKids -> {
                    val action =
                        ParentsHomeFragmentDirections.actionParentsHomeFragmentToAddKidsFragment(
                            EDIT_KIDS,
                            child
                        )
                    ParentsHomeFragment.mNavController.navigate(action)
                }
                R.id.deleteKids -> {
                    onDeleteClicked.invoke(position, child)
                }
            }
            true
        }
        popup.show()
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}