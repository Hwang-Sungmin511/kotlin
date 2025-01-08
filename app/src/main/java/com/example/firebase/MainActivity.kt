package com.example.firebase

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.firebase.databinding.ActivityMainBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics


class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setContentView(R.layout.activity_main)

        // Crashlytics 로그 수집 활성화
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)


        mainViewModel.fetchPosts { post ->
            binding.textView.text = post.joinToString("\n") { post ->
                "${post.id}: ${post.title}"
            }
        }

        // Crashlytics 사용자 설정 및 커스텀 로그 추가
        val crashlytics = FirebaseCrashlytics.getInstance()
        //crashlytics.setUserId("12345") // 사용자 식별자 설정
        //crashlytics.setCustomKey("example_key", "example_value") // 커스텀 키 추가


        // 강제 크래시 버튼
//        val crashButton: Button = findViewById(R.id.button_crash)
//        crashButton.setOnClickListener {
//            crashlytics.log("Crash button clicked") // 로그 추가
//            throw RuntimeException("This is a test crash for Crashlytics!") // 강제 크래시
//        }


    }
}
