package com.example.bubbles.models

import android.graphics.Bitmap

data class Contact(val name: String, val token: String?, val profile_image: Bitmap, val chatId: String, val unread_messages: String, val lastSpoke: String?, val lastMessage: String?)

data class addContactModel(val user_id: String, val chatId: String, val unread_messages: String = "0")
