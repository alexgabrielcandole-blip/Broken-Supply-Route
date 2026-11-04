package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.DispatchMode
import com.example.data.DispatchPreferencesRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DispatchPreferencesTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val repo = DispatchPreferencesRepository(context)
        repo.clearPreference()
    }

    @Test
    fun initialLaunch_hasNoSavedPreference() {
        val repo = DispatchPreferencesRepository(context)
        assertNull("New account should have no initial mode selected", repo.getSavedMode())
        assertNull("StateFlow initial value should be null", repo.selectedMode.value)
    }

    @Test
    fun selectingCompact_persistsAcrossAppRelaunch() {
        // First session: player chooses Compact Mode
        val firstSessionRepo = DispatchPreferencesRepository(context)
        firstSessionRepo.saveMode(DispatchMode.COMPACT)
        assertEquals(DispatchMode.COMPACT, firstSessionRepo.getSavedMode())
        assertEquals(DispatchMode.COMPACT, firstSessionRepo.selectedMode.value)

        // Simulate app kill & relaunch: create fresh repository instance reading persisted storage
        val relaunchRepo = DispatchPreferencesRepository(context)
        assertEquals(
            "Compact Mode must remain selected after app relaunch",
            DispatchMode.COMPACT,
            relaunchRepo.getSavedMode()
        )
        assertEquals(
            "StateFlow must restore Compact Mode on launch",
            DispatchMode.COMPACT,
            relaunchRepo.selectedMode.value
        )
    }

    @Test
    fun selectingGuided_persistsAcrossAppRelaunch() {
        // First session: player chooses Guided Mode
        val firstSessionRepo = DispatchPreferencesRepository(context)
        firstSessionRepo.saveMode(DispatchMode.GUIDED)
        assertEquals(DispatchMode.GUIDED, firstSessionRepo.getSavedMode())
        assertEquals(DispatchMode.GUIDED, firstSessionRepo.selectedMode.value)

        // Simulate app kill & relaunch: create fresh repository instance reading persisted storage
        val relaunchRepo = DispatchPreferencesRepository(context)
        assertEquals(
            "Guided Mode must remain selected after app relaunch",
            DispatchMode.GUIDED,
            relaunchRepo.getSavedMode()
        )
        assertEquals(
            "StateFlow must restore Guided Mode on launch",
            DispatchMode.GUIDED,
            relaunchRepo.selectedMode.value
        )
    }

    @Test
    fun switchingSelection_updatesPersistedState() {
        val repo = DispatchPreferencesRepository(context)

        repo.saveMode(DispatchMode.GUIDED)
        assertEquals(DispatchMode.GUIDED, repo.getSavedMode())

        repo.saveMode(DispatchMode.COMPACT)
        assertEquals(DispatchMode.COMPACT, repo.getSavedMode())

        // Re-read from fresh instance
        val relaunchRepo = DispatchPreferencesRepository(context)
        assertEquals(DispatchMode.COMPACT, relaunchRepo.getSavedMode())
    }
}
