package com.example.myrsaapp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myrsaapp.databinding.FileFragmentBinding
import com.example.myrsaapp.tool.externalFileDir
import com.example.myrsaapp.ui.FileAdapter
import com.example.myrsaapp.ui.RecycleViewItemDecoration
import java.io.File

class FileFragment : Fragment() {

    private lateinit var binding: FileFragmentBinding
    private val fileAdapter = FileAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvFiles.apply {
            adapter = fileAdapter.apply {
                clickListener = {
                    findNavController().navigate(FileFragmentDirections.toDetail(it.name, it))
                }
            }
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(RecycleViewItemDecoration(10))
        }
        fileAdapter.submitList(getFileList())
        binding.tvFolder.text = "存放的文件夹：${externalFileDir(requireContext(), "RSA").path}"
    }

    private fun getFileList(): List<File> =
        externalFileDir(requireContext(), "RSA").listFiles()?.filter { it.isFile }?.toList()
            ?: emptyList()

}