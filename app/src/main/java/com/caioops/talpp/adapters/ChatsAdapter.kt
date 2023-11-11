package com.caioops.talpp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.caioops.talpp.databinding.ItemChatsBinding
import com.caioops.talpp.databinding.ItemContactsBinding
import com.caioops.talpp.models.Chat
import com.caioops.talpp.models.User
import com.squareup.picasso.Picasso

class ChatsAdapter(
    private val onClick: (Chat) -> Unit
): Adapter<ChatsAdapter.ChatsViewHolder>() {
    private var chatsList = emptyList<Chat>()

    fun addChatsList(list: List<Chat>){
        chatsList = list
        notifyDataSetChanged()
    }

    inner class ChatsViewHolder(
        private val binding: ItemChatsBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(chat: Chat){
            binding.textContactName.text = chat.name
            binding.textChatMessage.text = chat.lastMessage
            if(chat.photo.isNotEmpty()) {
                Picasso.get()
                    .load(chat.photo)
                    .into(binding.imageContactPhoto)
            }
            binding.clChatItem.setOnClickListener{
                onClick(chat)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = ItemChatsBinding.inflate(inflater,parent,false)

        return ChatsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return chatsList.size
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        val chat = chatsList[position]
        holder.bind(chat)
    }

}