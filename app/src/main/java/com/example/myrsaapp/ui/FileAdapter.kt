package com.example.myrsaapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myrsaapp.R
import com.example.myrsaapp.databinding.FileItemBinding
import java.io.File

class FileAdapter : ListAdapter<File, FileAdapter.FileViewHolder>(DIFF_CALLBACK) {

    var clickListener: ((File) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val inflater = parent.context.getSystemService<LayoutInflater>()!!
        val binding =
            DataBindingUtil.inflate<FileItemBinding>(inflater, R.layout.file_item, parent, false)
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = getItem(position)
        holder.binding.fileName = file.name
        holder.binding.root.setOnClickListener {
            clickListener?.invoke(file)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<File>() {
            override fun areItemsTheSame(oldItem: File, newItem: File): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: File, newItem: File): Boolean =
                oldItem == newItem
        }
    }

    inner class FileViewHolder(val binding: FileItemBinding) : RecyclerView.ViewHolder(binding.root)
}