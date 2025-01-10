package com.example.firebase

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase.databinding.ActivityMainBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics


class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setContentView(R.layout.activity_main)

        // Crashlytics 로그 수집 활성화
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

//        mainViewModel.fetchPosts { post ->
//            binding.textView.text = post.joinToString("\n") { post ->
//                "${post.id}: ${post.title}"
//            }
//        }

        // RecyclerView 초기화
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        postAdapter = PostAdapter(emptyList())
        binding.recyclerView.adapter = postAdapter

        // 데이터 가져오기
        mainViewModel.fetchPosts { posts ->
            postAdapter = PostAdapter(posts)
            binding.recyclerView.adapter = postAdapter
        }

    }
}
