package com.caioops.talpp.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.caioops.talpp.adapters.MessagesAdapter
import com.caioops.talpp.databinding.ActivityMessagesBinding
import com.caioops.talpp.models.Chat
import com.caioops.talpp.models.Message
import com.caioops.talpp.models.User
import com.caioops.talpp.utils.Constants
import com.caioops.talpp.utils.showToastMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso

class MessagesActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMessagesBinding.inflate(layoutInflater)
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var listenerRegistration: ListenerRegistration
    private var receiverData:User? = null
    private var currentUserData:User? = null
    private lateinit var chatsAdapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        retrieveUserData()
        toolbarInitializer()
        clickEventsInitializer()
        recyclerViewInitializer()
        listenersInitializer()

    }

    private fun recyclerViewInitializer() {
        with(binding){
            chatsAdapter = MessagesAdapter()
            rvMessages.adapter = chatsAdapter
            rvMessages.layoutManager = LinearLayoutManager(applicationContext)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }

    private fun listenersInitializer() {
        val senderUserId = firebaseAuth.currentUser?.uid
        val receiverUserId = receiverData?.id
        if (senderUserId != null && receiverUserId != null){
            listenerRegistration = firestore
                .collection(Constants.DB_MESSAGES)
                .document(senderUserId)
                .collection(receiverUserId)
                .orderBy("date",Query.Direction.ASCENDING)
                .addSnapshotListener{ querySnapshot, error ->
                    if(error != null){
                        showToastMessage("Error retrieving messages")
                    }
                    val messagesList = mutableListOf<Message>()
                    val documents = querySnapshot?.documents


                    documents?.forEach { documentSnapshot ->
                        val message = documentSnapshot.toObject(Message::class.java)
                        if(message != null){
                            messagesList.add(message)
                        }
                    }

                    if(messagesList.isNotEmpty()){
                        chatsAdapter.addContactsList(messagesList)
                    }
                }
        }
    }

    private fun clickEventsInitializer() {
        binding.fabSend.setOnClickListener{
            val message = binding.editMessage.text.toString()
            saveMessage(message)
        }
    }

    private fun saveMessage(textMessage: String) {
        if (textMessage.isNotEmpty()){
            // TODO: replace senderUserId with currentUserData
            val senderUserId = firebaseAuth.currentUser?.uid
            val receiverUserId = receiverData?.id
            if (senderUserId != null && receiverUserId != null){
                val message = Message(
                    senderUserId, textMessage
                )

                //saving for the message sender
                saveMessageFirestore(senderUserId, receiverUserId, message)

                val senderChat = Chat(
                    senderUserId,
                    receiverUserId,
                    receiverData!!.name,
                    receiverData!!.photo,
                    textMessage
                )

                saveChatFirestore(senderChat)

                //saving for the receiver
                saveMessageFirestore(receiverUserId, senderUserId, message)

                val receiverChat = Chat(
                    receiverUserId,
                    senderUserId,
                    currentUserData!!.name,
                    currentUserData!!.photo,
                    textMessage
                )
                saveChatFirestore(receiverChat)

                binding.editMessage.setText("")
            }
        }
    }

    private fun saveChatFirestore(chat: Chat) {
        firestore
            .collection(Constants.DB_CHATS)
            .document(chat.senderUserId)
            .collection(Constants.DB_LAST_CHATS)
            .document(chat.receiverUserId)
            .set(chat)
            .addOnFailureListener{
                showToastMessage("Error saving chat")
            }
    }

    private fun saveMessageFirestore(senderUserId: String, receiverUserId: String, message: Message) {
        firestore
            .collection(Constants.DB_MESSAGES)
            .document(senderUserId)
            .collection(receiverUserId)
            .add(message)
            .addOnFailureListener{
                showToastMessage("Error sending message")
            }
    }

    private fun retrieveUserData() {
        // retrieve sender(current user) data
        val senderUserId = firebaseAuth.currentUser?.uid
        if (senderUserId != null) {
            firestore
                .collection(Constants.DB_USERS)
                .document(senderUserId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject(User::class.java)
                    if (user != null){
                        currentUserData = user
                    }
                }
        }

        // retrieve receiver data
        val extras = intent.extras
        if (extras != null){

                receiverData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras.getParcelable("receiverData", User::class.java)
                } else {
                    extras.getParcelable("receiverData")
                }
        }
    }

    private fun toolbarInitializer() {
        val toolbar = binding.tbChat
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            if (receiverData != null){
                binding.textName.text= receiverData!!.name
                if(receiverData!!.photo.isNotEmpty()){
                    Picasso.get()
                        .load(receiverData!!.photo)
                        .into(binding.imageProfilePhoto)
                }
            }
            setDisplayHomeAsUpEnabled(true)
        }

    }
}