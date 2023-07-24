package com.akshat.chatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.akshat.chatapp.databinding.ActivitySignInBinding
import com.akshat.chatapp.helpers.PLEASE_ENTER_ALL_FIELDS
import com.akshat.chatapp.helpers.SIGN_IN_SUCCESSFUL
import com.akshat.chatapp.helpers.SOMETHING_WENT_WRONG
import com.akshat.chatapp.helpers.USERS
import com.akshat.chatapp.helpers.showToast
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class SignInActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var binding: ActivitySignInBinding
    private val scope = MainScope()
    private val GOOGLE_SIGN_IN_REQUEST_CODE = 100
    private val googleSignInClient by lazy {
        GoogleSignInClient(
            applicationContext, Identity.getSignInClient(applicationContext)
        )
    }
    private val firebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
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
            startActivity(intent)
            finish()
        }
        binding.btnSignInGoogle.setOnClickListener {
            binding.signInProgress.visibility = View.VISIBLE
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
            binding.signInProgress.visibility = View.GONE
            val intent = Intent(this@SignInActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun processSignIn() {
        val password = binding.etPassword.text.toString()
        val email = binding.etEmail.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            binding.signInProgress.visibility = View.GONE
            Toast.makeText(this@SignInActivity, PLEASE_ENTER_ALL_FIELDS, Toast.LENGTH_SHORT)
                .show()
            return
        }
        binding.etEmail.text?.clear()
        binding.etPassword.text?.clear()
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            binding.signInProgress.visibility = View.GONE
            if (task.isSuccessful) {
                startMainActivity()
                showToast(this, SIGN_IN_SUCCESSFUL)
            } else {
                showToast(this,task.exception?.message.toString())
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        scope.launch {
            if (resultCode == RESULT_OK) {
                val signInRequest = data?.let { googleSignInClient.getSignInResultFromIntent(it) }
                signInRequest?.data?.let { userData ->
                    firebaseDatabase.reference.child(USERS).child(userData.userId)
                        .setValue(userData)
                }
                showToast(this@SignInActivity, SIGN_IN_SUCCESSFUL)
                startMainActivity()
            } else {
                showToast(this@SignInActivity, SOMETHING_WENT_WRONG)
            }
        }
    }
}