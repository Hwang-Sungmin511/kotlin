package com.example.firebase.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.databinding.ItemLogBinding

class LogAdapter(private val logs: MutableList<String>) : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {

    class LogViewHolder(private val binding: ItemLogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(log: String) {
            binding.logText.text = log
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding = ItemLogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LogViewHolder(binding)
    }

    override fun getItemCount(): Int = logs.size

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(logs[position])
    }

    fun addLog(log: String) {
        logs.add(log)
        notifyItemInserted(logs.size - 1)  // 새 로그 추가 후 리사이클러뷰 갱신
    }

    fun clearLogs() {
        logs.clear()
        notifyDataSetChanged()  // 모든 로그 삭제 후 리사이클러뷰 갱신
    }
}