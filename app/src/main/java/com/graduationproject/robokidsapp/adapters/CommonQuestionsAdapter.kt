package com.graduationproject.robokidsapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ListAdapter
import android.widget.TextView
import com.graduationproject.robokidsapp.R

class CommonQuestionsAdapter(
    private val context: Context,
    private val questionsList: List<String>,
    private val answerList: HashMap<String, List<String>>
) : BaseExpandableListAdapter() {


    override fun getGroupCount(): Int {
        return questionsList.size
    }

    override fun getChildrenCount(p0: Int): Int {
        return this.answerList[this.questionsList[p0]]!!.size
    }

    override fun getGroup(p0: Int): Any {
        return questionsList[p0]
    }

    override fun getChild(p0: Int, p1: Int): Any {
        return this.answerList[this.questionsList[p0]]!![p1]
    }

    override fun getGroupId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getChildId(p0: Int, p1: Int): Long {
        return p1.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPostion: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView = convertView
        val questionTitle = getGroup(groupPostion) as String
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.custom_layout_question, null, false)
        }
        val textViewQuestion = convertView!!.findViewById<TextView>(R.id.tv_questions)
        textViewQuestion.text = questionTitle

        return convertView
    }

    override fun getChildView(
        groupPostion: Int,
        childPostion: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView = convertView
        val answerTitle = getChild(groupPostion , childPostion) as String
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.custom_layout_answer, null, false)
        }
        val textViewAnswer = convertView!!.findViewById<TextView>(R.id.tv_answer)
        textViewAnswer.text = answerTitle

        return convertView
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean {
        return true
    }
}