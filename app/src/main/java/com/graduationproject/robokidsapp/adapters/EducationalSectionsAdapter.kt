package com.graduationproject.robokidsapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.data.model.EducationalSections
import pl.droidsonroids.gif.GifImageView

class EducationalSectionsAdapter (val context: Context, val listSection:ArrayList<EducationalSections>, val onItemClickListener: OnItemClickListener):
    RecyclerView.Adapter<EducationalSectionsAdapter.SectionViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        return SectionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_layout_sections, parent, false), onItemClickListener)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val section = listSection[position]
        holder.bind(section)
    }

    override fun getItemCount(): Int {
        return listSection.size
    }



    class SectionViewHolder(itemView: View, onItemClickListener: OnItemClickListener):
        RecyclerView.ViewHolder(itemView){
        init {
            itemView.setOnClickListener {
                onItemClickListener.onItemClick(adapterPosition)
            }
        }


        val imageContent = itemView.findViewById<GifImageView>(R.id.img_section)!!
        fun bind(sections: EducationalSections){
            imageContent.setImageResource(sections.sectionImage)
        }
    }


    interface OnItemClickListener{
        fun onItemClick(position:Int)
    }

}