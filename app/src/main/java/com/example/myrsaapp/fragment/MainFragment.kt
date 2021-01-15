package com.example.myrsaapp.fragment

import android.os.Bundle
import android.view.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.myrsaapp.MainViewModel
import com.example.myrsaapp.R
import com.example.myrsaapp.databinding.MainFragmentBinding
import com.example.myrsaapp.tool.externalFileDir
import com.example.myrsaapp.tool.showToast
import java.io.File
import java.util.*

class MainFragment : Fragment() {

    private lateinit var binding: MainFragmentBinding
    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_app, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_clear -> {
                mainViewModel.reset()
                true
            }
            R.id.menu_file -> {
                findNavController().navigate(MainFragmentDirections.toFile())
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun initView() {
        binding.apply {
            viewModel = mainViewModel
            lifecycleOwner = this@MainFragment
            fabKey.setOnClickListener {
                KeyFragment.newInstance().let {
                    it.show(childFragmentManager, it.toString())
                }
            }
            btnSign.setOnClickListener {
                checkPlainEmptyAndRun {
                    mainViewModel.sign()
                }
            }
            btnVerify.setOnClickListener {
                checkPlainEmptyAndRun {
                    val result = mainViewModel.verify()
                    if (result) {
                        showToast("验签成功")
                    } else {
                        showToast("验签失败")
                    }
                }
            }
            btnEncryption.setOnClickListener {
                checkPlainEmptyAndRun {
                    mainViewModel.encrypt()
                    save()
                    showToast("文件已保存")
                }
            }
            btnDecrypt.setOnClickListener {
                checkPlainEmptyAndRun {
                    mainViewModel.decrypt()
                }
            }
            tilPlain.editText?.addTextChangedListener {
                tilPlain.error = ""
            }
        }
    }

    private fun save() {
        val fileName = "${mainViewModel.plainText.value!!}_${Date().time}.txt"
        with(
            File(
                externalFileDir(requireContext(), "RSA"),
                fileName
            )
        ) {
            writeText(
                """
            —————明文—————
            ${mainViewModel.plainText.value!!}
            
            
            —————密文—————
            ${mainViewModel.cipherText.value!!}
            """.trimIndent()
            )
        }
    }

    private fun checkPlainEmptyAndRun(method: () -> Unit) {
        val plainText = mainViewModel.plainText.value!!
        if (plainText.isNotBlank()) {
            method.invoke()
        } else {
            binding.tilPlain.error = "你还没输入明文"
            binding.nsvMain.scrollTo(0, 0)
        }
    }
}