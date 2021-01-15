package com.example.myrsaapp.tool

import java.io.ByteArrayOutputStream
import java.security.Key
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.Signature
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException

object RSA {

    private const val KEY_ALGORITHM = "RSA"                 //加密算法RSA
    private const val SIGNATURE_ALGORITHM = "MD5withRSA"    //签名算法
    private const val PUBLIC_KEY = "RSAPublicKey"           //获取公钥的key
    private const val PRIVATE_KEY = "RSAPrivateKey"         //获取私钥的key
    private const val MAX_ENCRYPT_BLOCK = 117               //RSA最大加密明文大小
    private const val MAX_DECRYPT_BLOCK = 128               //RSA最大解密密文大小

    /**
     * 生成密钥对(公钥和私钥)
     */
    @Throws(Exception::class)
    fun genKeyPair(): Map<String, Any> {
        val keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM)
        keyPairGen.initialize(1024)
        val keyPair = keyPairGen.generateKeyPair()
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey
        val keyMap = HashMap<String, Any>(2)
        keyMap[PUBLIC_KEY] = publicKey
        keyMap[PRIVATE_KEY] = privateKey
        return keyMap
    }


    /**
     * 用私钥对信息生成数字签名
     *
     * @param data       已加密数据
     * @param privateKey 私钥(BASE64编码)
     */
    @Throws(Exception::class)
    fun sign(data: ByteArray, privateKey: String): String {
        val keyBytes = privateKey.decode64()
        val pkcs8KeySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val privateK = keyFactory.generatePrivate(pkcs8KeySpec)
        val signature = Signature.getInstance(SIGNATURE_ALGORITHM)
        signature.initSign(privateK)
        signature.update(data)
        return signature.sign().encode64()
    }

    /**
     * 校验数字签名
     *
     * @param data      已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign      数字签名
     */
    @Throws(Exception::class)
    fun verify(data: ByteArray, publicKey: String, sign: String): Boolean {
        val keyBytes = publicKey.decode64()
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val publicK = keyFactory.generatePublic(keySpec)
        val signature = Signature.getInstance(SIGNATURE_ALGORITHM)
        signature.initVerify(publicK)
        signature.update(data)
        return signature.verify(sign.decode64())
    }

    /**
     * 私钥解密
     *
     * @param encryptedData 已加密数据
     * @param privateKey    私钥(BASE64编码)
     */
    @Throws(Exception::class)
    fun decryptByPrivateKey(encryptedData: ByteArray, privateKey: String): ByteArray {
        val keyBytes = privateKey.decode64()
        val pkcs8KeySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val privateK = keyFactory.generatePrivate(pkcs8KeySpec)
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateK)
        return doFinal(encryptedData, cipher, false)
    }

    /**
     * 公钥加密
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     */
    @Throws(Exception::class)
    fun encryptByPublicKey(data: String, publicKey: String): ByteArray {
        val keyBytes = publicKey.decode64()
        val x509KeySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val publicK = keyFactory.generatePublic(x509KeySpec)
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicK)
        return doFinal(data.encodeToByteArray(), cipher, true)
    }

    /**
     * 获取私钥
     * @param keyMap 密钥对
     */
    @Throws(Exception::class)
    fun getPrivateKey(keyMap: Map<String, Any>): String {
        val key = keyMap[PRIVATE_KEY] as Key
        return key.encoded.encode64()
    }

    /**
     * 获取公钥
     * @param keyMap 密钥对
     */
    @Throws(Exception::class)
    fun getPublicKey(keyMap: Map<String, Any>): String {
        val key = keyMap[PUBLIC_KEY] as Key
        return key.encoded.encode64()
    }

    @Throws(
        BadPaddingException::class,
        IllegalBlockSizeException::class,
        java.io.IOException::class
    )
    private fun doFinal(
        encryptedData: ByteArray,
        cipher: Cipher,
        isEncryptMode: Boolean
    ): ByteArray {
        val inputLen = encryptedData.size
        ByteArrayOutputStream().use { out ->
            var offSet = 0
            var cache: ByteArray
            var i = 0
            val block = if (isEncryptMode) MAX_ENCRYPT_BLOCK else MAX_DECRYPT_BLOCK
            while (inputLen - offSet > 0) {
                cache = if (inputLen - offSet > block) {
                    cipher.doFinal(encryptedData, offSet, block)
                } else {
                    cipher.doFinal(encryptedData, offSet, inputLen - offSet)
                }
                out.write(cache, 0, cache.size)
                i++
                offSet = i * block
            }
            return out.toByteArray()
        }
    }
}

@Throws(Exception::class)
fun String.decode64(): ByteArray = Base64.getDecoder().decode(this.toByteArray())

@Throws(Exception::class)
fun ByteArray.encode64(): String = Base64.getEncoder().encodeToString(this)
