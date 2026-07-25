package com.example.tesladashk.utils

import android.content.Context
import android.util.Base64
import com.example.tesladashk.network.AppConfig
import com.google.gson.Gson
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object ConfigStorage {
    private const val PREF_NAME = "tesla_dash_config_secure"
    private const val KEY_DATA = "encrypted_config"
    private const val SECRET_KEY = "TeslaDashKKey123" // 16byte AES 암호키

    private fun encrypt(data: String): String {
        return try {
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(Charsets.UTF_8), "AES")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)
            val encrypted = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(encrypted, Base64.DEFAULT)
        } catch (e: Exception) {
            data
        }
    }

    private fun decrypt(encryptedData: String): String {
        return try {
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(Charsets.UTF_8), "AES")
            cipher.init(Cipher.DECRYPT_MODE, keySpec)
            val decoded = Base64.decode(encryptedData, Base64.DEFAULT)
            String(cipher.doFinal(decoded), Charsets.UTF_8)
        } catch (e: Exception) {
            encryptedData
        }
    }

    fun saveConfig(context: Context, config: AppConfig) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(config)
        val encrypted = encrypt(json)
        prefs.edit().putString(KEY_DATA, encrypted).apply()
    }

    fun loadConfig(context: Context): AppConfig {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val encrypted = prefs.getString(KEY_DATA, null) ?: return AppConfig()
        return try {
            val json = decrypt(encrypted)
            Gson().fromJson(json, AppConfig::class.java) ?: AppConfig()
        } catch (e: Exception) {
            AppConfig()
        }
    }
}
