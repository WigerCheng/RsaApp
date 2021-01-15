package com.example.myrsaapp.tool

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.io.File
import java.util.*

fun Fragment.showToast(text: String) {
    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
}


/**
 * 外部存储私有文件目录
 * @param type  Environment.DIRECTORY_DOWNLOADS
 * @see Environment.DIRECTORY_DOWNLOADS
 *
 * @return 如果type为空，则返回的是 /根目录/Android/data/包名/files
 *         否则则返回Type对应的公共文件夹
 */
fun externalFileDir(context: Context, type: String? = null): File {
    val folder = context.getExternalFilesDir(type)
    if (folder == null) {
        throw IllegalStateException("Failed to get external storage files directory")
    } else if (folder.exists()) {
        if (!folder.isDirectory) {
            throw  IllegalStateException("${folder.absolutePath} already exists and is not a directory")
        }
    } else {
        if (!folder.mkdirs()) {
            throw  IllegalStateException("Unable to create directory: ${folder.absolutePath}")
        }
    }
    return folder
}
