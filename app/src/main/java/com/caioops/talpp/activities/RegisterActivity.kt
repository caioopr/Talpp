package com.caioops.talpp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.caioops.talpp.databinding.ActivityRegisterBinding
import com.caioops.talpp.models.User
import com.caioops.talpp.utils.showToastMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        toolbarInitializer()
        clickEventsInitializer()
    }



    private fun clickEventsInitializer() {

        binding.buttonRegister.setOnClickListener{
            if (validateFields()){
                registerUser(name, email, password)
            }
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{ result ->

                if(result.isSuccessful){
                    val userId = result.result.user?.uid
                    // saving user data in fire store
                    if (userId != null){
                        val user = User(userId,name,email)
                        saveUserFirestore(user)
                    }
                }
            }
            .addOnFailureListener{ error ->
                try {
                    throw error
                } catch (WeakPasswordError: FirebaseAuthWeakPasswordException){
                    showToastMessage("Weak password, try again with characters, numbers and special characters")
                } catch (InvalidCredentialsError: FirebaseAuthInvalidCredentialsException){
                    showToastMessage("Invalid e-mail")
                } catch (DuplicatedEmailError: FirebaseAuthInvalidUserException){
                    showToastMessage("E-mail already in use")
                }
            }
    }

    private fun saveUserFirestore(user: User) {
        firestore
            .collection("app_users")
            .document(user.id)
            .set(user)
            .addOnSuccessListener {
                showToastMessage("Registered with success!")
                startActivity(
                    Intent(applicationContext, MainActivity::class.java)
                )
            }
            .addOnFailureListener{ showToastMessage("Error during the registration") }
    }

    private fun validateFields(): Boolean {
        name = binding.editName.text.toString()
        email = binding.editEmail.text.toString()
        password = binding.editPassword.text.toString()

        if (name.isEmpty()){
            binding.textInputLayoutName.error= " Fill the name field correctly"
            return false
        }
        binding.textInputLayoutName.error = null

        if (email.isEmpty()){
            binding.textInputLayoutEmail.error = " Fill the email field correctly"
            return false
        }
        binding.textInputLayoutEmail.error = null
        if (password.isEmpty()){
            binding.textInputLayoutPassword.error = " Fill the password field correctly"
            return false
        }
        binding.textInputLayoutPassword.error = null

        return true
    }

    private fun toolbarInitializer() {
        val toolbar = binding.includeToolbar.toolbarMain
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Register"
            setDisplayHomeAsUpEnabled(true)
        }

    }
}