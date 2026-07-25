package com.example.tesladashk.service

object GuardianService {
    var isRunning: Boolean = false

    fun startService() {
        isRunning = true
    }

    fun stopService() {
        isRunning = false
    }
}
