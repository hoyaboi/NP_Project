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

    // ViewHolder 클래스 정의
    class ChatRoomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val roomName: TextView = view.findViewById(R.id.room_name)  // 채팅방 이름을 표시하는 TextView
        val cardView: CardView = view as CardView  // 전체 CardView
    }

    // ViewHolder 생성 시 호출
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_room, parent, false)
        return ChatRoomViewHolder(view)
    }

    // ViewHolder에 데이터를 바인딩할 때 호출
    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        val chatRoom = chatRooms[position]
        holder.roomName.text = chatRoom.roomName  // 채팅방 이름 설정
        holder.cardView.setOnClickListener {
            onChatRoomClicked(chatRoom)  // 채팅방 클릭 시 콜백 함수 호출
        }
    }

    // 아이템 개수 반환
    override fun getItemCount(): Int {
        return chatRooms.size
    }

    // 채팅방 리스트 갱신 함수
    @SuppressLint("NotifyDataSetChanged")
    fun updateChatRooms(newChatRooms: List<ChatRoom>) {
        chatRooms = newChatRooms  // 새로운 채팅방 리스트로 업데이트
        notifyDataSetChanged()  // 어댑터에 데이터 변경을 알림
    }
}
