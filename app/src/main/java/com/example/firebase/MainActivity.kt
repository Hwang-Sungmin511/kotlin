package com.example.firebase

import UserAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase.data.User
import com.example.firebase.databinding.ActivityMainBinding
import com.example.firebase.service.CrawlingService
import com.example.firebase.ui.adapter.PostAdapter
import com.google.firebase.crashlytics.FirebaseCrashlytics


class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: UserAdapter
    private var crawlingService: CrawlingService? = null
    private var isServiceBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("hsm511", "Main -> onCreate : ")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        // RecyclerView 초기화
        adapter = UserAdapter(mutableListOf(), { user ->
            dbHelper.deleteUser(user.id)
            refreshUsers()
            Toast.makeText(this, "Deleted: ${user.name}", Toast.LENGTH_SHORT).show()
        }, { user ->
            binding.editName.setText(user.name)
            binding.buttonAdd.setOnClickListener {
                val updatedName = binding.editName.text.toString()
                if (updatedName.isNotEmpty()) {
                    dbHelper.updateUser(User(user.id, updatedName))
                    refreshUsers()
                    binding.editName.text.clear()
                    binding.buttonAdd.setOnClickListener { addUser() }
                }
            }
        })

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter


        // POST_NOTIFICATIONS 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33 이상 확인
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }

        // "Add" 버튼 클릭 이벤트
        binding.buttonAdd.setOnClickListener {
            addUser()
        }

        // 크롤링 서비스 시작 버튼
        binding.buttonStartCrawling.setOnClickListener {
            val intent = Intent(this, CrawlingService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent) // Foreground Service 시작
            } else {
                startService(intent)
            }
            // 서비스와 바인딩된 상태라면 크롤링 시작
            if (isServiceBound) {
                crawlingService?.startCrawling()
            } else {
                Toast.makeText(this, "Service is not yet bound. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
        refreshUsers()
    }

    override fun onStart() {
        super.onStart()
        Log.d("hsm511", "Main -> onStart: ")
        val intent = Intent(this, CrawlingService::class.java)
        startService(intent) // 서비스 시작
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        Log.d("hsm511", "Main -> onStop: ")
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }


    private fun addUser() {
        val name = binding.editName.text.toString()
        if (name.isNotEmpty()) {
            dbHelper.insertUser(User(0, name))
            refreshUsers()
            binding.editName.text.clear()
        } else {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun refreshUsers() {
        val users = dbHelper.getAllUsers()
        adapter.updateData(users)
    }

    // 바인딩 Service Connection
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as CrawlingService.LocalBinder
            crawlingService = binder.getService()
            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            crawlingService = null
            isServiceBound = false
        }
    }
}
