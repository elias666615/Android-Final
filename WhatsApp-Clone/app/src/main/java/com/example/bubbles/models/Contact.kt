package com.example.bubbles.models

import android.graphics.Bitmap

data class Contact(val name: String, val profile_image: Bitmap?, val chatId: String)

data class addContactModel(val user_id: String, val chatId: String, val unread_messages: String = "0")
