package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for managing and persisting the 2-front / 3-rear squad formation (§§03, 20.2).
 *
 * Invariants:
 * - Persisted locally across app sessions in SharedPreferences.
 * - Guarantees no recruit can be deployed into multiple slots simultaneously.
 * - Defaults to START00 canonical positions:
 *   Front-Left: K-01 Guardian
 *   Front-Right: K-03 Finisher
 *   Rear-Left: K-12 Scout
 *   Rear-Center: K-19 Reliable Striker
 *   Rear-Right: Open / Reserved
 */
class FormationRepository(context: Context) {

    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _formation = MutableStateFlow(loadFormation())
    val formation: StateFlow<SquadFormation> = _formation.asStateFlow()

    private fun loadFormation(): SquadFormation {
        val hasSaved = prefs.contains(KEY_INITIALIZED)
        if (!hasSaved) {
            val defaults = SquadFormation.defaultSlotMap()
            saveFormationInternal(defaults)
            return SquadFormation(defaults)
        }

        val slots = mutableMapOf<FormationSlotId, String?>()
        for (slotId in FormationSlotId.values()) {
            val savedId = prefs.getString(slotId.slotKey, null)
            slots[slotId] = savedId
        }
        return SquadFormation(slots)
    }

    private fun saveFormationInternal(slots: Map<FormationSlotId, String?>) {
        val editor = prefs.edit().putBoolean(KEY_INITIALIZED, true)
        for (slotId in FormationSlotId.values()) {
            val id = slots[slotId]
            if (id != null) {
                editor.putString(slotId.slotKey, id)
            } else {
                editor.remove(slotId.slotKey)
            }
        }
        editor.commit()
    }

    /**
     * Assigns a shinobi to targetSlot. If shinobiId is already in another slot,
     * it removes them from that other slot to ensure unique deployment.
     * If shinobiId is null, clears the slot.
     */
    fun assignShinobiToSlot(shinobiId: String?, targetSlot: FormationSlotId): SquadFormation {
        synchronized(this) {
            val currentMap = _formation.value.slots.toMutableMap()

            // If shinobi is already somewhere else, clear that slot
            if (shinobiId != null) {
                for (entry in currentMap.entries) {
                    if (entry.value == shinobiId && entry.key != targetSlot) {
                        currentMap[entry.key] = null
                    }
                }
            }

            currentMap[targetSlot] = shinobiId
            saveFormationInternal(currentMap)

            val updated = SquadFormation(currentMap)
            _formation.value = updated
            return updated
        }
    }

    /**
     * Swaps the occupants of two formation slots.
     */
    fun swapSlots(slotA: FormationSlotId, slotB: FormationSlotId): SquadFormation {
        if (slotA == slotB) return _formation.value

        synchronized(this) {
            val currentMap = _formation.value.slots.toMutableMap()
            val temp = currentMap[slotA]
            currentMap[slotA] = currentMap[slotB]
            currentMap[slotB] = temp

            saveFormationInternal(currentMap)

            val updated = SquadFormation(currentMap)
            _formation.value = updated
            return updated
        }
    }

    /**
     * Resets formation to canonical START00 deployment.
     */
    fun resetToDefaults(): SquadFormation {
        synchronized(this) {
            val defaults = SquadFormation.defaultSlotMap()
            saveFormationInternal(defaults)
            val updated = SquadFormation(defaults)
            _formation.value = updated
            return updated
        }
    }

    /**
     * Clears all formation preferences for testing.
     */
    fun clearForTesting() {
        prefs.edit().clear().commit()
        _formation.value = SquadFormation(emptyMap())
    }

    companion object {
        const val PREFS_NAME = "broken_supply_route_formation_prefs"
        const val KEY_INITIALIZED = "formation_initialized"

        @Volatile
        private var instance: FormationRepository? = null

        fun getInstance(context: Context): FormationRepository {
            return instance ?: synchronized(this) {
                instance ?: FormationRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
