package com.example.bubbles

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.bubbles.activities.MainActivity
import com.example.bubbles.adapters.ChatAdapter
import com.example.bubbles.adapters.ContactsAdapter
import com.example.bubbles.adapters.SearchResultsAdapter
import com.example.bubbles.models.*
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import android.provider.MediaStore
import com.example.bubbles.activities.Register
import java.io.ByteArrayOutputStream


object Service {
    val contacts: ArrayList<Contact> = ArrayList()
    val searchUsers: ArrayList<UserSearchItem> = ArrayList()
//    var user_id: String = "-Ms9Hm1VyIGb7ujmCfHj";
    var user_id: String? = null
    val database: FirebaseDatabase = Firebase.database
    val storage: StorageReference = Firebase.storage.reference
    val messageList: ArrayList<ChatMessage> = ArrayList()
    var chatId: String = ""
    var ReceiverName: String = ""
    var lat: Double = 0.0
    var long: Double = 0.0
    val allUsers: ArrayList<UserReference> = ArrayList()

    fun addUser(user: User) {
        val key = database.getReference("users").push().key
        user_id = key.toString()
        database.getReference("users").child(key!!).setValue(user).addOnFailureListener {
        }.addOnSuccessListener {
        }
        val search_key = database.getReference("search").push().key
        database.getReference("search").child(search_key!!).setValue(UserSearch(user.name, key, user.profile_image))
    }

    fun retrieveUser(id: String) {
        database.getReference("uesrs").child(id).get().addOnSuccessListener {
            println(it.value)
        }
    }

    fun uploadImage(imageUri: Uri): String {
        val formatter: SimpleDateFormat = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US)
        val now: Date = Date()
        val fileName: String = formatter.format(now)
        var success = true
        storage.child("profile_images").child(fileName).putFile(imageUri).addOnFailureListener {
            success = false
        }
        return if (success) fileName
        else ""
    }

    fun addContact(position: Int, context: Context, adapter: SearchResultsAdapter) {
        val id: String = searchUsers[position].user_id
        val name: String = searchUsers[position].name
        val key = database.getReference("chats").push().key
        database.getReference("chats").child(key!!).setValue("temp").addOnSuccessListener {
            val _Key = database.getReference("users").child(id).child("contacts").push().key
            database.getReference("users").child(id).child("contacts").child(_Key!!).setValue(addContactModel(user_id!!, key!!))
            val _key = database.getReference("users").child(user_id!!).child("contacts").push().key
                database.getReference("users").child(user_id!!).child("contacts").child(_key!!).setValue(addContactModel(id, key!!)).addOnSuccessListener {
                searchUsers.remove(searchUsers[position])
                adapter.notifyItemRemoved(position)
                Toast.makeText(context, "${name} successfully added to your contacts", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun addContactItem(data: HashMap<String, String>, adapter: ContactsAdapter, contactId: String) {
        database.getReference("users").child(data["user_id"]!!).get().addOnSuccessListener {
            val user_data: HashMap<String, String> = it.value as HashMap<String, String>
            val image_name = user_data["profile_image"]
            val imageRef = storage.child("profile_images/$image_name")
            val localFile = File.createTempFile(image_name, "jpeg")
            imageRef.getFile(localFile).addOnSuccessListener {
                val bitmap: Bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                contacts.add(Contact(user_data["name"]!!, bitmap, data["chatId"]!!))
                adapter.notifyItemInserted(contacts.size-1)
            }
        }
    }

    fun addSearchItem(data: HashMap<String, String>, adapter: SearchResultsAdapter) {
        if (data["user_id"] != user_id ) {
            val imageName = data["imageUri"]
            if(imageName != null) {
                val imageRef = storage.child("profile_images/$imageName")
                val localFile = File.createTempFile(imageName, "jpeg")
                imageRef.getFile(localFile).addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                    searchUsers.add(UserSearchItem(data["name"]!!, data["user_id"]!!, bitmap))
                    adapter.notifyDataSetChanged()
                }.addOnFailureListener {

                }
            }
            else {
                searchUsers.add(UserSearchItem(data["name"]!!, data["user_id"]!!))
                adapter.notifyDataSetChanged()
            }
        }
    }

    fun sendMessage(message: String, textInput: TextInputEditText) {
        val Timeformatter: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.US)
        val Dateformatter: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val now: Date = Date()

        val date: String = Dateformatter.format(now)
        val time: String = Timeformatter.format(now)
        val key = database.getReference("chats").child(chatId).push().key
        database.getReference("chats").child(chatId).child(key!!).setValue(ChatMessage("text", user_id!!, message, date, time)).addOnSuccessListener {
            textInput.setText("")
        }
    }

    fun sendLocation(longitude: Double, latitude: Double) {
        val Timeformatter: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.US)
        val Dateformatter: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val now: Date = Date()

        val date: String = Dateformatter.format(now)
        val time: String = Timeformatter.format(now)
        val key = database.getReference("chats").child(chatId).push().key
        database.getReference("chats").child(chatId).child(key!!).setValue(ChatMessage("location", user_id!!, "$latitude - $longitude", date, time)).addOnSuccessListener {

        }
    }

    fun addMessage(data: HashMap<String, String>, adapter: ChatAdapter) {
        messageList.add(0, ChatMessage(data["type"]!!, data["from"]!!, data["message"]!!, data["date"]!!, data["time"]!!))
        adapter.notifyItemInserted(0)
    }

    fun fetchUsers() {
        val reference = database.getReference("users")
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val user = dataSnapshot.getValue() as HashMap<String, String>
                allUsers.add(UserReference(user["email"]!!, dataSnapshot.key!!))
            }
            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        reference.addChildEventListener(childEventListener)
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }
}