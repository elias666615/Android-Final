package com.example.bubbles

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bubbles.activities.chat
import com.example.bubbles.adapters.ContactsAdapter
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.bubbles.adapters.SearchResultsAdapter.OnItemClickListener
import java.io.Serializable


class contacts : Fragment(), OnItemClickListener {

    private lateinit var userContactsReference: DatabaseReference
    private lateinit var adapter: ContactsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}


        userContactsReference = Firebase.database.getReference("users")
                                .child(Service.user_id!!).child("contacts")

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val ContactItemHash = dataSnapshot.getValue() as HashMap<String, String>
                Service.addContactItem(ContactItemHash, adapter, dataSnapshot.key!!)
            }
            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        userContactsReference.addChildEventListener(childEventListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)

        Service.contacts.clear()
        adapter = ContactsAdapter(Service.contacts, this)
        var ContactListRecyclerView: RecyclerView = view.findViewById(R.id.rv_contact_list)
        ContactListRecyclerView.setHasFixedSize(false)
        ContactListRecyclerView.layoutManager = LinearLayoutManager(this.context)
        ContactListRecyclerView.adapter = adapter

        return view
    }

    companion object {}

    override fun onItemClick(position: Int) {
        Service.chatId = Service.contacts[position].chatId
        Service.ReceiverName = Service.contacts[position].name
        val intent = Intent(this.context, chat::class.java)
        startActivity(intent)
    }
}