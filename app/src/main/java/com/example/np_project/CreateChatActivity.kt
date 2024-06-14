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

// 채팅방 생성 액티비티 클래스
class CreateChatActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var rNameEditText: TextInputEditText
    private lateinit var rIDEditText: TextInputEditText
    private lateinit var createMaterialButton: MaterialButton
    private lateinit var cancelMaterialButton: MaterialButton

    // 액티비티가 생성될 때 호출
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_chat)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // UI 컴포넌트 초기화 및 이벤트 리스너 설정
        setupViews()
        setupListeners()
    }

    // UI 컴포넌트 초기화 함수
    private fun setupViews() {
        rNameEditText = findViewById(R.id.rname_edittext)
        rIDEditText = findViewById(R.id.rID_edittext)
        createMaterialButton = findViewById(R.id.create_btn)
        cancelMaterialButton = findViewById(R.id.cancel_btn)
    }

    // 버튼 클릭 리스너 설정 함수
    private fun setupListeners() {
        createMaterialButton.setOnClickListener {
            createChatRoom()
        }
        cancelMaterialButton.setOnClickListener {
            finish()
        }
    }

    // 채팅방 생성 함수
    private fun createChatRoom() {
        val roomName = rNameEditText.text.toString().trim()
        val roomID = rIDEditText.text.toString().trim()

        // 채팅방 이름과 ID가 비어 있지 않을 경우 Firebase에 채팅방 생성 시도
        if(roomName.isNotEmpty() && roomID.isNotEmpty()) {
            val chatRoom = ChatRoom(rID = roomID, roomName = roomName, participants = hashMapOf(auth.uid!! to true))
            database.child("chatRooms").child(roomID).setValue(chatRoom)
                .addOnSuccessListener {
                    // 채팅방 생성 성공 시 메인 액티비티로 이동
                    startActivity(Intent(this, MainActivity::class.java))
                }
                .addOnFailureListener {
                    // 채팅방 생성 실패 시 토스트 메시지 표시
                    Toast.makeText(this, "Failed to create chat room. Try again", Toast.LENGTH_SHORT).show()
                }
        } else {
            // 채팅방 이름과 ID가 비어 있을 경우 토스트 메시지 표시
            Toast.makeText(this, "Room name and ID must not be empty", Toast.LENGTH_SHORT).show()
        }
    }
}
