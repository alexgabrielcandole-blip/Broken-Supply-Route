package com.example.data

/**
 * State representing the shared Training Hall, account wallet balances,
 * and the E0 idle reward anchor.
 *
 * Invariants per §§03, 32, 42, 52:
 * - Created at Level 1 with initial Cap 20.
 * - ONB01 credits 40 Ryo / 60 EXP, purchases 1→5 for 32 Ryo / 48 EXP.
 * - Retains exactly 8 Ryo and 12 EXP after ONB01.
 * - Unlocks E0 idle checkpoint (10 Ryo, 20 EXP, 2 essence, 1 enhancement per hour).
 */
data class TrainingHallState(
    val hallLevel: Int = 1,
    val hallCap: Int = 20,
    val ryo: Int = 0,
    val shinobiExp: Int = 0,
    val commonEssence: Int = 0,
    val enhancementMaterial: Int = 0,
    val onb01CreditGranted: Boolean = false,
    val onb01Completed: Boolean = false,
    val onb02CreditGranted: Boolean = false,
    val onb02Completed: Boolean = false,
    val isE0Unlocked: Boolean = false,
    val e0UnlockTimestamp: Long = 0L
)

data class LevelCost(
    val fromLevel: Int,
    val toLevel: Int,
    val ryoCost: Int,
    val expCost: Int
)

object TrainingHallCatalog {
    /**
     * Individual level upgrade costs for Band A (Hall 1→5) per §52.2.
     * 4 levels @ 8 Ryo / 12 EXP each = 32 Ryo / 48 EXP.
     */
    val ONB01_LEVEL_COSTS = listOf(
        LevelCost(1, 2, 8, 12),
        LevelCost(2, 3, 8, 12),
        LevelCost(3, 4, 8, 12),
        LevelCost(4, 5, 8, 12)
    )

    const val ONB01_GRANT_RYO = 40
    const val ONB01_GRANT_EXP = 60
    const val ONB01_TOTAL_SPEND_RYO = 32
    const val ONB01_TOTAL_SPEND_EXP = 48
    const val ONB01_EXPECTED_WALLET_RYO = 8
    const val ONB01_EXPECTED_WALLET_EXP = 12

    /**
     * Individual level upgrade costs for Band B (Hall 5→10) per §52.2.
     * 5 levels @ 12 Ryo / 18 EXP each = 60 Ryo / 90 EXP.
     */
    val ONB02_LEVEL_COSTS = listOf(
        LevelCost(5, 6, 12, 18),
        LevelCost(6, 7, 12, 18),
        LevelCost(7, 8, 12, 18),
        LevelCost(8, 9, 12, 18),
        LevelCost(9, 10, 12, 18)
    )

    const val ONB02_GRANT_RYO = 56
    const val ONB02_GRANT_EXP = 84
    const val ONB02_TOTAL_SPEND_RYO = 60
    const val ONB02_TOTAL_SPEND_EXP = 90
    // Net remainder: previous wallet (8 / 12) + grant (56 / 84) - spend (60 / 90) = 4 Ryo / 6 EXP
    const val ONB02_EXPECTED_WALLET_RYO = 4
    const val ONB02_EXPECTED_WALLET_EXP = 6

    // E0 Hourly Idle Vector per §42
    const val E0_HOURLY_RYO = 10
    const val E0_HOURLY_EXP = 20
    const val E0_HOURLY_ESSENCE = 2
    const val E0_HOURLY_ENHANCEMENT = 1
}
