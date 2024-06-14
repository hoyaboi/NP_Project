package com.example.np_project

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

// 회원가입 액티비티 클래스
class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var emailEditText: TextInputEditText
    private lateinit var pwdEditText: TextInputEditText
    private lateinit var nameEditText: TextInputEditText
    private lateinit var confirmButton: MaterialButton
    private lateinit var cancelButton: MaterialButton

    // 액티비티가 생성될 때 호출
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        // UI 컴포넌트 초기화 및 이벤트 리스너 설정
        setupViews()

        // 확인 버튼 클릭 시 회원가입 함수 호출
        confirmButton.setOnClickListener {
            signUp()
        }
        // 취소 버튼 클릭 시 액티비티 종료
        cancelButton.setOnClickListener {
            finish()
        }
    }

    // UI 컴포넌트 초기화 함수
    private fun setupViews() {
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        emailEditText = findViewById(R.id.id_edittext)
        pwdEditText = findViewById(R.id.pwd_edittext)
        nameEditText = findViewById(R.id.name_edittext)
        confirmButton = findViewById(R.id.confirm_btn)
        cancelButton = findViewById(R.id.cancel_btn)
    }

    // 회원가입 함수
    private fun signUp() {
        val email = emailEditText.text.toString().trim()
        val pwd = pwdEditText.text.toString().trim()
        val name = nameEditText.text.toString().trim()

        // 이메일, 비밀번호, 이름이 비어 있지 않을 경우 Firebase에 계정 생성 시도
        if(email.isNotEmpty() && pwd.isNotEmpty() && name.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if(uid != null) {
                        // 생성된 계정의 이메일과 이름을 Firebase Realtime Database에 저장
                        addUserToDatabase(uid, email, name)
                        // 회원가입 성공 시 로그인 액티비티로 전환
                        startActivity(Intent(this, SigninActivity::class.java))
                    }
                } else {
                    // 회원가입 실패 시 토스트 메시지 표시
                    Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // 필드가 비어 있을 경우 토스트 메시지 표시
            Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    // Firebase Realtime Database에 사용자 정보 저장
    private fun addUserToDatabase(uid: String, email: String, name: String) {
        val user = User(uid, email, name)
        database.child("Users").child(uid).setValue(user)
    }
}
