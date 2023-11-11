package com.caioops.talpp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.caioops.talpp.databinding.ItemContactsBinding
import com.caioops.talpp.models.User
import com.squareup.picasso.Picasso

class ContactsAdapter(
    private val onClick: (User) -> Unit
): Adapter<ContactsAdapter.ContactsViewHolder>(){

    private var contactsList = emptyList<User>()

    fun addContactsList(list: List<User>){
        contactsList = list
        notifyDataSetChanged()
    }

    inner class ContactsViewHolder(
        private val binding: ItemContactsBinding
    ): ViewHolder(binding.root){
        fun bind(user: User){
            binding.textContactName.text = user.name
            if(user.photo.isNotEmpty()) {
                Picasso.get()
                    .load(user.photo)
                    .into(binding.imageContactPhoto)
            }
            binding.clContactItem.setOnClickListener{
                onClick(user)
            }
        }

    }

    // adapter implementation
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = ItemContactsBinding.inflate(inflater,parent,false)
        
        return ContactsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val user = contactsList[position]
        holder.bind(user)
    }
}