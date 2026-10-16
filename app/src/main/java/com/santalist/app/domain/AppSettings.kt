package com.santalist.app.domain

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsState(
    val currency: String = "VND",
    val language: String = "system",
    val notificationsEnabled: Boolean = true,
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0,
    val themeMode: String = "system", // "system", "light", "dark"
    val selectedYear: Int = 2026
)

class AppSettings(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("santalist_user_settings", Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<SettingsState> = _settingsFlow.asStateFlow()

    private fun loadSettings(): SettingsState {
        return SettingsState(
            currency = prefs.getString("currency", "VND") ?: "VND",
            language = prefs.getString("language", "system") ?: "system",
            notificationsEnabled = prefs.getBoolean("notifications_enabled", true),
            reminderHour = prefs.getInt("reminder_hour", 9),
            reminderMinute = prefs.getInt("reminder_minute", 0),
            themeMode = prefs.getString("theme_mode", "system") ?: "system",
            selectedYear = prefs.getInt("selected_year", 2026)
        )
    }

    fun setCurrency(currency: String) {
        prefs.edit().putString("currency", currency).apply()
        _settingsFlow.value = _settingsFlow.value.copy(currency = currency)
    }

    fun setLanguage(language: String) {
        prefs.edit().putString("language", language).apply()
        _settingsFlow.value = _settingsFlow.value.copy(language = language)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(notificationsEnabled = enabled)
    }

    fun setReminderTime(hour: Int, minute: Int) {
        prefs.edit().putInt("reminder_hour", hour).putInt("reminder_minute", minute).apply()
        _settingsFlow.value = _settingsFlow.value.copy(reminderHour = hour, reminderMinute = minute)
    }

    fun setThemeMode(mode: String) {
        prefs.edit().putString("theme_mode", mode).apply()
        _settingsFlow.value = _settingsFlow.value.copy(themeMode = mode)
    }

    fun setSelectedYear(year: Int) {
        prefs.edit().putInt("selected_year", year).apply()
        _settingsFlow.value = _settingsFlow.value.copy(selectedYear = year)
    }
}
