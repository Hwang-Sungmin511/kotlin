package com.example.firebase.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase.databinding.FragmentCrawlingBinding
import com.example.firebase.ui.adapter.LogAdapter

class CrawlingFragment : Fragment() {

    private var _binding: FragmentCrawlingBinding? = null
    private val binding get() = _binding!!
    private val logAdapter by lazy { LogAdapter(mutableListOf()) }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCrawlingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewLogs.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewLogs.adapter = logAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun addLog(log: String) {
        logAdapter.addLog(log)
    }

    fun clearLogs() {
        logAdapter.clearLogs()
    }
}