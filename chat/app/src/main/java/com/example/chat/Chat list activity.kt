package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ChatListActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: ChatListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        val searchView = findViewById<SearchView>(R.id.searchView)
        val chatListRecyclerView = findViewById<RecyclerView>(R.id.chatListRecyclerView)

        db = FirebaseFirestore.getInstance()
        adapter = ChatListAdapter()
        chatListRecyclerView.adapter = adapter

        db.collection("chats").addSnapshotListener { snapshot, e ->
            if (snapshot != null && !snapshot.isEmpty) {
                val chats = snapshot.documents.map { it.getString("chatId") ?: "" }
                adapter.submitList(chats)
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }
}