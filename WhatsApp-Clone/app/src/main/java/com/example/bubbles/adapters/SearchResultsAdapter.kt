package com.example.bubbles.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.bubbles.ChangeImageDialogFragment
import com.example.bubbles.R
import com.example.bubbles.Service
import com.example.bubbles.models.UserSearchItem

class SearchResultsAdapter(
    private val dataSet: ArrayList<UserSearchItem>,
    private val listener: OnItemClickListener,
):
    RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        val image: ImageView = view.findViewById(R.id.iv_profile_image)
        val name: TextView = view.findViewById(R.id.tv_search_name)
        val add_button: Button = view.findViewById(R.id.btn_add)
        init {
            add_button.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.user_search_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchItem = dataSet[position]
        holder.name.setText(searchItem.name)
        if (searchItem.imageBitmap != null) holder.image.setImageBitmap(searchItem.imageBitmap)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}