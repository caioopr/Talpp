package com.caioops.talpp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder

import com.caioops.talpp.databinding.ItemMessageReceiverBinding
import com.caioops.talpp.databinding.ItemMessageSenderBinding
import com.caioops.talpp.models.Message
import com.caioops.talpp.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class MessagesAdapter: Adapter<ViewHolder>() {

    private var messagesList = emptyList<Message>()

    fun addContactsList(list: List<Message>){
        messagesList = list
        notifyDataSetChanged()
    }

    // Sender
    class MessageSenderViewHolder(
        private val binding:ItemMessageSenderBinding
    ):ViewHolder(binding.root){

        fun bind(message: Message){
            binding.textMessageSender.text = message.message
        }
        companion object{
            fun inflateLayout(parent: ViewGroup): MessageSenderViewHolder{
                val inflater = LayoutInflater.from(parent.context)
                val itemView = ItemMessageSenderBinding.inflate(inflater,parent,false)
                return MessageSenderViewHolder(itemView)
            }
        }
    }

    // Receiver
    class MessageReceiverViewHolder(
        private val binding:ItemMessageReceiverBinding
    ):ViewHolder(binding.root){

        fun bind(message: Message){
            binding.textMessageReceiver.text = message.message
        }
        companion object {
            fun inflateLayout(parent: ViewGroup): MessageReceiverViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val itemView = ItemMessageReceiverBinding.inflate(inflater, parent, false)
                return MessageReceiverViewHolder(itemView)
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        val message = messagesList[position]
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        return if (currentUserId == message.userId) {
                (Constants.TYPE_SENDER)
            } else {
            (Constants.TYPE_RECEIVER)
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if(viewType == Constants.TYPE_SENDER){
            return MessageSenderViewHolder.inflateLayout(parent)
        }
        return MessageReceiverViewHolder.inflateLayout(parent)

    }



    override fun getItemCount(): Int {
        return messagesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messagesList[position]
        when(holder){
            is MessageSenderViewHolder -> holder.bind(message)
            is MessageReceiverViewHolder -> holder.bind(message)
        }
    }
}