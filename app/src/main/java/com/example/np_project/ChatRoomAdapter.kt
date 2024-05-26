package com.example.np_project

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

// ChatRoom 리스트를 매개변수로 받는 ChatRoomAdapter 클래스 정의
class ChatRoomAdapter(
    private var chatRooms: List<ChatRoom>,
    private val onChatRoomClicked: (ChatRoom) -> Unit
) : RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder>() {

    class ChatRoomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val roomName: TextView = view.findViewById(R.id.room_name)  // TextView 참조
        val cardView: CardView = view as CardView  // CardView 참조
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_room, parent, false)
        return ChatRoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        val chatRoom = chatRooms[position]
        holder.roomName.text = chatRoom.roomName
        holder.cardView.setOnClickListener {
            onChatRoomClicked(chatRoom)
        }
    }

    override fun getItemCount(): Int {
        return chatRooms.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateChatRooms(newChatRooms: List<ChatRoom>) {
        chatRooms = newChatRooms
        notifyDataSetChanged()
    }
}
