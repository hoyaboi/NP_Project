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

class MessageAdapter(
    private var messages: List<Message>,
    private val currentUserId: String,
    private val database: DatabaseReference
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2

    override fun getItemCount() = messages.size

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.uid == currentUserId) VIEW_TYPE_MESSAGE_SENT else VIEW_TYPE_MESSAGE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            LayoutInflater.from(parent.context).inflate(R.layout.item_message_sent, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.item_message_received, parent, false)
        }
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is MessageViewHolder) {
            holder.bind(message)

            // 사용자 이름 불러오기
            database.child("Users").child(message.uid ?: "").get().addOnSuccessListener { dataSnapshot ->
                val userName = dataSnapshot.child("name").getValue(String::class.java)
                if (getItemViewType(position) == VIEW_TYPE_MESSAGE_RECEIVED) {
                    holder.setSenderName(userName ?: "Unknown")
                }
            }

            // 마지막 아이템에 마진 추가
            val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
            if (position == itemCount - 1) {  // 마지막 아이템
                layoutParams.bottomMargin = 10.dpToPx(holder.itemView.context)  // 10dp 마진
            } else {
                layoutParams.bottomMargin = 0  // 다른 아이템은 마진 없음
            }
            holder.itemView.layoutParams = layoutParams
        }
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

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