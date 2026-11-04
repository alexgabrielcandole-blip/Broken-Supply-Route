package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.RosterRepository
import com.example.data.ShinobiCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RosterRepositoryTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val repo = RosterRepository(context)
        repo.clearRosterForTesting()
    }

    @Test
    fun initialRoster_isEmptyBeforeGrant() {
        val repo = RosterRepository(context)
        assertFalse("START00 receipt should be false on a fresh account", repo.isStart00Granted())
        assertTrue("Owned recruits should be empty before grant", repo.ownedShinobi.value.isEmpty())
    }

    @Test
    fun grantStart00_grantsExactlyFourFoundationalRecruits() {
        val repo = RosterRepository(context)
        val recruits = repo.grantStart00()

        assertTrue("START00 receipt must be marked granted", repo.isStart00Granted())
        assertTrue("hasStart00Receipt StateFlow must be true", repo.hasStart00Receipt.value)
        assertEquals("START00 must grant exactly 4 shinobi", 4, recruits.size)
        assertEquals("StateFlow must emit exactly 4 shinobi", 4, repo.ownedShinobi.value.size)

        val ids = recruits.map { it.id }.toSet()
        assertEquals(
            "START00 recruits must be K-01, K-03, K-12, K-19",
            setOf("K-01", "K-03", "K-12", "K-19"),
            ids
        )

        // Verify Hall20/Band-I reference specs
        val k01 = recruits.first { it.id == "K-01" }
        assertEquals(7800, k01.baseHp)
        assertEquals(800, k01.baseAtk)
        assertEquals(1200, k01.baseDef)
        assertEquals(90, k01.baseSpeed)
        assertEquals("Standard Kunai (+0)", k01.standardToolName)
        assertEquals("Shinobi Flak Vest (+0)", k01.standardArmorName)

        val k03 = recruits.first { it.id == "K-03" }
        assertEquals(5100, k03.baseHp)
        assertEquals(1250, k03.baseAtk)
        assertEquals(850, k03.baseDef)
        assertEquals(110, k03.baseSpeed)

        val k12 = recruits.first { it.id == "K-12" }
        assertEquals(5700, k12.baseHp)
        assertEquals(850, k12.baseAtk)
        assertEquals(950, k12.baseDef)
        assertEquals(115, k12.baseSpeed)

        val k19 = recruits.first { it.id == "K-19" }
        assertEquals(6000, k19.baseHp)
        assertEquals(1000, k19.baseAtk)
        assertEquals(1000, k19.baseDef)
        assertEquals(100, k19.baseSpeed)
    }

    @Test
    fun reopeningRoster_isStrictlyIdempotentAndCannotDoubleGrant() {
        val repo = RosterRepository(context)

        // First grant call
        val firstCall = repo.grantStart00()
        assertEquals(4, firstCall.size)

        // Reopen / call grant multiple times
        val secondCall = repo.grantStart00()
        val thirdCall = repo.grantStart00()

        assertEquals("Reopening roster must still contain exactly 4 recruits", 4, secondCall.size)
        assertEquals("Re-granting must still contain exactly 4 recruits", 4, thirdCall.size)
        assertEquals("StateFlow must retain exactly 4 recruits", 4, repo.ownedShinobi.value.size)
    }

    @Test
    fun persistedRoster_survivesAppRelaunch() {
        // Session 1: grant START00
        val session1Repo = RosterRepository(context)
        session1Repo.grantStart00()
        assertEquals(4, session1Repo.ownedShinobi.value.size)

        // Simulate app kill & relaunch: create fresh repository instance
        val session2Repo = RosterRepository(context)
        assertTrue("START00 receipt must persist across app relaunch", session2Repo.isStart00Granted())
        assertTrue("hasStart00Receipt must be true on launch", session2Repo.hasStart00Receipt.value)
        assertEquals("Persisted roster must load all 4 recruits on launch", 4, session2Repo.ownedShinobi.value.size)

        // Re-invoking grant on new session does nothing
        val afterReopening = session2Repo.grantStart00()
        assertEquals(4, afterReopening.size)
    }
}
