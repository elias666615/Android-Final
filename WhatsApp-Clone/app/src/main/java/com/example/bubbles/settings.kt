package com.example.bubbles

import android.content.Context
import android.os.Bundle
import android.view.ContextMenu
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.bubbles.models.Contact
import com.example.bubbles.models.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.Serializable

class settings : Fragment() {

    lateinit var userName: String
    lateinit var nameInput: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    fun saveName() {
        if(nameInput.text.toString() != "") {
            Firebase.database.getReference("users").child(Service.user_id!!).child("name").setValue(nameInput.text.toString()).addOnSuccessListener {
            }
            Toast.makeText(this.context, "Name successfully updated", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        nameInput = view.findViewById(R.id.tit_name)

        Firebase.database.getReference("users").child(Service.user_id!!).child("name").get().addOnSuccessListener {
            nameInput.setText(it.value.toString())
        }

        val button = view.findViewById<Button>(R.id.btn_save_name)
        button.setOnClickListener {
            saveName()
        }



        return view
    }

    companion object {
    }
}