package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.FormationRepository
import com.example.data.FormationSlotId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FormationRepositoryTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val repo = FormationRepository(context)
        repo.clearForTesting()
    }

    @Test
    fun defaultFormation_hasExpectedStart00Deployment() {
        val repo = FormationRepository(context)
        val formation = repo.formation.value

        assertEquals("Front-Left must default to K-01 Guardian", "K-01", formation.slots[FormationSlotId.FRONT_LEFT])
        assertEquals("Front-Right must default to K-03 Finisher", "K-03", formation.slots[FormationSlotId.FRONT_RIGHT])
        assertEquals("Rear-Left must default to K-12 Scout", "K-12", formation.slots[FormationSlotId.REAR_LEFT])
        assertEquals("Rear-Center must default to K-19 Striker", "K-19", formation.slots[FormationSlotId.REAR_CENTER])
        assertNull("Rear-Right must default to null (reserved for START01)", formation.slots[FormationSlotId.REAR_RIGHT])

        assertEquals("Deployed count must be 4", 4, formation.deployedCount)
        assertEquals("Frontline count must be 2", 2, formation.frontlineCount)
        assertEquals("Rearguard count must be 2", 2, formation.rearguardCount)
    }

    @Test
    fun assignShinobiToSlot_movesRecruitAndAvoidsDuplicates() {
        val repo = FormationRepository(context)

        // Move K-01 from Front-Left to Rear-Right
        val updated = repo.assignShinobiToSlot("K-01", FormationSlotId.REAR_RIGHT)

        assertEquals("Rear-Right must now hold K-01", "K-01", updated.slots[FormationSlotId.REAR_RIGHT])
        assertNull("Front-Left must be vacated to avoid duplicate deployment", updated.slots[FormationSlotId.FRONT_LEFT])
        assertEquals("Deployed count must remain 4", 4, updated.deployedCount)
    }

    @Test
    fun swapSlots_exchangesPositionsCorrectly() {
        val repo = FormationRepository(context)

        // Swap Front-Left (K-01) with Front-Right (K-03)
        val swapped = repo.swapSlots(FormationSlotId.FRONT_LEFT, FormationSlotId.FRONT_RIGHT)

        assertEquals("Front-Left must now hold K-03", "K-03", swapped.slots[FormationSlotId.FRONT_LEFT])
        assertEquals("Front-Right must now hold K-01", "K-01", swapped.slots[FormationSlotId.FRONT_RIGHT])
    }

    @Test
    fun formation_survivesAppRelaunch() {
        // Session 1: move K-12 to Front-Left
        val session1 = FormationRepository(context)
        session1.assignShinobiToSlot("K-12", FormationSlotId.FRONT_LEFT)

        // Session 2: simulate new launch reading persisted SharedPreferences
        val session2 = FormationRepository(context)
        val restored = session2.formation.value

        assertEquals("Front-Left must restore K-12", "K-12", restored.slots[FormationSlotId.FRONT_LEFT])
        assertNull("Rear-Left must be vacated", restored.slots[FormationSlotId.REAR_LEFT])
    }

    @Test
    fun resetToDefaults_restoresCanonicalStart00() {
        val repo = FormationRepository(context)
        repo.assignShinobiToSlot(null, FormationSlotId.FRONT_LEFT)
        repo.assignShinobiToSlot(null, FormationSlotId.FRONT_RIGHT)

        val reset = repo.resetToDefaults()

        assertEquals("K-01", reset.slots[FormationSlotId.FRONT_LEFT])
        assertEquals("K-03", reset.slots[FormationSlotId.FRONT_RIGHT])
        assertEquals("K-12", reset.slots[FormationSlotId.REAR_LEFT])
        assertEquals("K-19", reset.slots[FormationSlotId.REAR_CENTER])
        assertNull(reset.slots[FormationSlotId.REAR_RIGHT])
    }
}
