package com.example.data

/**
 * Immutable model representing a recruited shinobi in The Broken Supply Route.
 * Conforms to the reference stat packet and kit contracts defined in §§26-27, 45, 51.
 */
data class Shinobi(
    val id: String,
    val name: String,
    val title: String,
    val primaryRole: String,
    val preferredRow: String,
    val baseHp: Int,
    val baseAtk: Int,
    val baseDef: Int,
    val baseSpeed: Int,
    val critChancePercent: Int,
    val techniqueName: String,
    val techniqueSummary: String,
    val standardToolName: String = "Standard Kunai (+0)",
    val standardArmorName: String = "Shinobi Flak Vest (+0)",
    val grantReceiptId: String = "START00"
)

object ShinobiCatalog {
    val K01_GUARDIAN = Shinobi(
        id = "K-01",
        name = "K-01 Guardian",
        title = "Iron Bulwark",
        primaryRole = "Guardian / Protector",
        preferredRow = "Frontline (Left)",
        baseHp = 7800,
        baseAtk = 800,
        baseDef = 1200,
        baseSpeed = 90,
        critChancePercent = 5,
        techniqueName = "Taunt & Shelter",
        techniqueSummary = "Redirects up to 2 eligible single-target hostile attacks to self, shielding exposed allies."
    )

    val K03_FINISHER = Shinobi(
        id = "K-03",
        name = "K-03 Finisher",
        title = "Execution Duelist",
        primaryRole = "Single-Target Finisher",
        preferredRow = "Frontline (Right)",
        baseHp = 5100,
        baseAtk = 1250,
        baseDef = 850,
        baseSpeed = 110,
        critChancePercent = 15,
        techniqueName = "Focused Technique",
        techniqueSummary = "1.40× ATK direct strike on legal frontline enemy; prioritizes declared threats."
    )

    val K12_SCOUT = Shinobi(
        id = "K-12",
        name = "K-12 Scout",
        title = "Forward Signal Marker",
        primaryRole = "Scout / Mark Setter",
        preferredRow = "Rear Line (Left)",
        baseHp = 5700,
        baseAtk = 850,
        baseDef = 950,
        baseSpeed = 115,
        critChancePercent = 12,
        techniqueName = "Scout Mark",
        techniqueSummary = "0.55× ATK with 80% chance to apply Mark (+10% Vulnerability for next 2 target slots)."
    )

    val K19_STRIKER = Shinobi(
        id = "K-19",
        name = "K-19 Reliable Striker",
        title = "Tactical Vanguard",
        primaryRole = "Reliable Striker / Flex",
        preferredRow = "Rear Line (Center)",
        baseHp = 6000,
        baseAtk = 1000,
        baseDef = 1000,
        baseSpeed = 100,
        critChancePercent = 10,
        techniqueName = "Reliable Technique",
        techniqueSummary = "1.30× ATK dependable frontline strike providing reliable damage without setup dependency."
    )

    val START00_MEMBERS = listOf(K01_GUARDIAN, K03_FINISHER, K12_SCOUT, K19_STRIKER)

    fun findById(id: String): Shinobi? = START00_MEMBERS.firstOrNull { it.id == id }
}
