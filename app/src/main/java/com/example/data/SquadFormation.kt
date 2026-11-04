package com.example.data

/**
 * Tactical Formation Slots per Master Design Bible §§03, 20.2:
 * 5-slot grid composed of 2 Frontline slots and 3 Rearguard slots.
 *
 * Rules & Targeting:
 * - Frontline (Front-Left, Front-Right): Direct melee & frontline single-target attacks
 *   must resolve against living Frontline units first before reaching the Rearguard.
 * - Rearguard (Rear-Left, Rear-Center, Rear-Right): Sheltered from direct non-piercing melee.
 * - Rear-Right is reserved for the upcoming START01 support recruit.
 */
enum class FormationSlotId(
    val slotKey: String,
    val displayName: String,
    val rowName: String,
    val tacticalRoleDescription: String
) {
    FRONT_LEFT(
        slotKey = "slot_front_left",
        displayName = "Frontline Left",
        rowName = "Frontline",
        tacticalRoleDescription = "Primary vanguard interceptor. Absorbs direct melee target priority."
    ),
    FRONT_RIGHT(
        slotKey = "slot_front_right",
        displayName = "Frontline Right",
        rowName = "Frontline",
        tacticalRoleDescription = "Secondary frontline combatant. Duelist / melee counter offensive."
    ),
    REAR_LEFT(
        slotKey = "slot_rear_left",
        displayName = "Rearguard Left",
        rowName = "Rearguard",
        tacticalRoleDescription = "Flank signal & tactical debuff. Protected by frontline vanguard."
    ),
    REAR_CENTER(
        slotKey = "slot_rear_center",
        displayName = "Rearguard Center",
        rowName = "Rearguard",
        tacticalRoleDescription = "Central fire support & consistent technique cadence."
    ),
    REAR_RIGHT(
        slotKey = "slot_rear_right",
        displayName = "Rearguard Right (Reserved)",
        rowName = "Rearguard",
        tacticalRoleDescription = "Tactical reserve slot allocated for START01 support specialist."
    )
}

/**
 * Immutable state representing the 5-slot squad deployment.
 */
data class SquadFormation(
    val slots: Map<FormationSlotId, String?> = defaultSlotMap()
) {
    companion object {
        fun defaultSlotMap(): Map<FormationSlotId, String?> = mapOf(
            FormationSlotId.FRONT_LEFT to "K-01",
            FormationSlotId.FRONT_RIGHT to "K-03",
            FormationSlotId.REAR_LEFT to "K-12",
            FormationSlotId.REAR_CENTER to "K-19",
            FormationSlotId.REAR_RIGHT to null
        )
    }

    fun getShinobiInSlot(slotId: FormationSlotId): Shinobi? {
        val id = slots[slotId] ?: return null
        return ShinobiCatalog.findById(id)
    }

    val deployedCount: Int
        get() = slots.values.count { it != null }

    val frontlineCount: Int
        get() = listOf(FormationSlotId.FRONT_LEFT, FormationSlotId.FRONT_RIGHT).count { slots[it] != null }

    val rearguardCount: Int
        get() = listOf(FormationSlotId.REAR_LEFT, FormationSlotId.REAR_CENTER, FormationSlotId.REAR_RIGHT).count { slots[it] != null }

    /**
     * Finds which slot currently holds the given shinobiId, if any.
     */
    fun findSlotForShinobi(shinobiId: String): FormationSlotId? {
        return slots.entries.firstOrNull { it.value == shinobiId }?.key
    }
}
