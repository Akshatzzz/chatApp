package com.akshat.chatapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.akshat.chatapp.databinding.ActivitySignUpBinding
import com.akshat.chatapp.model.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    lateinit var fireBaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialize()

        binding.btnSignUp.setOnClickListener {
            binding.signUpProgress.visibility = View.VISIBLE
            processSignUp()
        }
        binding.alreayHaveAc.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun processSignUp() {
        val name = binding.tvEnterName.text.toString()
        val password = binding.etPassword.text.toString()
        val email = binding.etEmail.text.toString()
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            binding.signUpProgress.visibility = View.GONE
            Toast.makeText(this@SignUpActivity, "Please Enter All the Fields", Toast.LENGTH_SHORT)
                .show()
            return
        }
        binding.etEmail.text?.clear()
        binding.tvEnterName.text?.clear()
        binding.etPassword.text?.clear()
        fireBaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            binding.signUpProgress.visibility = View.GONE
            if (task.isSuccessful) {
                    val user = task.result.user?.uid?.let { it1 -> UserModel(name,email,"", it1) }
                    user?.let {
                        firebaseDatabase.reference.child("User").child(user.userId).setValue(user)
                    }
                    startMainActivity()
                    Toast.makeText(
                        this@SignUpActivity, "Successfully Signed Up !!", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Couldn't sign in returning ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
    private fun startMainActivity() {
        try {
            val intent = Intent(this@SignUpActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initialize() {
        fireBaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
    }
}