package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for managing Training Hall level progression, wallet balances,
 * and the E0 idle anchor.
 *
 * Implements strict transaction integrity for ONB01:
 * - 40 Ryo / 60 EXP credited once.
 * - Hall 1→5 purchased for 32 Ryo / 48 EXP (4 levels @ 8R / 12EXP each).
 * - Leaves exactly 8 Ryo / 12 EXP remainder.
 * - Unlocks E0 idle checkpoint.
 * - Entire workflow is idempotent and survives process restarts.
 */
class TrainingHallRepository(context: Context) {

    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _hallState = MutableStateFlow(loadState())
    val hallState: StateFlow<TrainingHallState> = _hallState.asStateFlow()

    private fun loadState(): TrainingHallState {
        return TrainingHallState(
            hallLevel = prefs.getInt(KEY_HALL_LEVEL, 1),
            hallCap = prefs.getInt(KEY_HALL_CAP, 20),
            ryo = prefs.getInt(KEY_WALLET_RYO, 0),
            shinobiExp = prefs.getInt(KEY_WALLET_EXP, 0),
            commonEssence = prefs.getInt(KEY_WALLET_ESSENCE, 0),
            enhancementMaterial = prefs.getInt(KEY_WALLET_ENHANCEMENT, 0),
            onb01CreditGranted = prefs.getBoolean(KEY_ONB01_CREDIT, false),
            onb01Completed = prefs.getBoolean(KEY_ONB01_COMPLETED, false),
            onb02CreditGranted = prefs.getBoolean(KEY_ONB02_CREDIT, false),
            onb02Completed = prefs.getBoolean(KEY_ONB02_COMPLETED, false),
            isE0Unlocked = prefs.getBoolean(KEY_E0_UNLOCKED, false),
            e0UnlockTimestamp = prefs.getLong(KEY_E0_TIMESTAMP, 0L)
        )
    }

    /**
     * Executes the ONB01 training band grant and purchase in a single atomic confirmation.
     * Credits 40 Ryo / 60 EXP, purchases Hall 1→5 (32 Ryo / 48 EXP), unlocks E0,
     * leaving exactly 8 Ryo / 12 EXP.
     * Strictly idempotent: re-calling cannot re-credit or re-charge.
     */
    fun fundAndTrainOnb01(): TrainingHallState {
        if (_hallState.value.onb01Completed) {
            return _hallState.value
        }

        synchronized(this) {
            if (_hallState.value.onb01Completed) {
                return _hallState.value
            }

            var currentRyo = _hallState.value.ryo
            var currentExp = _hallState.value.shinobiExp
            val creditGranted = _hallState.value.onb01CreditGranted

            // 1. Credit 40 Ryo / 60 EXP if not already credited
            if (!creditGranted) {
                currentRyo += TrainingHallCatalog.ONB01_GRANT_RYO
                currentExp += TrainingHallCatalog.ONB01_GRANT_EXP
            }

            // 2. Debit 32 Ryo / 48 EXP for Hall 1→5
            val netRyo = currentRyo - TrainingHallCatalog.ONB01_TOTAL_SPEND_RYO
            val netExp = currentExp - TrainingHallCatalog.ONB01_TOTAL_SPEND_EXP
            val now = System.currentTimeMillis()

            prefs.edit()
                .putInt(KEY_HALL_LEVEL, 5)
                .putInt(KEY_WALLET_RYO, netRyo)
                .putInt(KEY_WALLET_EXP, netExp)
                .putBoolean(KEY_ONB01_CREDIT, true)
                .putBoolean(KEY_ONB01_COMPLETED, true)
                .putBoolean(KEY_E0_UNLOCKED, true)
                .putLong(KEY_E0_TIMESTAMP, now)
                .commit()

            val updated = _hallState.value.copy(
                hallLevel = 5,
                ryo = netRyo,
                shinobiExp = netExp,
                onb01CreditGranted = true,
                onb01Completed = true,
                isE0Unlocked = true,
                e0UnlockTimestamp = now
            )
            _hallState.value = updated
            return updated
        }
    }

    /**
     * Credits ONB01 grant without immediate purchase.
     * Used to test crash recovery where credit committed before purchase (§03, §52).
     */
    fun creditOnb01GrantOnly(): TrainingHallState {
        if (_hallState.value.onb01CreditGranted) {
            return _hallState.value
        }

        synchronized(this) {
            if (_hallState.value.onb01CreditGranted) {
                return _hallState.value
            }

            val newRyo = _hallState.value.ryo + TrainingHallCatalog.ONB01_GRANT_RYO
            val newExp = _hallState.value.shinobiExp + TrainingHallCatalog.ONB01_GRANT_EXP

            prefs.edit()
                .putInt(KEY_WALLET_RYO, newRyo)
                .putInt(KEY_WALLET_EXP, newExp)
                .putBoolean(KEY_ONB01_CREDIT, true)
                .commit()

            val updated = _hallState.value.copy(
                ryo = newRyo,
                shinobiExp = newExp,
                onb01CreditGranted = true
            )
            _hallState.value = updated
            return updated
        }
    }

