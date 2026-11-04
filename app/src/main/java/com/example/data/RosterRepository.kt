package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository responsible for account roster ownership and idempotent grants.
 * Enforces the strict rule that START00 grants K-01, K-03, K-12, K-19 exactly once,
 * with durable transaction receipts that survive process death and cannot double-grant.
 */
class RosterRepository(context: Context) {

    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _ownedShinobi = MutableStateFlow(loadOwnedShinobi())
    val ownedShinobi: StateFlow<List<Shinobi>> = _ownedShinobi.asStateFlow()

    private val _hasStart00Receipt = MutableStateFlow(isStart00Granted())
    val hasStart00Receipt: StateFlow<Boolean> = _hasStart00Receipt.asStateFlow()

    /**
     * Checks if the START00 grant receipt has already been committed.
     */
    fun isStart00Granted(): Boolean {
        return prefs.getBoolean(KEY_START00_GRANTED, false)
    }

    /**
     * Executes the START00 grant idempotently.
     * If already granted, returns the existing roster without adding duplicates or advancing counters.
     * If not granted, commits the START00 receipt and credits the four foundational recruits atomically.
     */
    fun grantStart00(): List<Shinobi> {
        if (isStart00Granted()) {
            return _ownedShinobi.value
        }

        synchronized(this) {
            if (isStart00Granted()) {
                return _ownedShinobi.value
            }

            val recruitIds = ShinobiCatalog.START00_MEMBERS.joinToString(",") { it.id }
            prefs.edit()
                .putBoolean(KEY_START00_GRANTED, true)
                .putString(KEY_OWNED_IDS, recruitIds)
                .putLong(KEY_START00_TIMESTAMP, System.currentTimeMillis())
                .commit()

            val updatedList = ShinobiCatalog.START00_MEMBERS
            _ownedShinobi.value = updatedList
            _hasStart00Receipt.value = true
            return updatedList
        }
    }

    private fun loadOwnedShinobi(): List<Shinobi> {
        val rawIds = prefs.getString(KEY_OWNED_IDS, null) ?: return emptyList()
        val ids = rawIds.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        return ids.mapNotNull { ShinobiCatalog.findById(it) }
    }

    /**
     * Clears all roster preferences (used in test cases).
     */
    fun clearRosterForTesting() {
        prefs.edit()
            .remove(KEY_START00_GRANTED)
            .remove(KEY_OWNED_IDS)
            .remove(KEY_START00_TIMESTAMP)
            .commit()
        _ownedShinobi.value = emptyList()
        _hasStart00Receipt.value = false
    }

    companion object {
        const val PREFS_NAME = "broken_supply_route_roster_prefs"
        const val KEY_START00_GRANTED = "receipt_START00_granted"
        const val KEY_OWNED_IDS = "owned_shinobi_ids"
        const val KEY_START00_TIMESTAMP = "receipt_START00_timestamp"

        @Volatile
        private var instance: RosterRepository? = null

        fun getInstance(context: Context): RosterRepository {
            return instance ?: synchronized(this) {
                instance ?: RosterRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
