package com.example.bubbles.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.bubbles.adapters.SearchResultsAdapter.OnItemClickListener
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bubbles.R
import com.example.bubbles.Service
import com.example.bubbles.adapters.ChatAdapter
import com.example.bubbles.services.NotificationData
import com.example.bubbles.services.PushNotification
import com.example.bubbles.services.RetrofitInstance
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class chat : AppCompatActivity(), OnItemClickListener {

    lateinit var messageInput: TextInputEditText
    lateinit var chatDbReference: DatabaseReference
    lateinit var adapter: ChatAdapter
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val TOPIC: String = "/topics/myTopic"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        FirebaseInstallations.getInstance().id.addOnSuccessListener {

        }

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        messageInput = findViewById(R.id.tit_message)

        Service.messageList.clear()
        adapter = ChatAdapter(Service.messageList, this)
        var ChatListRecyclerView: RecyclerView = findViewById(R.id.rv_message_list)
        ChatListRecyclerView.setHasFixedSize(false)
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        ChatListRecyclerView.layoutManager = linearLayoutManager
        ChatListRecyclerView.adapter = adapter

        chatDbReference = Firebase.database.getReference("chats").child(Service.chatId)

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val messageItem = dataSnapshot.getValue() as HashMap<String, String>
                Service.addMessage(messageItem, adapter)
            }
            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        }

        chatDbReference.addChildEventListener(childEventListener)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    fun checkPermissions(view: View) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
        else {
            getLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if (it == null) {
                Toast.makeText(this, "Sorry can't get location", Toast.LENGTH_SHORT).show()
            } else {
                Service.sendLocation(it.longitude, it.latitude)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if(requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    getLocation()
                }
                else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun sendMessage(view: View) {
        val message: String? = messageInput.text.toString()
        if (message != "" || message != null) {
            Service.sendMessage(message!!, messageInput)
            PushNotification(NotificationData("someone sent a message", message!!), TOPIC).also {
                sendNotification(it)
            }
        }
    }

    override fun onItemClick(position: Int) {
        val locationString: String = Service.messageList[position].message
        val latlong: List<String> = locationString.split(" - ")
        Service.lat = latlong[0].toDouble()
        Service.long = latlong[1].toDouble()
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d("MainActivity", "Response: ${Gson().toJson(response)}")
            }
            else {
                Log.e("MainActivity", response.errorBody().toString())
            }
        } catch (e: Exception) {
            Log.e("MainActivity", e.toString())
        }
    }
}