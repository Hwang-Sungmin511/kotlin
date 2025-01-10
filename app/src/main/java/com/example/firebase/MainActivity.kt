package com.example.firebase

import UserAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase.data.User
import com.example.firebase.databinding.ActivityMainBinding
import com.example.firebase.ui.adapter.PostAdapter
import com.google.firebase.crashlytics.FirebaseCrashlytics


class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: UserAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        // "Add" 버튼 클릭 이벤트
        binding.buttonAdd.setOnClickListener {
            addUser()
        }

        refreshUsers()
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
}
