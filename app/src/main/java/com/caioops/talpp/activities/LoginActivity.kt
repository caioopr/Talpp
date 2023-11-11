package com.caioops.talpp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.caioops.talpp.databinding.ActivityLoginBinding
import com.caioops.talpp.utils.showToastMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private lateinit var email: String
    private lateinit var password: String

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initializeClickEvents()
    }

    override fun onStart() {
        super.onStart()
        verifyCurrentUser()
    }

    private fun verifyCurrentUser() {
        val currentUser = firebaseAuth.currentUser
        if(currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun initializeClickEvents() {

        email = binding.editLoginEmail.text.toString()
        password = binding.editLoginPassword.text.toString()

        binding.textRegister.setOnClickListener{
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }

        binding.buttonLogin.setOnClickListener{
            if(validateFields(email, password)){
                logUser()
            }
        }

    }

    private fun logUser() {
        firebaseAuth
            .signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                showToastMessage("Success login!")
                startActivity(Intent(this, MainActivity::class.java))
            }
            .addOnFailureListener{error ->
                try {
                    throw error
                } catch (invalidUserError: FirebaseAuthInvalidUserException){
                    showToastMessage(" Invalid user!")
                } catch (invalidCredentialsError: FirebaseAuthInvalidCredentialsException){
                    showToastMessage(" Invalid credentials, incorrect e-mail or password!")
                }

            }
    }

    private fun validateFields( email: String, password: String): Boolean {

        if (email.isEmpty()){
            binding.textInputLayoutLoginEmail.error = " Fill the email field correctly"
            return false
        }
        binding.textInputLayoutLoginEmail.error = null
        if (password.isEmpty()){
            binding.textInputLayoutLoginPassword.error = " Fill the password field correctly"
            return false
        }
        binding.textInputLayoutLoginPassword.error = null

        return true
    }
}