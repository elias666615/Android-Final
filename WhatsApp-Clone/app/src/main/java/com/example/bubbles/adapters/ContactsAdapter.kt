package com.example.bubbles.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bubbles.R
import com.example.bubbles.models.Contact
import com.example.bubbles.adapters.SearchResultsAdapter.OnItemClickListener

class ContactsAdapter(
    private val contactList: ArrayList<Contact>,
    private val listener: OnItemClickListener,
): RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        val image: ImageView = view.findViewById(R.id.iv_profile_image)
        val name: TextView = view.findViewById(R.id.tv_name)
        val last_spoke: TextView = view.findViewById(R.id.tv_last_spoke)
        val last_message: TextView = view.findViewById(R.id.tv_last_message)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.contact, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val contactItem = contactList[position]
        viewHolder.name.setText(contactItem.name)
        viewHolder.image.setImageBitmap(contactItem.profile_image)
        if (contactItem.lastSpoke == null) {
            viewHolder.last_spoke.setText("new")
        }
        else {
            viewHolder.last_spoke.setText(viewHolder.last_spoke.toString())
        }
        if (contactItem.lastMessage == null) {
            viewHolder.last_message.setText("no messages yet")
        }
        else {
            viewHolder.last_message.setText(contactItem.lastMessage.toString())
        }
    }

    override fun getItemCount() = contactList.size
}