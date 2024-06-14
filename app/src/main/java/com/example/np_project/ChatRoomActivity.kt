package com.example.np_project

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// 채팅방 액티비티 클래스
class ChatRoomActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var roomNameToolBar: MaterialToolbar
    private lateinit var messageEditText: TextInputEditText
    private lateinit var sendButton: MaterialButton
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var sideMenuImageButton: ImageView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var messagesList = mutableListOf<ChatItem>()

    private var roomName: String? = null
    private var roomID: String? = null

    private lateinit var headerView: View
    private lateinit var roomNameTextView: TextView
    private lateinit var roomIdTextView: TextView
    private lateinit var copyRoomIdButton: MaterialButton
    private lateinit var keyEditText: TextInputEditText
    private lateinit var saveKeyButton: MaterialButton

    // 액티비티가 생성될 때 호출
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        // 상태표시줄 색상 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_gray)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        roomID = intent.getStringExtra("roomID") ?: return

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // UI 컴포넌트 초기화 및 이벤트 리스너 설정
        setupViews()
        setupListeners()
        loadRoom()
        loadMessages()
    }

    // UI 컴포넌트 초기화 함수
    private fun setupViews() {
        roomNameToolBar = findViewById(R.id.toolbar)
        messageEditText = findViewById(R.id.msg_edittext)
        sendButton = findViewById(R.id.send_btn)
        messagesRecyclerView = findViewById(R.id.message_recycler_view)
        sideMenuImageButton = findViewById(R.id.side_menu_image)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        setSupportActionBar(roomNameToolBar)
        messageAdapter = MessageAdapter(messagesList, auth.currentUser?.uid ?: "", FirebaseDatabase.getInstance().reference)
        messagesRecyclerView.adapter = messageAdapter
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)

        headerView = navigationView.getHeaderView(0)
        roomNameTextView = headerView.findViewById(R.id.room_name_text)
        roomIdTextView = headerView.findViewById(R.id.room_id_text)
        copyRoomIdButton = headerView.findViewById(R.id.copy_room_id_button)
        keyEditText = headerView.findViewById(R.id.key_edittext)
        saveKeyButton = headerView.findViewById(R.id.save_key_button)
    }

    // 버튼 클릭 리스너 설정 함수
    private fun setupListeners() {
        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString().trim()
            if(messageText.isNotEmpty()) {
                val key = getKeyFromStorage() ?: ""
                val encryptedMessage = encryptMessage(messageText, key)

                // 메시지 저장 로직 구현
                val message = Message(
                    uid = auth.currentUser?.uid,  // 현재 사용자의 UID
                    message = encryptedMessage,
                    timestamp = System.currentTimeMillis()
                )
                sendMessage(message)
                messageEditText.setText("")  // 메시지 전송 후 입력 필드 클리어
            }
        }

        sideMenuImageButton.setOnClickListener {
            // navigation drawer 메뉴바 열기
            drawerLayout.openDrawer(GravityCompat.END)
            navigationView.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.leave_room -> {
                        AlertDialog.Builder(this)
                            .setTitle("Leave Room")
                            .setMessage("Are you sure you want to leave this room?")
                            .setPositiveButton("Yes") { dialog, which ->
                                leaveRoom()
                            }
                            .setNegativeButton("No", null)
                            .show()
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.END)
                true
            }
        }

        copyRoomIdButton.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Room ID", roomID)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Room ID copied to clipboard", Toast.LENGTH_SHORT).show()
            drawerLayout.closeDrawer(GravityCompat.END)
        }

        saveKeyButton.setOnClickListener {
            val key = keyEditText.text.toString()
            saveKeyToStorage(key)
            loadMessages()
            drawerLayout.closeDrawer(GravityCompat.END)
        }
    }

    // 채팅방 정보 로드 함수
    private fun loadRoom() {
        database.child("chatRooms").child(roomID!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    roomName = snapshot.child("roomName").getValue(String::class.java) ?: "Unknown Room"
                    roomNameToolBar.title = roomName
                    roomNameTextView.text = roomName
                    roomIdTextView.text = roomID

                    val key = getKeyFromStorage()
                    keyEditText.setText(key)
                } else {
                    Toast.makeText(this@ChatRoomActivity, "Room does not exits", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatRoomActivity, "Failed to load room name", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 메시지 로드 함수
    private fun loadMessages() {
        database.child("chatRooms").child(roomID!!).child("messages").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<ChatItem>()
                var lastTimestamp: Long? = null
                snapshot.children.forEach { messageSnapshot ->
                    val message = messageSnapshot.getValue(Message::class.java)
                    message?.let {
                        val decryptedMessageText = decryptMessage(it.message ?: "", getKeyFromStorage())
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = it.timestamp!!
                        val newDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

                        if (lastTimestamp == null || !isSameDay(lastTimestamp, it.timestamp)) {
                            tempList.add(ChatItem.DateItem(newDate))
                            lastTimestamp = it.timestamp
                        }

                        tempList.add(ChatItem.MessageItem(it.copy(message = decryptedMessageText)))
                    }
                }
                messagesList = tempList
                messageAdapter.items = messagesList
                messageAdapter.notifyDataSetChanged()
                if (messagesList.isNotEmpty()) {
                    messagesRecyclerView.scrollToPosition(messagesList.size - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // 메시지 전송 함수
    private fun sendMessage(message: Message) {
        val roomID = intent.getStringExtra("roomID") ?: return
        database.child("chatRooms").child(roomID).child("messages").push().setValue(message)
            .addOnFailureListener {
                // 메시지 저장 실패 시, 오류 메시지 표시
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
            }
    }

    // 같은 날인지 확인하는 함수
    private fun isSameDay(timestamp1: Long?, timestamp2: Long?): Boolean {
        val calendar1 = Calendar.getInstance()
        calendar1.timeInMillis = timestamp1 ?: return false
        val calendar2 = Calendar.getInstance()
        calendar2.timeInMillis = timestamp2 ?: return false
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
    }

    // 키를 저장하는 함수
    private fun saveKeyToStorage(key: String) {
        val sharedPreferences = getSharedPreferences("ChatRoomPreferences", MODE_PRIVATE)
        sharedPreferences.edit().putString("room_key_$roomID", key).apply()
        Toast.makeText(this, "Key saved successfully", Toast.LENGTH_SHORT).show()
    }

    // 저장된 키를 불러오는 함수
    private fun getKeyFromStorage(): String? {
        val sharedPreferences = getSharedPreferences("ChatRoomPreferences", MODE_PRIVATE)
        return sharedPreferences.getString("room_key_$roomID", "")
    }

    // 메시지 암호화 함수
    private fun encryptMessage(message: String, key: String?): String {
        if (key.isNullOrEmpty()) {
            return message  // 키가 없는 경우 원본 메시지 반환
        }
        val expandedKey = key.repeat((message.length / key.length) + 1)
        return message.mapIndexed { index, c ->
            c.toInt().xor(expandedKey[index].toInt()).toChar()  // 각 문자를 키의 해당 문자와 XOR 연산
        }.joinToString("")
    }

    // 메시지 복호화 함수
    private fun decryptMessage(encryptedMessage: String, key: String?): String {
        return if (key.isNullOrEmpty()) {
            encryptedMessage  // 키가 없는 경우 암호화된 메시지 반환
        } else {
            encryptMessage(encryptedMessage, key)  // 암호화와 동일한 로직으로 복호화
        }
    }

    // 채팅방 나가기 함수
    private fun leaveRoom() {
        val userUID = auth.currentUser?.uid ?: return
        // Firebase에서 현재 사용자의 UID 제거
        database.child("chatRooms").child(roomID!!).child("participants").child(userUID).removeValue()
            .addOnSuccessListener {
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to leave the room: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
