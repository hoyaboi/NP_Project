package com.example.np_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var chatRoomsRecyclerView: RecyclerView
    private lateinit var chatRoomAdapter: ChatRoomAdapter
    private lateinit var joinFloatingButton: ExtendedFloatingActionButton
    private lateinit var createFloatingButton: ExtendedFloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 상태표시줄 색상 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_gray)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setupViews()
        setupListeners()
        loadChatRooms()
    }

    private fun setupViews() {
        joinFloatingButton = findViewById(R.id.join_action_btn)
        createFloatingButton = findViewById(R.id.add_action_btn)
        chatRoomsRecyclerView = findViewById(R.id.chat_rooms_recycler_view)
        chatRoomAdapter = ChatRoomAdapter(emptyList()) { chatRoom ->
            Log.d("MainActivity", "Room ID: ${chatRoom.rID}") // 로그로 ID 확인
            val intent = Intent(this, ChatRoomActivity::class.java)
            intent.putExtra("roomID", chatRoom.rID)
            startActivity(intent)
        }
        chatRoomsRecyclerView.adapter = chatRoomAdapter
        chatRoomsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupListeners() {
        joinFloatingButton.setOnClickListener {
            startActivity(Intent(this, JoinChatActivity::class.java))
        }
        createFloatingButton.setOnClickListener {
            startActivity(Intent(this, CreateChatActivity::class.java))
        }
    }

    private fun loadChatRooms() {
        val curUserUid = auth.currentUser?.uid
        database.child("chatRooms").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatRooms = mutableListOf<ChatRoom>()
                for (data in snapshot.children) {
                    val chatRoom = data.getValue(ChatRoom::class.java)
                    chatRoom?.let {
                        if(it.participants[curUserUid] == true) {
                            chatRooms.add(it)
                        }
                    }
                }
                chatRoomAdapter.updateChatRooms(chatRooms)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load chat rooms", Toast.LENGTH_SHORT).show()
            }
        })
    }

}

