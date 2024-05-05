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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class JoinChatActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var rIDEditText: TextInputEditText
    private lateinit var joinMaterialButton: MaterialButton
    private lateinit var cancelMaterialButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_join_chat)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        rIDEditText = findViewById(R.id.rID_edittext)
        joinMaterialButton = findViewById(R.id.join_btn)
        cancelMaterialButton = findViewById(R.id.cancel_btn)
    }

    private fun setupListeners() {
        joinMaterialButton.setOnClickListener {
            joinChatRoom()
        }
        cancelMaterialButton.setOnClickListener {
            finish()
        }
    }

    private fun joinChatRoom() {
        val roomID = rIDEditText.text.toString().trim()

        if(roomID.isNotEmpty()) {
            database.child("chatRooms").child(roomID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) { // 채팅방이 존재하는 경우
                        snapshot.ref.child("participants").child(auth.uid!!).setValue(true)
                            .addOnSuccessListener {
                                // 채팅방으로 이동하는 코드로 변경
                                startActivity(Intent(this@JoinChatActivity, MainActivity::class.java))
                            }
                    } else { // 채팅방이 존재하지 않는 경우
                        Toast.makeText(this@JoinChatActivity, "Chat room does not exits.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@JoinChatActivity, "Failed to read from database", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Room ID must not be empty", Toast.LENGTH_SHORT).show()
        }
    }
}