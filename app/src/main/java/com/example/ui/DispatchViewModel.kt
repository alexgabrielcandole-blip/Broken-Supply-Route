package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.DispatchMode
import com.example.data.DispatchPreferencesRepository
import com.example.data.FormationRepository
import com.example.data.FormationSlotId
import com.example.data.RosterRepository
import com.example.data.Shinobi
import com.example.data.SquadFormation
import com.example.data.TrainingHallRepository
import com.example.data.TrainingHallState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppScreen {
    DISPATCH,
    ROSTER,
    TRAINING_HALL,
    FORMATION
}

class DispatchViewModel(
    application: Application,
    private val dispatchRepo: DispatchPreferencesRepository = DispatchPreferencesRepository.getInstance(application),
    private val rosterRepo: RosterRepository = RosterRepository.getInstance(application),
    private val hallRepo: TrainingHallRepository = TrainingHallRepository.getInstance(application),
    private val formationRepo: FormationRepository = FormationRepository.getInstance(application)
) : AndroidViewModel(application) {

    val selectedMode: StateFlow<DispatchMode?> = dispatchRepo.selectedMode
    val ownedShinobi: StateFlow<List<Shinobi>> = rosterRepo.ownedShinobi
    val hasStart00Receipt: StateFlow<Boolean> = rosterRepo.hasStart00Receipt
    val hallState: StateFlow<TrainingHallState> = hallRepo.hallState
    val formation: StateFlow<SquadFormation> = formationRepo.formation

    private val _currentScreen = MutableStateFlow(AppScreen.DISPATCH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    fun selectMode(mode: DispatchMode) {
        dispatchRepo.saveMode(mode)
    }

    /**
     * Idempotently claims or re-validates the START00 introductory roster.
     */
    fun grantStart00Roster() {
        rosterRepo.grantStart00()
    }

    /**
     * Executes the funded ONB01 training band:
     * - Credits 40 Ryo / 60 EXP
     * - Purchases Hall levels 1→5 for 32 Ryo / 48 EXP
     * - Retains 8 Ryo / 12 EXP
     * - Unlocks the E0 idle anchor
     */
    fun fundAndTrainOnb01() {
        hallRepo.fundAndTrainOnb01()
    }

    /**
     * Executes the funded ONB02 training band (§03, §52.2):
     * - Prerequisite: ONB01 completed
     * - Credits 56 Ryo / 84 EXP
     * - Purchases Hall levels 5→10 for 60 Ryo / 90 EXP
     * - Retains 4 Ryo / 6 EXP
     * - Advances Hall to Level 10
     */
    fun fundAndTrainOnb02() {
        hallRepo.fundAndTrainOnb02()
    }

    /**
     * Assigns a shinobi to a specific formation slot (or clears if null).
     */
    fun assignShinobiToSlot(shinobiId: String?, targetSlot: FormationSlotId) {
        formationRepo.assignShinobiToSlot(shinobiId, targetSlot)
    }

    /**
     * Swaps the occupants of two formation slots.
     */
    fun swapFormationSlots(slotA: FormationSlotId, slotB: FormationSlotId) {
        formationRepo.swapSlots(slotA, slotB)
    }

    /**
     * Resets formation to canonical START00 deployment.
     */
    fun resetFormationToDefault() {
        formationRepo.resetToDefaults()
    }

    fun navigateTo(screen: AppScreen) {
        if (screen == AppScreen.ROSTER) {
            grantStart00Roster()
        }
        _currentScreen.value = screen
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DispatchViewModel::class.java)) {
                return DispatchViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
