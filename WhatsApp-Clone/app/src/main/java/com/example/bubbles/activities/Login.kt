package com.example.bubbles.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.bubbles.R
import com.example.bubbles.Service
import com.google.android.material.textfield.TextInputEditText

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun Login(view: View) {
        val email = findViewById<TextInputEditText>(R.id.tit_email_login)
        if (email.text!!.isEmpty()) {
            val toast = Toast.makeText(applicationContext, "User ID is required", Toast.LENGTH_SHORT).show()
            return
        }
        else {
            var userExists = false
           for (user in Service.allUsers) {
               if (user.email == email.text.toString()) {
                   Service.user_id = user.id
                   userExists = true
               }
           }
            if(userExists) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            else {
                val toast = Toast.makeText(applicationContext, "User does not exist", Toast.LENGTH_SHORT).show()
            }

        }
    }
}