package com.caioops.talpp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.caioops.talpp.R
import com.caioops.talpp.activities.MessagesActivity
import com.caioops.talpp.adapters.ChatsAdapter
import com.caioops.talpp.adapters.ContactsAdapter
import com.caioops.talpp.databinding.FragmentChatsBinding
import com.caioops.talpp.databinding.FragmentContactsBinding
import com.caioops.talpp.models.Chat
import com.caioops.talpp.models.User
import com.caioops.talpp.utils.Constants
import com.caioops.talpp.utils.showToastMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query


class ChatsFragment : Fragment() {

    private lateinit var binding: FragmentChatsBinding
    private lateinit var snapshotEvent : ListenerRegistration
    private lateinit var chatsAdapter: ChatsAdapter



    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatsBinding.inflate(inflater,container,false)


        chatsAdapter = ChatsAdapter { chat ->
            val intent = Intent(context, MessagesActivity::class.java)
            val user = User(
                id = chat.receiverUserId, name = chat.name, photo = chat.photo
            )
            intent.putExtra("receiverData", user)
            //intent.putExtra("origin", Constants.ORIGIN_CHAT)
            startActivity(intent)
        }
        binding.rvChats.adapter = chatsAdapter
        binding.rvChats.layoutManager = LinearLayoutManager(context)
        binding.rvChats.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )



        return binding.root
    }

    override fun onStart() {
        super.onStart()
        addChatListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        snapshotEvent.remove()
    }

    private fun addChatListener() {
        val senderUserId = firebaseAuth.currentUser?.uid
        if(senderUserId != null){
            snapshotEvent = firestore
                .collection(Constants.DB_CHATS)
                .document(senderUserId)
                .collection(Constants.DB_LAST_CHATS)
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot, error ->
                    if( error != null) {
                        activity?.showToastMessage("Error retrieving chat")
                    }

                    val chatList = mutableListOf<Chat>()
                    val documents = querySnapshot?.documents

                    documents?.forEach{ documentSnapshot ->
                        val chat = documentSnapshot.toObject(Chat::class.java)
                        if(chat != null){
                            chatList.add(chat)
                        }
                    }
                    // update adapter
                    if(chatList.isNotEmpty()){
                        chatsAdapter.addChatsList(chatList)
                    }

                }
        }
    }

}