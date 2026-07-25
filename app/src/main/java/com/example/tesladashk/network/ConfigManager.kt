package com.example.tesladashk.network

import android.content.Context
import com.google.gson.Gson
import java.io.File

object ConfigManager {
    private const val FILE_NAME = "tesla_config.json"

    fun loadConfig(context: Context): AppConfig {
        return try {
            val rootFile = File(context.getExternalFilesDir(null), FILE_NAME)
            if (rootFile.exists()) {
                val json = rootFile.readText()
                Gson().fromJson(json, AppConfig::class.java) ?: AppConfig()
            } else {
                AppConfig()
            }
        } catch (e: Exception) {
            AppConfig()
        }
    }

    fun saveConfig(context: Context, config: AppConfig) {
        try {
            val rootFile = File(context.getExternalFilesDir(null), FILE_NAME)
            val json = Gson().toJson(config)
            rootFile.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
