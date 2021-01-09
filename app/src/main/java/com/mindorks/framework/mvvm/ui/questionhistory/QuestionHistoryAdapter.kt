package com.mindorks.framework.mvvm.ui.questionhistory

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.mindorks.framework.mvvm.data.model.others.QuestionCardData
import com.mindorks.framework.mvvm.databinding.CardLayoutBinding

class QuestionHistoryAdapter(val questionList: List<QuestionCardData>) : RecyclerView.Adapter<QuestionHistoryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.updateUi(position)
    }

    override fun getItemCount(): Int = questionList.size

    inner class ViewHolder(private val cardLayoutBinding: CardLayoutBinding) : RecyclerView.ViewHolder(cardLayoutBinding.root) {


        fun updateUi(position: Int) {
            val obj = questionList[position]
            cardLayoutBinding.questionCard = obj
            val list: List<Button> = arrayListOf(cardLayoutBinding.btnOption1, cardLayoutBinding.btnOption2, cardLayoutBinding.btnOption3, cardLayoutBinding.btnOption4)
            list.forEachIndexed { index, button ->
                if (index <= obj.options.lastIndex) {
                    if (obj.options[index].isCorrect) {
                        button.setBackgroundColor(Color.GREEN)
                    } else {
                        button.setBackgroundColor(Color.RED)
                    }
                }
            }
        }
    }
}
