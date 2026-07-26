package com.mdkdw1.ui.tesla

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mdkdw1.ui.tesla.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefsStore: PrefsStore
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var apiClient: TeslaApiClient
    private lateinit var repository: AppRepository
    private var selectedPeriod: RoutePeriod = RoutePeriod.DAY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsStore = PrefsStore(this)
        notificationHelper = NotificationHelper(prefsStore)
        apiClient = TeslaApiClient(prefsStore)
        repository = AppRepository(prefsStore, apiClient, notificationHelper)

        setupPeriodToggle()
        setupClicks()
    }

    private fun setupClicks() {
        binding.btnSaveConfig.setOnClickListener { saveConfigFromUi() }
        binding.btnLogin.setOnClickListener { startTeslaLogin() }
        binding.btnVehicles.setOnClickListener { loadVehicles() }
        binding.btnRefresh.setOnClickListener { refreshAndSync() }
        binding.btnFlashLights.setOnClickListener { flashLights() }
        binding.btnToggleSentry.setOnClickListener { toggleSentry() }
        binding.btnOpenRouteMap.setOnClickListener { openRouteMap() }
    }

    private fun setupPeriodToggle() {
        binding.tgPeriod.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            selectedPeriod = when (checkedId) {
                R.id.btnPeriodDay -> RoutePeriod.DAY
                R.id.btnPeriodWeek -> RoutePeriod.WEEK
                R.id.btnPeriodMonth -> RoutePeriod.MONTH
                R.id.btnPeriodQuarter -> RoutePeriod.QUARTER
                R.id.btnPeriodHalfYear -> RoutePeriod.HALF_YEAR
                R.id.btnPeriodYear -> RoutePeriod.YEAR
                else -> RoutePeriod.DAY
            }
            binding.tvSelectedPeriod.text = when (selectedPeriod) {
                RoutePeriod.DAY -> "일"
                RoutePeriod.WEEK -> "주"
                RoutePeriod.MONTH -> "월"
                RoutePeriod.QUARTER -> "분기"
                RoutePeriod.HALF_YEAR -> "반기"
                RoutePeriod.YEAR -> "년"
                RoutePeriod.CUSTOM -> "직접"
            }
        }
    }

    private fun openRouteMap() {
        startActivity(
            Intent(this, KakaoRouteMapActivity::class.java)
                .putExtra(EXTRA_PERIOD, selectedPeriod.name)
        )
    }

    private fun saveConfigFromUi() {
        prefsStore.saveClientId(binding.etClientId.text?.toString())
        prefsStore.saveBackendUrl(binding.etBackendCallback.text?.toString())
        prefsStore.saveKakaoNativeAppKey(binding.etKakaoNativeAppKey.text?.toString())
        prefsStore.saveNtfyTopic(binding.etNtfyTopic.text?.toString())
    }

    private fun startTeslaLogin() {}
    private fun loadVehicles() {}
    private fun refreshAndSync() {}
    private fun flashLights() {}
    private fun toggleSentry() {}

    companion object {
        const val EXTRA_PERIOD = "extra_period"
    }
}
