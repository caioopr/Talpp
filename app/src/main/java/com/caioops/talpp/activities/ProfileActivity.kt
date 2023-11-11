package com.caioops.talpp.activities

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.caioops.talpp.databinding.ActivityProfileBinding
import com.caioops.talpp.utils.showToastMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val storage by lazy {
        FirebaseStorage.getInstance()
    }

    private var hasCamPermission = false
    private var hasGalleryPermission = false

    private val galleryManager = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){uri ->
            if(uri != null){
                binding.imageProfile.setImageURI(uri)
                uploadImageStorage(uri)
            } else {
                showToastMessage("No image selected")
            }
    }

    private fun uploadImageStorage(uri: Uri) {
        val userId = firebaseAuth.currentUser?.uid

        if (userId != null){
            storage
                .getReference("photos")
                .child("users")
                .child("id")
                .child("profile.jpg")
                .putFile(uri)
                .addOnSuccessListener {task ->
                    showToastMessage("Success image upload")
                    task.metadata?.reference?.downloadUrl?.addOnSuccessListener {url ->
                        val data = mapOf(
                            "photo" to url.toString()
                        )

                        updateProfileData(userId,data)
                    }
                }
                .addOnFailureListener {
                    showToastMessage("Error uploading the image")
                }
        }
    }

    private fun updateProfileData(userId: String, data: Map<String, String>) {
        firestore
            .collection("app_users")
            .document(userId)
            .update(data)
            .addOnSuccessListener {
                showToastMessage("User data updated")
            }
            .addOnFailureListener {
                showToastMessage("Error updating user data")
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        toolbarInitializer()
        askPermissons()
        clickEventsInitializer()
    }

    override fun onStart() {
        super.onStart()
        retrieveInitialUserData()
    }

    private fun retrieveInitialUserData() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null){
            firestore
                .collection("app_users")
                .document(userId)
                .get()
                .addOnSuccessListener {documentSnapshot ->
                    val userData = documentSnapshot.data
                    if(userData != null){
                        val name = userData["name"] as String
                        val photo = userData["photo"] as String

                        binding.editNameProfile.setText(name)
                        if(photo.isNotEmpty()){
                            Picasso.get()
                                .load(photo)
                                .into(binding.imageProfile)
                        }
                    }

                }
        }
    }

    private fun clickEventsInitializer() {
        binding.fabSelectImage.setOnClickListener{
            if(hasGalleryPermission){
                galleryManager.launch("image/*")
            } else {
                showToastMessage("Gallery access not authorized")
                askPermissons()
            }
        }

        binding.buttonUpdateProfile.setOnClickListener{
            val userName = binding.editNameProfile.text.toString()

            if(userName.isNotEmpty()){

                val userId = firebaseAuth.currentUser?.uid
                if(userId != null){
                    val data = mapOf(
                        "name" to userName
                    )
                    updateProfileData(userId, data)
                }

            }
        }
    }

    private fun askPermissons() {
        hasCamPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        hasGalleryPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        val deniedPermissionsList = mutableListOf<String>()
        if (!hasCamPermission){
            deniedPermissionsList.add(Manifest.permission.CAMERA)
        }
        if (!hasGalleryPermission){
            deniedPermissionsList.add(Manifest.permission.READ_MEDIA_IMAGES)
        }

        if(deniedPermissionsList.isNotEmpty()) {
            val permissionsManager = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                hasCamPermission = permissions[Manifest.permission.CAMERA] ?: hasCamPermission

                hasGalleryPermission = permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: hasGalleryPermission

            }
            permissionsManager.launch(deniedPermissionsList.toTypedArray())
        }
    }

    private fun toolbarInitializer() {
        val toolbar = binding.includeProfileToolbar.toolbarMain
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Profile"
            setDisplayHomeAsUpEnabled(true)
        }

    }
}