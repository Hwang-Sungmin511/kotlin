package com.example.firebase.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class CrawlingService : Service() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())
    private val binder = LocalBinder() // 항상 반환될 바인더 객체

    inner class LocalBinder : Binder() {
        fun getService(): CrawlingService = this@CrawlingService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("CrawlingService", "Service created")
        startForegroundService()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("CrawlingService", "Service destroyed")
        coroutineScope.cancel() // 코루틴 정리
    }

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService() {
        val channelId = "crawling_service"
        val channelName = "Crawling Service"

        val notificationManager = getSystemService(NotificationManager::class.java)
        if (notificationManager == null) {
            Log.e("CrawlingService", "NotificationManager is null")
            stopSelf()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Crawling in progress")
            .setContentText("The service is running in the background")
            .setSmallIcon(android.R.drawable.ic_menu_report_image)
            .build()

        startForeground(1, notification)
    }

    fun startCrawling() {
        coroutineScope.launch {
            try {
                // 크롤링 작업 수행
                for (i in 1..10) {
                    Log.d("CrawlingService", "Crawling data... $i")
                    delay(1000) // 1초 대기
                }
                Log.d("CrawlingService", "Crawling completed")

                // 크롤링 완료 알림 표시
                showCompletionNotification()

            } catch (e: Exception) {
                Log.e("CrawlingService", "Error in crawling: ${e.message}")
            }
        }
    }

    private fun showCompletionNotification() {
        val channelId = "crawling_service_completion"
        val channelName = "Crawling Completion Service"

        val notificationManager = getSystemService(NotificationManager::class.java)
        if (notificationManager == null) {
            Log.e("CrawlingService", "NotificationManager is null")
            return
        }

        // 알림 채널 등록
        if (notificationManager.getNotificationChannel(channelId) == null) {
            val channel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Crawling Completed")
            .setContentText("All crawling tasks have been successfully completed.")
            .setSmallIcon(android.R.drawable.ic_menu_report_image)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // 알림 클릭 시 자동으로 제거
            .build()

        notificationManager.notify(2, notification) // ID 2로 알림 표시
    }
}