package com.example.np_project

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatRoomActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var roomNameToolBar: MaterialToolbar
    private lateinit var messageEditText: TextInputEditText
    private lateinit var sendButton: MaterialButton
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private var messagesList = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        // 상태표시줄 색상 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_gray)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val roomID = intent.getStringExtra("roomID") ?: return

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setupViews()
        setupListeners()
        loadRoom(roomID)
        loadMessages(roomID)
    }

    private fun setupViews() {
        roomNameToolBar = findViewById(R.id.toolbar)
        messageEditText = findViewById(R.id.msg_edittext)
        sendButton = findViewById(R.id.send_btn)
        messagesRecyclerView = findViewById(R.id.message_recycler_view)
        setSupportActionBar(roomNameToolBar)
        messageAdapter = MessageAdapter(messagesList, auth.currentUser?.uid ?: "", FirebaseDatabase.getInstance().reference)
        messagesRecyclerView.adapter = messageAdapter
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupListeners() {
        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString().trim()
            if(messageText.isNotEmpty()) {
                // 메시지 저장 로직 구현
                val message = Message(
                    uid = auth.currentUser?.uid,  // 현재 사용자의 UID
                    message = messageText,
                    timestamp = System.currentTimeMillis()
                )
                sendMessage(message)
                messageEditText.setText("")  // 메시지 전송 후 입력 필드 클리어
            }
        }
    }

    private fun loadRoom(roomID: String) {
        database.child("chatRooms").child(roomID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val roomName = snapshot.child("roomName").getValue(String::class.java)
                    roomName?.let {
                        roomNameToolBar.title = it
                    }
                } else {
                    Toast.makeText(this@ChatRoomActivity, "Room does not exits", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatRoomActivity, "Failed to load room name", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadMessages(roomID: String) {
        database.child("chatRooms").child(roomID).child("messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messagesList.clear()
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(Message::class.java)
                    message?.let { messagesList.add(it) }
                }
                messagesList.sortBy { it.timestamp }  // 시간순으로 정렬
                messageAdapter.notifyDataSetChanged()
                if (messagesList.isNotEmpty()) {
                    messagesRecyclerView.scrollToPosition(messagesList.size - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatRoomActivity, "Failed to load messages", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendMessage(message: Message) {
        val roomID = intent.getStringExtra("roomID") ?: return
        database.child("chatRooms").child(roomID).child("messages").push().setValue(message)
            .addOnFailureListener {
                // 메시지 저장 실패 시, 오류 메시지 표시
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
            }
    }
}