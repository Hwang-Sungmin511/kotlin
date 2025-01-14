package com.example.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
class MainViewModel : ViewModel() {

    private val _notiMessage = MutableLiveData<String>()
    val notiMessage: LiveData<String> get() = _notiMessage

    fun fetchPosts(onResult: (List<Post>) -> Unit) {
        viewModelScope.launch {
            try {
                val posts = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getPosts()
                }
                onResult(posts)
                
                _notiMessage.value = "성공"
            } catch (e: Exception) {
                throw RuntimeException("This is a test crash for Crashlytics!")
                _notiMessage.value = "실패"
            }
        }
    }

}