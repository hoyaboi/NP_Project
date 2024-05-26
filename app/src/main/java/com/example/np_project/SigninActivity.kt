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

class SigninActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: TextInputEditText
    private lateinit var pwdEditText: TextInputEditText
    private lateinit var signInButton: MaterialButton
    private lateinit var signUpButton: MaterialButton

    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signin)

        // UI 컴포넌트 초기화, 이벤트 리스너 설정
        setupViews()

        // sign in 버튼이 눌리면
        signInButton.setOnClickListener {
            signIn()
        }
        // sign up 버튼이 눌리면
        signUpButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun setupViews() {
        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.id_edittext)
        pwdEditText = findViewById(R.id.pwd_edittext)
        signInButton = findViewById(R.id.signin_btn)
        signUpButton = findViewById(R.id.signup_btn)
    }

    private fun signIn() {
        val email = emailEditText.text.toString().trim()
        val pwd = pwdEditText.text.toString().trim()

        if(email.isNotEmpty() && pwd.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Enter your email or password", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishAffinity()
            return
        } else {
            Toast.makeText(this, "Press the back button again to close the app", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}