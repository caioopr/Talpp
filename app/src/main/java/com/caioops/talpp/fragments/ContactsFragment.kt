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
import com.caioops.talpp.adapters.ContactsAdapter
import com.caioops.talpp.databinding.FragmentContactsBinding
import com.caioops.talpp.models.User
import com.caioops.talpp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ContactsFragment : Fragment() {

    private lateinit var binding: FragmentContactsBinding
    private lateinit var snapshotEvent : ListenerRegistration
    private lateinit var contactsAdapter: ContactsAdapter


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

        binding = FragmentContactsBinding.inflate(inflater,container,false)
        contactsAdapter = ContactsAdapter{user ->
            val intent = Intent(context, MessagesActivity::class.java)
            intent.putExtra("receiverData", user)
            intent.putExtra("origin", Constants.ORIGIN_CONTACT)
            startActivity(intent)
        }
        binding.rvContacts.adapter = contactsAdapter
        binding.rvContacts.layoutManager = LinearLayoutManager(context)
        binding.rvContacts.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )

        return binding.root

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onStart() {
        super.onStart()
        addContactsListener()
    }

    private fun addContactsListener() {
        snapshotEvent = firestore
            .collection("app_users")
            .addSnapshotListener{ querySnapshot, error ->
                val contactsList = mutableListOf<User>()

                val documents = querySnapshot?.documents
                documents?.forEach{ documentSnapshot ->
                    val user = documentSnapshot.toObject(User::class.java)
                    val currentUserId = firebaseAuth.currentUser?.uid

                    if(user != null && currentUserId != null && currentUserId != user.id){
                        contactsList.add(user)
                    }
                }

                // update recycler view contacts list
                if (contactsList.isNotEmpty()){
                    contactsAdapter.addContactsList(contactsList)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        // stop the data retrieve from firebase once the user gets out of this view
        snapshotEvent.remove()
    }
}