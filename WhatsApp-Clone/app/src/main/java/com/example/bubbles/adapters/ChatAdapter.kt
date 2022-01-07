package com.example.bubbles.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.example.bubbles.R
import com.example.bubbles.Service
import com.example.bubbles.models.ChatMessage

class ChatAdapter(
    private val chatList: ArrayList<ChatMessage>,
    private val listener: SearchResultsAdapter.OnItemClickListener): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        val message_r: TextView = view.findViewById(R.id.tv_message_r)
        val datetime_r: TextView = view.findViewById(R.id.tv_datetime_r)
        val bubble_container_r: ConstraintLayout = view.findViewById(R.id.cl_bubble_container_r)
        val message_l: TextView = view.findViewById(R.id.tv_message_l)
        val datetime_l: TextView = view.findViewById(R.id.tv_datetime_l)
        val bubble_container_l: ConstraintLayout = view.findViewById(R.id.cl_bubble_container_l)

        init {
            bubble_container_l.setOnClickListener(this)
            bubble_container_r.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position: Int = adapterPosition
            if(Service.messageList[position].type == "location") {
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bubble, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat_message = chatList[position]
        if (chat_message.from == Service.user_id) {
            holder.message_r.setText(chat_message.message.toString())
            holder.datetime_r.setText("${chat_message.time} on ${chat_message.date}")
            holder.bubble_container_l.visibility = View.INVISIBLE
        }
        else {
            holder.message_l.setText(chat_message.message.toString())
            holder.datetime_l.setText("${chat_message.time} on ${chat_message.date}")
            holder.bubble_container_r.visibility = View.INVISIBLE
        }

    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}