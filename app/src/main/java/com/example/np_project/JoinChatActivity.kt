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

// 채팅방 참여 액티비티 클래스
class JoinChatActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var rIDEditText: TextInputEditText
    private lateinit var joinMaterialButton: MaterialButton
    private lateinit var cancelMaterialButton: MaterialButton

    // 액티비티가 생성될 때 호출
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_join_chat)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // UI 컴포넌트 초기화 및 이벤트 리스너 설정
        setupViews()
        setupListeners()
    }

    // UI 컴포넌트 초기화 함수
    private fun setupViews() {
        rIDEditText = findViewById(R.id.rID_edittext)
        joinMaterialButton = findViewById(R.id.join_btn)
        cancelMaterialButton = findViewById(R.id.cancel_btn)
    }

    // 버튼 클릭 리스너 설정 함수
    private fun setupListeners() {
        joinMaterialButton.setOnClickListener {
            joinChatRoom()
        }
        cancelMaterialButton.setOnClickListener {
            finish()
        }
    }

    // 채팅방 참여 함수
    private fun joinChatRoom() {
        val roomID = rIDEditText.text.toString().trim()

        // 채팅방 ID가 비어 있지 않을 경우 Firebase에서 채팅방 존재 여부 확인
        if(roomID.isNotEmpty()) {
            database.child("chatRooms").child(roomID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) { // 채팅방이 존재하는 경우
                        snapshot.ref.child("participants").child(auth.uid!!).setValue(true)
                            .addOnSuccessListener {
                                // 채팅방 참여 성공 시 메인 액티비티로 이동
                                startActivity(Intent(this@JoinChatActivity, MainActivity::class.java))
                            }
                    } else { // 채팅방이 존재하지 않는 경우
                        Toast.makeText(this@JoinChatActivity, "Chat room does not exist.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // 데이터베이스 읽기 실패 시 토스트 메시지 표시
                    Toast.makeText(this@JoinChatActivity, "Failed to read from database", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // 채팅방 ID가 비어 있을 경우 토스트 메시지 표시
            Toast.makeText(this, "Room ID must not be empty", Toast.LENGTH_SHORT).show()
        }
    }
}
