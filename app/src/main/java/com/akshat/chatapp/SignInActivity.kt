package com.akshat.chatapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.akshat.chatapp.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialize()
        if (firebaseAuth.currentUser != null) {
            startMainActivity()
        }
        binding.btnSignIn.setOnClickListener {
            binding.signInProgress.visibility = View.VISIBLE
            processSignIn()
        }
        binding.tvSignUp.setOnClickListener {
           val intent = Intent(this@SignInActivity,SignUpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun startMainActivity() {
        try {
            val intent = Intent(this@SignInActivity, MainActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun processSignIn() {
        val password = binding.etPassword.text.toString()
        val email = binding.etEmail.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this@SignInActivity, "Please Enter All the Fields", Toast.LENGTH_SHORT)
                .show()
            return
        }
        binding.etEmail.text?.clear()
        binding.etPassword.text?.clear()
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            binding.signInProgress.visibility = View.GONE
            if (task.isSuccessful) {
                startMainActivity()
                Toast.makeText(this@SignInActivity, "Sign in successful !!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(
                    this@SignInActivity, "${task.exception?.message}", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initialize() {
        firebaseAuth = FirebaseAuth.getInstance()
    }
}