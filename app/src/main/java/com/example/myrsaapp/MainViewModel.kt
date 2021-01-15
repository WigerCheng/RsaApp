package com.example.myrsaapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myrsaapp.tool.RSA
import com.example.myrsaapp.tool.decode64
import com.example.myrsaapp.tool.encode64

class MainViewModel : ViewModel() {
    private val _publicKey: MutableLiveData<String> = MutableLiveData()
    val publicKey: LiveData<String> = _publicKey
    private val _privateKey: MutableLiveData<String> = MutableLiveData()
    val privateKey: LiveData<String> = _privateKey

    val plainText: MutableLiveData<String> = MutableLiveData("")
    private val _cipherText: MutableLiveData<String> = MutableLiveData("")
    val cipherText: LiveData<String> = _cipherText
    private val _decryptText: MutableLiveData<String> = MutableLiveData("")
    val decryptText: LiveData<String> = _decryptText

    private val _signText: MutableLiveData<String> = MutableLiveData()
    val signText: LiveData<String> = _signText

    init {
        resetKey()
    }

    fun encrypt() {
        val cipherByte = RSA.encryptByPublicKey(plainText.value!!, publicKey.value!!)
        val cipherString = cipherByte.encode64()
        _cipherText.value = cipherString
        Log.d(TAG, "密文: $cipherString")
    }

    fun decrypt() {
        val cipherByte = cipherText.value!!.decode64()
        val decryptString = RSA.decryptByPrivateKey(cipherByte, privateKey.value!!).decodeToString()
        _decryptText.value = decryptString
    }

    fun sign() {
        val plainByte = plainText.value!!.toByteArray()
        val sign = RSA.sign(plainByte, privateKey.value!!)
        Log.d(TAG, "签名值: $sign")
        _signText.value = sign
    }

    fun verify(): Boolean {
        val plainByte = plainText.value!!.toByteArray()
        return RSA.verify(plainByte, publicKey.value!!, signText.value!!)
    }

    fun reset() {
        plainText.value = ""
        _cipherText.value = ""
        _decryptText.value = ""
        _signText.value = ""
    }

    fun resetKey() {
        with(RSA.genKeyPair()) {
            _publicKey.value = RSA.getPublicKey(this)
            _privateKey.value = RSA.getPrivateKey(this)
        }
        Log.d(TAG, "公钥: ${publicKey.value}")
        Log.d(TAG, "密钥: ${privateKey.value}")
    }

    companion object {
        const val TAG = "MainViewModel"
    }
}