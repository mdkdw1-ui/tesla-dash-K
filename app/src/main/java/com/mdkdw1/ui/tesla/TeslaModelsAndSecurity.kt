package com.mdkdw1.ui.tesla

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.io.Serializable
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

// ==========================================
// 1. App Configuration Model
// ==========================================
data class AppConfig(
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val kakaoMapKey: String = "",
    val teslaAccessToken: String = ""
) : Serializable

// ==========================================
// 2. Tesla Dashboard Data Models
// ==========================================
data class VehicleState(
    val vehicleName: String = "Model Y Long Range",
    val batteryLevel: Int = 82,
    val estimatedRangeKm: Int = 412,
    val odometerKm: Int = 35240,
    val isLocked: Boolean = true,
    val isClimateOn: Boolean = false,
    val insideTempC: Double = 21.5,
    val outsideTempC: Double = 24.0,
    val isTrunkOpen: Boolean = false,
    val isFrunkOpen: Boolean = false,
    val isSentryModeOn: Boolean = true,
    val speedKmh: Int = 0,
    val chargeStatus: String = "Discharging"
)

data class ConsumableItem(
    val name: String,
    val currentKm: Int,
    val maxKm: Int,
    val lastReplacedDate: String
) {
    val progressRatio: Float
        get() = (currentKm.toFloat() / maxKm.toFloat()).coerceIn(0f, 1f)
}

data class DailyTrip(
    val date: String,
    val distanceKm: Double,
    val efficiencyWhPerKm: Double,
    val energyUsedKwh: Double
)

data class ChargeRecord(
    val date: String,
    val kWh: Double,
    val cost: Int,
    val location: String
)

data class BatteryDegradationPoint(
    val odometerKm: Int,
    val capacityKwh: Double,
    val degradationPct: Double
)

// ==========================================
// 3. Hardware KeyStore AES-256 Encryption
// ==========================================
class CryptoManager {
    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private fun getOrCreateKey(): SecretKey {
        val existingKey = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        if (existingKey != null) {
            return existingKey.secretKey
        }
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )
        val keySpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        keyGenerator.init(keySpec)
        return keyGenerator.generateKey()
    }

    fun encrypt(data: String): String {
        if (data.isEmpty()) return ""
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        val combined = ByteArray(iv.size + encryptedBytes.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    fun decrypt(encryptedData: String): String {
        if (encryptedData.isEmpty()) return ""
        return try {
            val combined = Base64.decode(encryptedData, Base64.DEFAULT)
            val iv = ByteArray(12)
            val cipherText = ByteArray(combined.size - 12)
            System.arraycopy(combined, 0, iv, 0, 12)
            System.arraycopy(combined, 12, cipherText, 0, cipherText.size)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), spec)
            String(cipher.doFinal(cipherText), Charsets.UTF_8)
        } catch (e: Exception) {
            ""
        }
    }

    companion object {
        private const val KEY_ALIAS = "TeslaDashEncryptedKey"
    }
}

// ==========================================
// 4. Encrypted Settings Preferences Manager
// ==========================================
class EncryptedSettingsManager(context: Context) {
    private val cryptoManager = CryptoManager()
    private val prefs: SharedPreferences = context.getSharedPreferences("tesla_dash_prefs", Context.MODE_PRIVATE)

    fun saveSettings(config: AppConfig) {
        prefs.edit().apply {
            putString("enc_supabase_url", cryptoManager.encrypt(config.supabaseUrl))
            putString("enc_supabase_key", cryptoManager.encrypt(config.supabaseKey))
            putString("enc_kakao_map_key", cryptoManager.encrypt(config.kakaoMapKey))
            putString("enc_tesla_token", cryptoManager.encrypt(config.teslaAccessToken))
            apply()
        }
    }

    fun loadSettings(): AppConfig {
        val encUrl = prefs.getString("enc_supabase_url", "") ?: ""
        val encKey = prefs.getString("enc_supabase_key", "") ?: ""
        val encKakao = prefs.getString("enc_kakao_map_key", "") ?: ""
        val encToken = prefs.getString("enc_tesla_token", "") ?: ""

        return AppConfig(
            supabaseUrl = cryptoManager.decrypt(encUrl),
            supabaseKey = cryptoManager.decrypt(encKey),
            kakaoMapKey = cryptoManager.decrypt(encKakao),
            teslaAccessToken = cryptoManager.decrypt(encToken)
        )
    }
}
