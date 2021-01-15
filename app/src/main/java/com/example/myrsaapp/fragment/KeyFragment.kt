package com.example.myrsaapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.myrsaapp.MainViewModel
import com.example.myrsaapp.R
import com.example.myrsaapp.databinding.KeyFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class KeyFragment private constructor() : BottomSheetDialogFragment() {

    private val mainViewModel by activityViewModels<MainViewModel>()
    private lateinit var binding: KeyFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.key_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = mainViewModel
            lifecycleOwner = this@KeyFragment
            btnResetKey.setOnClickListener {
                mainViewModel.resetKey()
            }
        }
    }

    companion object {
        fun newInstance() = KeyFragment()
    }
}