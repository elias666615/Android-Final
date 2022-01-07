package com.example.bubbles.models

import java.io.File

data class User(var email: String, var name: String, var profile_image: String, var token: String, var online: Boolean = false, var contacts: ArrayList<Contact> = ArrayList())

data class UserReference(var email: String, var id: String)