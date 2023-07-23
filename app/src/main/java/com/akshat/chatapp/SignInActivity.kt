package com.akshat.chatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.akshat.chatapp.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var binding: ActivitySignInBinding
    private val scope = MainScope()
    private val GOOGLE_SIGN_IN_REQUEST_CODE = 100
    private val googleSignInClient by lazy {
        GoogleSignInClient(
            applicationContext, Identity.getSignInClient(applicationContext)
        )
    }

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
            val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        binding.btnSignInGoogle.setOnClickListener {
            scope.launch {
                val intentSender = googleSignInClient.signIn()
                if (intentSender != null) {
                    startIntentSenderForResult(
                        intentSender, GOOGLE_SIGN_IN_REQUEST_CODE, null, 0, 0, 0
                    )
                }
            }
        }
    }

    private fun startMainActivity() {
        try {
            val intent = Intent(this@SignInActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun processSignIn() {
        val password = binding.etPassword.text.toString()
        val email = binding.etEmail.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            binding.signInProgress.visibility = View.GONE
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

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "super.onActivityResult(requestCode, resultCode, data)",
            "androidx.appcompat.app.AppCompatActivity"
        )
    )
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("AKSHAT", "onActivityResult: called")
        if (resultCode == RESULT_OK) {
            scope.launch {
                val signInResult =
                    googleSignInClient.getSignInResultFromIntent(data ?: return@launch)
                if (signInResult.data != null) {
                    val userData = googleSignInClient.getSignInResultFromIntent(data)
                    Log.d("CHEACK123", "onActivityResult: $userData")
                    Toast.makeText(this@SignInActivity, "signed in successful", Toast.LENGTH_SHORT)
                        .show()
                    startMainActivity()
                } else {
                    Toast.makeText(this@SignInActivity, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}