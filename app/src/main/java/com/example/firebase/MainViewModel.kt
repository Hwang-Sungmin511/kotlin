package com.example.firebase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebase.data.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 *  ViewModel에서 코루틴을 사용해 데이터를 가져 온다.
 *
 * */
class MainViewModel: ViewModel() {
    fun fetchPosts(onResult: (List<Post>) -> Unit) {
        viewModelScope.launch {
            try{
                val posts = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getPosts()
                }
                onResult(posts)
            }catch (e: Exception){
                throw RuntimeException("This is a test crash for Crashlytics!")
            }


        }
    }

}