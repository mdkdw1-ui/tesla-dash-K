package com.mdkdw1.ui.tesla

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
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

        setupClicks()
        setupPeriodToggle()
        setupMainTabs()
        setupSubTabs()
        showMonitorVehicle()
        showVehicleSubTab()
    }

    private fun setupClicks() {
        binding.btnSaveConfig.setOnClickListener { saveConfigFromUi() }
        binding.btnLogin.setOnClickListener { startTeslaLogin() }
        binding.btnVehicles.setOnClickListener { loadVehicles() }
        binding.btnRefresh.setOnClickListener { refreshAndSync() }
        binding.btnFlashLights.setOnClickListener { flashLights() }
        binding.btnToggleSentry.setOnClickListener { toggleSentry() }
        binding.btnOpenRouteMap.setOnClickListener { openRouteMap() }
        binding.btnSettings.setOnClickListener { openConfigScreen() }
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

    private fun setupMainTabs() {
        binding.btnMainMonitor.setOnClickListener { showMonitorTab() }
        binding.btnMainGuardian.setOnClickListener { showGuardianTab() }
    }

    private fun setupSubTabs() {
        binding.btnSubVehicle.setOnClickListener { showVehicleSubTab() }
        binding.btnSubDriving.setOnClickListener { showDrivingSubTab() }
        binding.btnSubMonthly.setOnClickListener { showMonthlySubTab() }
        binding.btnSubBattery.setOnClickListener { showBatterySubTab() }
    }

    private fun showMonitorTab() {
        binding.panelVehicle.visibility = View.VISIBLE
        binding.panelDriving.visibility = View.GONE
        binding.panelMonthly.visibility = View.GONE
        binding.panelBattery.visibility = View.GONE

        binding.btnMainMonitor.setBackgroundColor(getColor(R.color.tesla_blue))
        binding.btnMainMonitor.setTextColor(getColor(android.R.color.white))
        binding.btnMainGuardian.setBackgroundColor(getColor(R.color.tesla_chip))
        binding.btnMainGuardian.setTextColor(getColor(R.color.tesla_text_muted))
    }

    private fun showGuardianTab() {
        binding.panelVehicle.visibility = View.VISIBLE
        binding.panelDriving.visibility = View.GONE
        binding.panelMonthly.visibility = View.GONE
        binding.panelBattery.visibility = View.GONE

        binding.btnMainMonitor.setBackgroundColor(getColor(R.color.tesla_chip))
        binding.btnMainMonitor.setTextColor(getColor(R.color.tesla_text_muted))
        binding.btnMainGuardian.setBackgroundColor(getColor(R.color.tesla_blue))
        binding.btnMainGuardian.setTextColor(getColor(android.R.color.white))
    }

    private fun showVehicleSubTab() {
        binding.panelVehicle.visibility = View.VISIBLE
        binding.panelDriving.visibility = View.GONE
        binding.panelMonthly.visibility = View.GONE
        binding.panelBattery.visibility = View.GONE
        highlightSubTab(1)
    }

    private fun showDrivingSubTab() {
        binding.panelVehicle.visibility = View.GONE
        binding.panelDriving.visibility = View.VISIBLE
        binding.panelMonthly.visibility = View.GONE
        binding.panelBattery.visibility = View.GONE
        highlightSubTab(2)
    }

    private fun showMonthlySubTab() {
        binding.panelVehicle.visibility = View.GONE
        binding.panelDriving.visibility = View.GONE
        binding.panelMonthly.visibility = View.VISIBLE
        binding.panelBattery.visibility = View.GONE
        highlightSubTab(3)
    }

    private fun showBatterySubTab() {
        binding.panelVehicle.visibility = View.GONE
        binding.panelDriving.visibility = View.GONE
        binding.panelMonthly.visibility = View.GONE
        binding.panelBattery.visibility = View.VISIBLE
        highlightSubTab(4)
    }

    private fun highlightSubTab(active: Int) {
        val activeColor = getColor(R.color.tesla_blue)
        val inactiveColor = getColor(R.color.tesla_chip)
        val activeText = getColor(android.R.color.white)
        val inactiveText = getColor(R.color.tesla_text_muted)

        binding.btnSubVehicle.setBackgroundColor(if (active == 1) activeColor else inactiveColor)
        binding.btnSubDriving.setBackgroundColor(if (active == 2) activeColor else inactiveColor)
        binding.btnSubMonthly.setBackgroundColor(if (active == 3) activeColor else inactiveColor)
        binding.btnSubBattery.setBackgroundColor(if (active == 4) activeColor else inactiveColor)

        binding.btnSubVehicle.setTextColor(if (active == 1) activeText else inactiveText)
        binding.btnSubDriving.setTextColor(if (active == 2) activeText else inactiveText)
        binding.btnSubMonthly.setTextColor(if (active == 3) activeText else inactiveText)
        binding.btnSubBattery.setTextColor(if (active == 4) activeText else inactiveText)
    }

    private fun openRouteMap() {
        startActivity(
            Intent(this, KakaoRouteMapActivity::class.java)
                .putExtra(EXTRA_PERIOD, selectedPeriod.name)
        )
    }

    private fun openConfigScreen() {
        binding.btnSaveConfig.performClick()
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
