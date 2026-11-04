package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Handles persistent local storage of player dispatch preferences.
 * Persists choice synchronously across process restarts and app lifecycle events.
 */
class DispatchPreferencesRepository(context: Context) {

    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _selectedMode = MutableStateFlow(loadInitialMode())
    val selectedMode: StateFlow<DispatchMode?> = _selectedMode.asStateFlow()

    private fun loadInitialMode(): DispatchMode? {
        val raw = prefs.getString(KEY_DISPATCH_MODE, null)
        return DispatchMode.fromSerialized(raw)
    }

    /**
     * Persists the player's selected briefing mode locally.
     * Uses commit() to guarantee synchronous persistence before activity teardown.
     */
    fun saveMode(mode: DispatchMode) {
        prefs.edit().putString(KEY_DISPATCH_MODE, mode.serializedName).commit()
        _selectedMode.value = mode
    }

    /**
     * Synchronously returns the currently saved mode.
     */
    fun getSavedMode(): DispatchMode? {
        val raw = prefs.getString(KEY_DISPATCH_MODE, null)
        return DispatchMode.fromSerialized(raw)
    }

    /**
     * Clears saved mode (useful for testing and reset flows).
     */
    fun clearPreference() {
        prefs.edit().remove(KEY_DISPATCH_MODE).commit()
        _selectedMode.value = null
    }

    companion object {
        const val PREFS_NAME = "broken_supply_route_prefs"
        const val KEY_DISPATCH_MODE = "pref_dispatch_mode"

        @Volatile
        private var instance: DispatchPreferencesRepository? = null

        fun getInstance(context: Context): DispatchPreferencesRepository {
            return instance ?: synchronized(this) {
                instance ?: DispatchPreferencesRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
