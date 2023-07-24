package com.akshat.chatapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.akshat.chatapp.databinding.ActivitySignUpBinding
import com.akshat.chatapp.helpers.GOOGLE_SIGN_IN_REQUEST_CODE
import com.akshat.chatapp.helpers.PLEASE_ENTER_ALL_FIELDS
import com.akshat.chatapp.helpers.SIGN_IN_SUCCESSFUL
import com.akshat.chatapp.helpers.SIGN_UP_SUCCESSFULLY
import com.akshat.chatapp.helpers.SOMETHING_WENT_WRONG
import com.akshat.chatapp.helpers.USERS
import com.akshat.chatapp.helpers.showToast
import com.akshat.chatapp.model.UserModel
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var fireBaseAuth: FirebaseAuth
    private val scope = MainScope()
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
        binding.btnSignUpGoogle.setOnClickListener {
            binding.signUpProgress.visibility = View.VISIBLE
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

    private fun processSignUp() {
        val name = binding.tvEnterName.text.toString()
        val password = binding.etPassword.text.toString()
        val email = binding.etEmail.text.toString()
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            binding.signUpProgress.visibility = View.GONE
            showToast(this, PLEASE_ENTER_ALL_FIELDS)
            return
        }
        binding.etEmail.text?.clear()
        binding.tvEnterName.text?.clear()
        binding.etPassword.text?.clear()
        fireBaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            binding.signUpProgress.visibility = View.GONE
            if (task.isSuccessful) {
                val user = task.result.user?.uid?.let { it1 -> UserModel(name, email, "", it1) }
                user?.let {
                    user.userId?.let { it1 ->
                        firebaseDatabase.reference.child(USERS).child(it1).setValue(user)
                    }
                }
                startMainActivity()
                showToast(this, SIGN_UP_SUCCESSFULLY)
            } else {
                showToast(this, "${task.exception?.message}")
            }
        }
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
                showToast(this@SignUpActivity, SIGN_IN_SUCCESSFUL)
                startMainActivity()
            } else {
                binding.signUpProgress.visibility = View.GONE
                showToast(this@SignUpActivity, SOMETHING_WENT_WRONG)
            }
        }
    }

    private fun startMainActivity() {
        binding.signUpProgress.visibility = View.GONE
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
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}