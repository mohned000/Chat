package com.example.chatapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ChatActivity : AppCompatActivity() {
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var attachmentButton: ImageButton

    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth

    private lateinit var chatId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        messagesRecyclerView = findViewById(R.id.messagesRecyclerView)
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        attachmentButton = findViewById(R.id.attachmentButton)

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        chatId = intent.getStringExtra("chatId") ?: return

        // إعداد RecyclerView لعرض الرسائل
        val adapter = MessagesAdapter()
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messagesRecyclerView.adapter = adapter

        // جلب الرسائل من Firestore
        db.collection("messages")
            .whereEqualTo("chatId", chatId)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null && !snapshot.isEmpty) {
                    val messages = snapshot.documents.map { it.toObject(Message::class.java) }
                    adapter.submitList(messages)
                }
            }

        // إرسال رسالة نصية
        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString()
            if (messageText.isNotEmpty()) {
                val message = hashMapOf(
                    "chatId" to chatId,
                    "senderId" to auth.currentUser?.uid,
                    "content" to messageText,
                    "type" to "text",
                    "timestamp" to System.currentTimeMillis()
                )
                db.collection("messages").add(message)
                messageEditText.text.clear()
            }
        }

        // إرسال مرفق
        attachmentButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/* video/*"
            startActivityForResult(intent, 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val fileUri: Uri = data?.data ?: return
            val fileName = "attachments/${System.currentTimeMillis()}"
            val storageRef = storage.reference.child(fileName)

            storageRef.putFile(fileUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        val message = hashMapOf(
                            "chatId" to chatId,
                            "senderId" to auth.currentUser?.uid,
                            "content" to downloadUri.toString(),
                            "type" to "media",
                            "timestamp" to System.currentTimeMillis()
                        )
                        db.collection("messages").add(message)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to upload file", Toast.LENGTH_SHORT).show()
                }
        }
    }
}