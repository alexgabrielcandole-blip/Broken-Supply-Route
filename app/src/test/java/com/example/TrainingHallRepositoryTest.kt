package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.TrainingHallRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TrainingHallRepositoryTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val repo = TrainingHallRepository(context)
        repo.clearForTesting()
    }

    @Test
    fun initialHallState_isLevelOneCapTwentyWithZeroWalletAndE0Locked() {
        val repo = TrainingHallRepository(context)
        val state = repo.hallState.value

        assertEquals("Initial Hall Level must be 1", 1, state.hallLevel)
        assertEquals("Initial Hall Cap must be 20", 20, state.hallCap)
        assertEquals("Opening Ryo must be 0", 0, state.ryo)
        assertEquals("Opening EXP must be 0", 0, state.shinobiExp)
        assertFalse("ONB01 must not be completed initially", state.onb01Completed)
        assertFalse("ONB02 must not be completed initially", state.onb02Completed)
        assertFalse("E0 idle checkpoint must be locked initially", state.isE0Unlocked)
    }

    @Test
    fun fundAndTrainOnb01_commitsLevelFiveAndE0AnchorWithExactWalletRemainder() {
        val repo = TrainingHallRepository(context)

        // Commit ONB01 funded band
        val updated = repo.fundAndTrainOnb01()

        assertEquals("Hall Level must advance to 5", 5, updated.hallLevel)
        assertEquals("Hall Cap must remain 20", 20, updated.hallCap)
        assertEquals("Wallet must retain exactly 8 Ryo (40 credit - 32 debit)", 8, updated.ryo)
        assertEquals("Wallet must retain exactly 12 EXP (60 credit - 48 debit)", 12, updated.shinobiExp)
        assertTrue("ONB01 credit must be marked granted", updated.onb01CreditGranted)
        assertTrue("ONB01 band must be marked completed", updated.onb01Completed)
        assertTrue("E0 idle checkpoint must be unlocked", updated.isE0Unlocked)
        assertTrue("E0 unlock timestamp must be recorded", updated.e0UnlockTimestamp > 0L)
    }

    @Test
    fun fundAndTrainOnb01_isStrictlyIdempotentOnRepeatCall() {
        val repo = TrainingHallRepository(context)

        repo.fundAndTrainOnb01()
        val repeat1 = repo.fundAndTrainOnb01()
        val repeat2 = repo.fundAndTrainOnb01()

        assertEquals("Hall Level must stay 5 on repeat calls", 5, repeat1.hallLevel)
        assertEquals("Ryo must stay 8 on repeat calls (no double credit)", 8, repeat1.ryo)
        assertEquals("EXP must stay 12 on repeat calls (no double credit)", 12, repeat1.shinobiExp)

        assertEquals(5, repeat2.hallLevel)
        assertEquals(8, repeat2.ryo)
        assertEquals(12, repeat2.shinobiExp)
    }

    @Test
    fun fundAndTrainOnb02_requiresOnb01Prerequisite() {
        val repo = TrainingHallRepository(context)

        // Attempt ONB02 before completing ONB01
        val result = repo.fundAndTrainOnb02()

        assertEquals("Hall Level must stay 1 if ONB01 not completed", 1, result.hallLevel)
        assertEquals("Ryo must stay 0", 0, result.ryo)
        assertEquals("EXP must stay 0", 0, result.shinobiExp)
        assertFalse("ONB02 must remain uncompleted", result.onb02Completed)
    }

    @Test
    fun fundAndTrainOnb02_advancesToLevel10AndLeavesExactFourRyoSixExp() {
        val repo = TrainingHallRepository(context)

        // Step 1: Complete ONB01 -> Level 5, 8 Ryo, 12 EXP
        repo.fundAndTrainOnb01()

        // Step 2: Complete ONB02 -> Level 10, 8 + 56 - 60 = 4 Ryo, 12 + 84 - 90 = 6 EXP
        val stateAfterOnb02 = repo.fundAndTrainOnb02()

        assertEquals("Hall Level must advance to 10", 10, stateAfterOnb02.hallLevel)
        assertEquals("Hall Cap must remain 20", 20, stateAfterOnb02.hallCap)
        assertEquals("Ryo remainder must be exactly 4 per §03 and §52.2", 4, stateAfterOnb02.ryo)
        assertEquals("EXP remainder must be exactly 6 per §03 and §52.2", 6, stateAfterOnb02.shinobiExp)
        assertTrue("ONB02 credit must be marked granted", stateAfterOnb02.onb02CreditGranted)
        assertTrue("ONB02 band must be marked completed", stateAfterOnb02.onb02Completed)
    }

    @Test
    fun fundAndTrainOnb02_isStrictlyIdempotentOnRepeatCall() {
        val repo = TrainingHallRepository(context)

        repo.fundAndTrainOnb01()
        repo.fundAndTrainOnb02()

        val repeat1 = repo.fundAndTrainOnb02()
        val repeat2 = repo.fundAndTrainOnb02()

        assertEquals("Hall Level must stay 10 on repeat calls", 10, repeat1.hallLevel)
        assertEquals("Ryo must stay 4 on repeat calls (no double credit)", 4, repeat1.ryo)
        assertEquals("EXP must stay 6 on repeat calls (no double credit)", 6, repeat1.shinobiExp)

        assertEquals(10, repeat2.hallLevel)
        assertEquals(4, repeat2.ryo)
        assertEquals(6, repeat2.shinobiExp)
    }

    @Test
    fun persistedTrainingHallState_survivesAppRelaunch() {
        // Session 1: complete ONB01 and ONB02
        val session1 = TrainingHallRepository(context)
        session1.fundAndTrainOnb01()
        session1.fundAndTrainOnb02()
        assertEquals(10, session1.hallState.value.hallLevel)

        // Session 2: simulate fresh app launch reading SharedPreferences
        val session2 = TrainingHallRepository(context)
        val restored = session2.hallState.value

        assertEquals("Restored Hall level must be 10", 10, restored.hallLevel)
        assertEquals("Restored Ryo must be 4", 4, restored.ryo)
        assertEquals("Restored EXP must be 6", 6, restored.shinobiExp)
        assertTrue("Restored ONB01 must be completed", restored.onb01Completed)
        assertTrue("Restored ONB02 must be completed", restored.onb02Completed)
        assertTrue("Restored E0 must be unlocked", restored.isE0Unlocked)
    }

    @Test
    fun crashRecovery_whenCreditGrantedBeforePurchase_resumesWithoutDoubleCredit() {
        val session1 = TrainingHallRepository(context)
        session1.fundAndTrainOnb01()

        // Simulate crash right after ONB02 credit was granted:
        // Prior wallet: 8 Ryo / 12 EXP + 56 / 84 = 64 Ryo / 96 EXP
        session1.creditOnb02GrantOnly()
        assertEquals(64, session1.hallState.value.ryo)
        assertEquals(96, session1.hallState.value.shinobiExp)
        assertTrue(session1.hallState.value.onb02CreditGranted)
        assertFalse(session1.hallState.value.onb02Completed)

        // Session 2: app relaunches and completes purchase
        val session2 = TrainingHallRepository(context)
        val finalState = session2.fundAndTrainOnb02()

        assertEquals("Hall Level must be 10", 10, finalState.hallLevel)
        assertEquals("Ryo must be exactly 4 (no double credit)", 4, finalState.ryo)
        assertEquals("EXP must be exactly 6 (no double credit)", 6, finalState.shinobiExp)
        assertTrue(finalState.onb02Completed)
    }
}
