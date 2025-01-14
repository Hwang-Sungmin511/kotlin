package com.example.firebase

import UserAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase.data.User
import com.example.firebase.databinding.ActivityMainBinding
import com.example.firebase.service.CrawlingService
import com.example.firebase.ui.fragment.CrawlingFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: UserAdapter
    private var crawlingService: CrawlingService? = null
    private var isServiceBound = false

    private lateinit var crawlingFragment: CrawlingFragment

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

        // 프래그먼트 초기화
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (fragment is CrawlingFragment) {
            crawlingFragment = fragment
        } else {
            crawlingFragment = CrawlingFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, crawlingFragment)
                .commit()
        }

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
                    9999
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
                startCrawlingLogs()
                //crawlingService?.startCrawling(crawlingFragment)
                //crawlingFragment.addLog("Crawling started...")
            } else {
                Toast.makeText(this, "Service is not yet bound. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }

        // Stop Crawling 버튼 클릭
        binding.buttonClearCrawling.setOnClickListener {
            // CrawlingFragment 제거
            crawlingFragment.clearLogs()
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

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {

        Log.d("hsm511", "onRequestPermissionsResult : requestCode=$requestCode")

        if (requestCode == 9999) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted.", Toast.LENGTH_SHORT).show()
                Log.d("hsm511", "Notification permission granted.")
            } else {
                Toast.makeText(this, "Notification permission denied.", Toast.LENGTH_SHORT).show()
                Log.d("hsm511", "Notification permission denied.")
            }
        }
    }

    fun startCrawlingLogs(){
        val handler = Handler(mainLooper)
        val runnable = object : Runnable {
            override fun run() {
                if (isServiceBound) {
                    val logMessage = "Crawling log at ${System.currentTimeMillis()}"
                    crawlingFragment.addLog(logMessage)
                    handler.postDelayed(this, 1000)  // 1초 간격으로 추가
                }
            }
        }
        handler.post(runnable)
    }
}