    /**
     * Executes the ONB02 funded training band (Band B: Hall 5→10) atomically (§03, §52.2).
     * Prerequisite: ONB01 must be completed first.
     * Credits +56 Ryo / +84 EXP, debits 60 Ryo / 90 EXP for 5 levels (5→10).
     * Exact wallet remainder from prior 8/12: 8 + 56 - 60 = 4 Ryo; 12 + 84 - 90 = 6 EXP.
     * Strictly idempotent and persists synchronously.
     */
    fun fundAndTrainOnb02(): TrainingHallState {
        // Prerequisite check: must complete ONB01 before ONB02
        if (!_hallState.value.onb01Completed) {
            return _hallState.value
        }
        if (_hallState.value.onb02Completed) {
            return _hallState.value
        }

        synchronized(this) {
            if (!_hallState.value.onb01Completed || _hallState.value.onb02Completed) {
                return _hallState.value
            }

            var currentRyo = _hallState.value.ryo
            var currentExp = _hallState.value.shinobiExp
            val creditGranted = _hallState.value.onb02CreditGranted

            // 1. Credit 56 Ryo / 84 EXP if not already credited
            if (!creditGranted) {
                currentRyo += TrainingHallCatalog.ONB02_GRANT_RYO
                currentExp += TrainingHallCatalog.ONB02_GRANT_EXP
            }

            // 2. Debit 60 Ryo / 90 EXP for Hall 5→10 (5 levels @ 12R / 18EXP)
            val netRyo = currentRyo - TrainingHallCatalog.ONB02_TOTAL_SPEND_RYO
            val netExp = currentExp - TrainingHallCatalog.ONB02_TOTAL_SPEND_EXP

            prefs.edit()
                .putInt(KEY_HALL_LEVEL, 10)
                .putInt(KEY_WALLET_RYO, netRyo)
                .putInt(KEY_WALLET_EXP, netExp)
                .putBoolean(KEY_ONB02_CREDIT, true)
                .putBoolean(KEY_ONB02_COMPLETED, true)
                .commit()

            val updated = _hallState.value.copy(
                hallLevel = 10,
                ryo = netRyo,
                shinobiExp = netExp,
                onb02CreditGranted = true,
                onb02Completed = true
            )
            _hallState.value = updated
            return updated
        }
    }

    /**
     * Credits ONB02 grant without immediate purchase.
     * Used to test crash recovery where credit committed before purchase (§03, §52).
     */
    fun creditOnb02GrantOnly(): TrainingHallState {
        if (!_hallState.value.onb01Completed || _hallState.value.onb02CreditGranted) {
            return _hallState.value
        }

        synchronized(this) {
            if (!_hallState.value.onb01Completed || _hallState.value.onb02CreditGranted) {
                return _hallState.value
            }

            val newRyo = _hallState.value.ryo + TrainingHallCatalog.ONB02_GRANT_RYO
            val newExp = _hallState.value.shinobiExp + TrainingHallCatalog.ONB02_GRANT_EXP

            prefs.edit()
                .putInt(KEY_WALLET_RYO, newRyo)
                .putInt(KEY_WALLET_EXP, newExp)
                .putBoolean(KEY_ONB02_CREDIT, true)
                .commit()

            val updated = _hallState.value.copy(
                ryo = newRyo,
                shinobiExp = newExp,
                onb02CreditGranted = true
            )
            _hallState.value = updated
            return updated
        }
    }

    /**
     * Clears all training hall and wallet data (for testing).
     */
    fun clearForTesting() {
        prefs.edit().clear().commit()
        _hallState.value = TrainingHallState()
    }

    companion object {
        const val PREFS_NAME = "broken_supply_route_hall_prefs"
        const val KEY_HALL_LEVEL = "hall_level"
        const val KEY_HALL_CAP = "hall_cap"
        const val KEY_WALLET_RYO = "wallet_ryo"
        const val KEY_WALLET_EXP = "wallet_exp"
        const val KEY_WALLET_ESSENCE = "wallet_essence"
        const val KEY_WALLET_ENHANCEMENT = "wallet_enhancement"
        const val KEY_ONB01_CREDIT = "receipt_ONB01_credit_granted"
        const val KEY_ONB01_COMPLETED = "receipt_ONB01_completed"
        const val KEY_ONB02_CREDIT = "receipt_ONB02_credit_granted"
        const val KEY_ONB02_COMPLETED = "receipt_ONB02_completed"
        const val KEY_E0_UNLOCKED = "receipt_E0_unlocked"
        const val KEY_E0_TIMESTAMP = "receipt_E0_timestamp"

        @Volatile
        private var instance: TrainingHallRepository? = null

        fun getInstance(context: Context): TrainingHallRepository {
            return instance ?: synchronized(this) {
                instance ?: TrainingHallRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
