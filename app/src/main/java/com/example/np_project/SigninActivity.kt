package com.example.np_project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

// 로그인 액티비티 클래스
class SigninActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: TextInputEditText
    private lateinit var pwdEditText: TextInputEditText
    private lateinit var signInButton: MaterialButton
    private lateinit var signUpButton: MaterialButton

    private var backPressedTime: Long = 0

    // 액티비티가 생성될 때 호출
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signin)

        // UI 컴포넌트 초기화 및 이벤트 리스너 설정
        setupViews()

        // 로그인 버튼 클릭 시 로그인 함수 호출
        signInButton.setOnClickListener {
            signIn()
        }
        // 회원가입 버튼 클릭 시 회원가입 액티비티로 전환
        signUpButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    // UI 컴포넌트 초기화 함수
    private fun setupViews() {
        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.id_edittext)
        pwdEditText = findViewById(R.id.pwd_edittext)
        signInButton = findViewById(R.id.signin_btn)
        signUpButton = findViewById(R.id.signup_btn)
    }

    // 로그인 함수
    private fun signIn() {
        val email = emailEditText.text.toString().trim()
        val pwd = pwdEditText.text.toString().trim()

        // 이메일과 비밀번호가 비어 있지 않을 경우 Firebase 인증 시도
        if(email.isNotEmpty() && pwd.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    // 로그인 성공 시 메인 액티비티로 전환
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // 로그인 실패 시 토스트 메시지 표시
                    Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // 이메일 또는 비밀번호가 비어 있을 경우 토스트 메시지 표시
            Toast.makeText(this, "Enter your email or password", Toast.LENGTH_SHORT).show()
        }
    }

    // 백 버튼 두 번 클릭 시 앱 종료
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishAffinity() // 앱 종료
            return
        } else {
            // 백 버튼 한 번 클릭 시 토스트 메시지 표시
            Toast.makeText(this, "Press the back button again to close the app", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}
