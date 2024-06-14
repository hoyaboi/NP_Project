package com.example.np_project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 메시지 리스트를 매개변수로 받는 MessageAdapter 클래스 정의
class MessageAdapter(
    var items: List<ChatItem>,
    private val currentUserId: String,
    private val database: DatabaseReference
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2
    private val VIEW_TYPE_DATE = 3

    // 아이템 개수 반환
    override fun getItemCount() = items.size

    // 각 아이템의 뷰 타입을 반환
    override fun getItemViewType(position: Int): Int = when (val item = items[position]) {
        is ChatItem.DateItem -> VIEW_TYPE_DATE
        is ChatItem.MessageItem -> if (item.message.uid == currentUserId) VIEW_TYPE_MESSAGE_SENT else VIEW_TYPE_MESSAGE_RECEIVED
    }

    // ViewHolder 생성 시 호출
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DATE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date, parent, false)
                DateViewHolder(view)
            }
            VIEW_TYPE_MESSAGE_SENT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_sent, parent, false)
                MessageViewHolder(view)
            }
            VIEW_TYPE_MESSAGE_RECEIVED -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_received, parent, false)
                MessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    // ViewHolder에 데이터를 바인딩할 때 호출
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DateViewHolder -> holder.bind((items[position] as ChatItem.DateItem).date)
            is MessageViewHolder -> {
                val messageItem = items[position] as ChatItem.MessageItem
                holder.bind(messageItem.message)

                // 사용자 이름 불러오기
                database.child("Users").child(messageItem.message.uid ?: "").get().addOnSuccessListener { dataSnapshot ->
                    val userName = dataSnapshot.child("name").getValue(String::class.java)
                    if (getItemViewType(position) == VIEW_TYPE_MESSAGE_RECEIVED) {
                        holder.setSenderName(userName ?: "Unknown")
                    }
                }

                // 마지막 메시지에 마진 추가
                val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
                if (position == itemCount - 1) {  // 마지막 아이템
                    layoutParams.bottomMargin = 10.dpToPx(holder.itemView.context)  // 10dp 마진
                } else {
                    layoutParams.bottomMargin = 0  // 다른 아이템은 마진 없음
                }
                holder.itemView.layoutParams = layoutParams
            }
        }
    }

    // dp 단위를 px 단위로 변환하는 함수
    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    // 날짜 ViewHolder 클래스 정의
    class DateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val dateTextView: TextView = view.findViewById(R.id.date_text)
        fun bind(date: String) {
            dateTextView.text = date
        }
    }

    // 메시지 ViewHolder 클래스 정의
    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(R.id.text_message_body)
        private val dateView: TextView = view.findViewById(R.id.message_date)
        private val senderView: TextView = view.findViewById(R.id.sender)

        fun bind(message: Message) {
            textView.text = message.message
            dateView.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp ?: 0))
        }

        fun setSenderName(name: String) {
            senderView.text = name
        }
    }
}
