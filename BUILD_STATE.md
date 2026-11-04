# BUILD STATE — The Broken Supply Route

## Current Checkpoint: Step 4 (Completed)
- **Project Stage:** Step 4 / Chapter 1 Advancement & 2-Front / 3-Rear Squad Formation
- **Source Authority:** Naruto Idle RPG Master Game Theory & Design Bible v0.10.32 (§03, §20.2, §32, §42, §52.2–52.4, §56.3)
- **Status:** Step 4 Implemented & Verified

---

### What Works & What Changed
1. **Funded Training Band ONB02 (`TrainingHallState.kt` & `TrainingHallRepository.kt`):**
   - Implemented Band ONB02 (Hall Levels 5→10):
     - 5 individual level upgrades: 5→6, 6→7, 7→8, 8→9, 9→10 @ 12 Ryo / 18 Shinobi EXP each = 60 Ryo / 90 EXP.
     - Funded Training Band Grant: +56 Ryo / +84 Shinobi EXP.
     - Strict Ledger Integrity: Prior wallet (8 Ryo / 12 EXP) + Grant (56 Ryo / 84 EXP) - Debit (60 Ryo / 90 EXP) = exactly **4 Ryo / 6 Shinobi EXP**.
     - Requires ONB01 completion as prerequisite.
     - Single atomic confirmation commits levels 5→10 and marks `receipt_ONB02_completed`.
     - Idempotent and crash-recoverable: no double-credit on session restore.
2. **2-Front / 3-Rear Tactical Squad Formation (`SquadFormation.kt` & `FormationRepository.kt`):**
   - Canonical 5-slot tactical grid per §§03, 20.2:
     - **Frontline Left:** K-01 Guardian (Iron Bulwark) — Primary vanguard interceptor; absorbs hostile melee target priority.
     - **Frontline Right:** K-03 Finisher (Execution Duelist) — Secondary frontline duelist; melee counter-strike.
     - **Rearguard Left:** K-12 Scout (Forward Signal Marker) — Flank signal, vulnerable mark debuff.
     - **Rearguard Center:** K-19 Reliable Striker (Tactical Vanguard) — Central fire support & consistent technique cadence.
     - **Rearguard Right:** Open / Reserved for the upcoming START01 support specialist.
   - Synchronous local persistence in `broken_supply_route_formation_prefs`.
   - Invariant enforcement: a single shinobi cannot be assigned to multiple slots simultaneously (auto-clears prior slot on move/swap).
   - Ability to swap slots, clear slots, or reset to canonical START00 defaults.
3. **Tactical Formation Screen (`FormationScreen.kt`):**
   - Visual 2-front / 3-rear layout with distinctive M3 dark ninja aesthetic (slate surfaces, parchment gold, jade glow).
   - Explains targeting shelter rules (§20.2): hostile single-target melee strikes must target frontline units first, keeping rearguard sheltered.
   - Accessible slot management interface: tapping any slot opens an assignment sheet with options to deploy, swap, or clear, with minimum 48×48dp touch targets and Compose test tags (`formation_slot_*`).
   - "RESET CANONICAL" button restores canonical layout instantly.
   - Integrated into Navigation stack with `BackHandler` returning to Training Hall.
4. **Training Hall Updates (`TrainingHallScreen.kt`):**
   - Added Band ONB02 card showing prerequisite check, grant ledger, and commit button.
   - Direct shortcut card and navigation bar button to launch the Squad Formation screen.
   - Live header receipt badge updates from `ONB01` to `RECEIPT: ONB02`.

---

### Checks Actually Run
- **Robolectric JVM Unit Tests:**
  - `TrainingHallRepositoryTest.fundAndTrainOnb02_advancesToLevel10AndLeavesExactFourRyoSixExp` — PASSED
  - `TrainingHallRepositoryTest.fundAndTrainOnb02_isStrictlyIdempotentOnRepeatCall` — PASSED
  - `TrainingHallRepositoryTest.fundAndTrainOnb02_requiresOnb01Prerequisite` — PASSED
  - `TrainingHallRepositoryTest.persistedTrainingHallState_survivesAppRelaunch` — PASSED
  - `TrainingHallRepositoryTest.crashRecovery_whenCreditGrantedBeforePurchase_resumesWithoutDoubleCredit` — PASSED
  - `FormationRepositoryTest.defaultFormation_hasExpectedStart00Deployment` — PASSED
  - `FormationRepositoryTest.assignShinobiToSlot_movesRecruitAndAvoidsDuplicates` — PASSED
  - `FormationRepositoryTest.swapSlots_exchangesPositionsCorrectly` — PASSED
  - `FormationRepositoryTest.formation_survivesAppRelaunch` — PASSED
  - `FormationRepositoryTest.resetToDefaults_restoresCanonicalStart00` — PASSED
  - `TrainingHallRepositoryTest.*` (all ONB01 & initial state tests) — PASSED
  - `RosterRepositoryTest.*` (all START00 tests) — PASSED
  - `DispatchPreferencesTest.*` (all mode tests) — PASSED
- **Compilation & Lint Verification:**
  - `compile_applet` — PASSED
  - `lint_applet` — PASSED

---

### Verification Instructions for Android
1. Launch the app on the streaming emulator.
2. Select your briefing mode (Guided or Compact) and tap **INSPECT START00 RECRUITS**.
3. Tap **PROCEED TO TRAINING HALL**:
   - Verify initial or existing state. If ONB01 is not committed, tap **COMMIT ONB01: TRAIN TO LEVEL 5** (wallet shows 8 Ryo / 12 EXP).
4. Inspect the **BAND ONB02: ADVANCEMENT (LEVEL 5 → 10)** card:
   - Verify the ledger shows: Wallet Prior (8R/12EXP) + Grant (+56R/+84EXP) - 5 Levels (-60R/-90EXP) = **4 Ryo / 6 Shinobi EXP**.
5. Tap **COMMIT ONB02: TRAIN TO LEVEL 10**:
   - Verify Hall Level updates to **Level 10 (Cap 20)**.
   - Verify the top wallet bar displays exactly **4 Ryo** and **6 Shinobi EXP**.
   - Verify the top-right badge displays **RECEIPT: ONB02**.
   - Verify the **SQUAD FORMATION UNLOCKED** card appears.
6. Tap **TACTICAL GRID** (or the bottom **FORMATION** button):
   - Observe the 2-Front / 3-Rear tactical grid:
     - Frontline: Front-Left (K-01 Guardian), Front-Right (K-03 Finisher).
     - Rearguard: Rear-Left (K-12 Scout), Rear-Center (K-19 Reliable Striker), Rear-Right (OPEN / RESERVED).
   - Verify the badge shows **4 / 5 DEPLOYED**.
7. Tap any slot (e.g. Front-Left or Rear-Right) to test the accessible assignment sheet:
   - Deploy or swap positions without dragging.
   - Tap **RESET CANONICAL** to restore the canonical START00 setup.
8. Tap Back (or system back gesture) to smoothly return to the Training Hall, then to Roster, and Dispatch.

---

### Single Recommended Next Step
- **Step 5:** Implement combat encounter C-01 against Border Patrol Scouts (Chapter 1 early combat test with turn order, frontline targeting absorption resolution, technique execution, and combat log preview).
