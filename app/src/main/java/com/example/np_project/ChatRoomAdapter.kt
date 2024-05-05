package com.example.np_project

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

// ChatRoom 리스트를 매개변수로 받는 ChatRoomAdapter 클래스 정의
class ChatRoomAdapter(
    private var chatRooms: List<ChatRoom>,
    private val onChatRoomClicked: (ChatRoom) -> Unit
) : RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder>() {

    // 각 채팅방 아이템에 대한 뷰를 관리하는 뷰홀더 클래스
    class ChatRoomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val roomName: MaterialButton = view.findViewById(R.id.room_name)
    }

    // 새로운 뷰홀더 객체를 생성하여 반환
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        // chat_room_item.xml 레이아웃을 인플레이트하여 새로운 뷰 생성
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_room_item, parent, false)
        return ChatRoomViewHolder(view)
    }

    // 특정 위치(position)에 있는 데이터를 뷰홀더에 바인딩
    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        val chatRoom = chatRooms[position]
        holder.roomName.text = chatRoom.roomName
        holder.roomName.setOnClickListener {
            onChatRoomClicked(chatRoom)
        }
    }

    // 채팅방 리스트의 총 아이템 수 반환
    override fun getItemCount(): Int {
        return chatRooms.size
    }

    // 새로운 채팅방 목록으로 어댑터 데이터를 업데이트하고 UI를 새로고침
    @SuppressLint("NotifyDataSetChanged")
    fun updateChatRooms(newChatRooms: List<ChatRoom>) {
        chatRooms = newChatRooms
        notifyDataSetChanged()  // 데이터가 변경된 것을 어댑터에 알려 UI를 업데이트
    }
}
