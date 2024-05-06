package com.example.np_project

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
    private lateinit var sendMaterialButton: FloatingActionButton

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
    }

    private fun setupViews() {
        roomNameToolBar = findViewById(R.id.toolbar)
        messageEditText = findViewById(R.id.msg_edittext)
        sendMaterialButton = findViewById(R.id.send_btn)
        setSupportActionBar(roomNameToolBar)
    }

    private fun setupListeners() {
        sendMaterialButton.setOnClickListener {
            val message = messageEditText.text.toString().trim()
            if(message.isNotEmpty()) {
                // 메시지 저장 로직 구현
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
}