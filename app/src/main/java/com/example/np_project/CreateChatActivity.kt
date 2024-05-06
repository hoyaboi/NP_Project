package com.example.np_project

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateChatActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var rNameEditText: TextInputEditText
    private lateinit var rIDEditText: TextInputEditText
    private lateinit var createMaterialButton: MaterialButton
    private lateinit var cancelMaterialButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_chat)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        rNameEditText = findViewById(R.id.rname_edittext)
        rIDEditText = findViewById(R.id.rID_edittext)
        createMaterialButton = findViewById(R.id.create_btn)
        cancelMaterialButton = findViewById(R.id.cancel_btn)
    }

    private fun setupListeners() {
        createMaterialButton.setOnClickListener {
            createChatRoom()
        }
        cancelMaterialButton.setOnClickListener {
            finish()
        }
    }

    private fun createChatRoom() {
        val roomName = rNameEditText.text.toString().trim()
        val roomID = rIDEditText.text.toString().trim()

        if(roomName.isNotEmpty() && roomID.isNotEmpty()) {
            val chatRoom = ChatRoom(rID = roomID, roomName = roomName, participants = hashMapOf(auth.uid!! to true))
            database.child("chatRooms").child(roomID).setValue(chatRoom)
                .addOnSuccessListener {
                    // 채팅방으로 이동하는 코드로 변경
                    startActivity(Intent(this, MainActivity::class.java))
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to create chat room. Try again", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Room name and ID must not be empty", Toast.LENGTH_SHORT).show()
        }
    }
}