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

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var emailEditText: TextInputEditText
    private lateinit var pwdEditText: TextInputEditText
    private lateinit var nameEditText: TextInputEditText
    private lateinit var confirmButton: MaterialButton
    private lateinit var cancelButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        setupViews()

        confirmButton.setOnClickListener {
            signUp()
        }
        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun setupViews() {
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        emailEditText = findViewById(R.id.id_edittext)
        pwdEditText = findViewById(R.id.pwd_edittext)
        nameEditText = findViewById(R.id.name_edittext)
        confirmButton = findViewById(R.id.confirm_btn)
        cancelButton = findViewById(R.id.cancel_btn)
    }

    private fun signUp() {
        val email = emailEditText.text.toString().trim()
        val pwd = pwdEditText.text.toString().trim()
        val name = nameEditText.text.toString().trim()

        if(email.isNotEmpty() && pwd.isNotEmpty() && name.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if(uid != null) {
                        // 생성된 계정의 이메일과 이름을 firebase realtime database에 저장
                        addUserToDatabase(uid, email, name)
                        startActivity(Intent(this, SigninActivity::class.java))
                    }
                } else {
                    Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addUserToDatabase(uid: String, email: String, name: String) {
        val user = User(uid, email, name)
        database.child("Users").child(uid).setValue(user)
    }
}

