package com.example.bubbles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.bubbles.adapters.SearchResultsAdapter
import android.text.Editable

import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText


class third : Fragment(), SearchResultsAdapter.OnItemClickListener {

    private lateinit var userSearchReference: DatabaseReference
    private lateinit var adapter: SearchResultsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        userSearchReference = Firebase.database.getReference("search")

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val searchItemHash = dataSnapshot.value as HashMap<String, String>
                Service.addSearchItem(searchItemHash, adapter)
            }
            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        }
        userSearchReference.addChildEventListener(childEventListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_third, container, false)
        Service.searchUsers.clear()
        adapter = SearchResultsAdapter(Service.searchUsers, this)
        var SearchListRecyclerView: RecyclerView = view.findViewById(R.id.rv_search_list)
        SearchListRecyclerView.setHasFixedSize(false)
        SearchListRecyclerView.layoutManager = LinearLayoutManager(this.context)
        SearchListRecyclerView.adapter = adapter

        val search_input: TextInputEditText = view.findViewById(R.id.tit_email_login)
        search_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Service.searchUsers.sortBy { minDistance(s.toString().lowercase(), it.name.lowercase()) }
                adapter.notifyDataSetChanged()
            }
        })

        return view
    }

    override fun onItemClick(position: Int) {
        Service.addContact(position, this.requireContext(), adapter)
    }

    fun minDistance(word1: String, word2: String): Int {
        val len1 = word1.length
        val len2 = word2.length

        val dp = Array(len1 + 1) {
            IntArray(
                len2 + 1
            )
        }
        for (i in 0..len1) {
            dp[i][0] = i
        }
        for (j in 0..len2) {
            dp[0][j] = j
        }

        for (i in 0 until len1) {
            val c1 = word1[i]
            for (j in 0 until len2) {
                val c2 = word2[j]

                if (c1 == c2) {
                    dp[i + 1][j + 1] = dp[i][j]
                } else {
                    val replace = dp[i][j] + 1
                    val insert = dp[i][j + 1] + 1
                    val delete = dp[i + 1][j] + 1
                    var min = if (replace > insert) insert else replace
                    min = if (delete > min) min else delete
                    dp[i + 1][j + 1] = min
                }
            }
        }
        return dp[len1][len2]
    }

    companion object {
    }
}