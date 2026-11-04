package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.FormationSlotId
import com.example.data.Shinobi
import com.example.data.SquadFormation
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.AmberGlowDark
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.JadeGreen
import com.example.ui.theme.JadeGreenDark
import com.example.ui.theme.ParchmentGold
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

@Composable
fun FormationScreen(
    viewModel: DispatchViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(AppScreen.TRAINING_HALL)
    }

    val formation by viewModel.formation.collectAsStateWithLifecycle()
    val ownedShinobi by viewModel.ownedShinobi.collectAsStateWithLifecycle()
    val hallState by viewModel.hallState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var selectedSlotForEdit by remember { mutableStateOf<FormationSlotId?>(null) }

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Slate950, Slate900, Color(0xFF0A0F14))
                )
            )
            .testTag("formation_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(
                    top = statusBarPadding + 12.dp,
                    bottom = navBarPadding + 24.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Header
                FormationTopBar(
                    onBack = { viewModel.navigateTo(AppScreen.TRAINING_HALL) },
                    deployedCount = formation.deployedCount
                )

                // Tactical Positioning Rules Banner (§03, §20.2)
                TacticalRulesCard()

                // Frontline Grid Section (2 Slots)
                RowSectionHeader(
                    title = "FRONTLINE (2 SLOTS)",
                    subtitle = "Direct melee target absorption & vanguard interceptors"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FormationSlotCard(
                        slotId = FormationSlotId.FRONT_LEFT,
                        shinobi = formation.getShinobiInSlot(FormationSlotId.FRONT_LEFT),
                        hallLevel = hallState.hallLevel,
                        isSelected = selectedSlotForEdit == FormationSlotId.FRONT_LEFT,
                        onClick = { selectedSlotForEdit = FormationSlotId.FRONT_LEFT },
                        modifier = Modifier.weight(1f)
                    )

                    FormationSlotCard(
                        slotId = FormationSlotId.FRONT_RIGHT,
                        shinobi = formation.getShinobiInSlot(FormationSlotId.FRONT_RIGHT),
                        hallLevel = hallState.hallLevel,
                        isSelected = selectedSlotForEdit == FormationSlotId.FRONT_RIGHT,
                        onClick = { selectedSlotForEdit = FormationSlotId.FRONT_RIGHT },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Rearguard Grid Section (3 Slots)
                RowSectionHeader(
                    title = "REARGUARD (3 SLOTS)",
                    subtitle = "Sheltered from direct non-piercing melee strikes"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FormationSlotCard(
                        slotId = FormationSlotId.REAR_LEFT,
                        shinobi = formation.getShinobiInSlot(FormationSlotId.REAR_LEFT),
                        hallLevel = hallState.hallLevel,
                        isSelected = selectedSlotForEdit == FormationSlotId.REAR_LEFT,
                        onClick = { selectedSlotForEdit = FormationSlotId.REAR_LEFT },
                        modifier = Modifier.weight(1f)
                    )

                    FormationSlotCard(
                        slotId = FormationSlotId.REAR_CENTER,
                        shinobi = formation.getShinobiInSlot(FormationSlotId.REAR_CENTER),
                        hallLevel = hallState.hallLevel,
                        isSelected = selectedSlotForEdit == FormationSlotId.REAR_CENTER,
                        onClick = { selectedSlotForEdit = FormationSlotId.REAR_CENTER },
                        modifier = Modifier.weight(1f)
                    )

                    FormationSlotCard(
                        slotId = FormationSlotId.REAR_RIGHT,
                        shinobi = formation.getShinobiInSlot(FormationSlotId.REAR_RIGHT),
                        hallLevel = hallState.hallLevel,
                        isSelected = selectedSlotForEdit == FormationSlotId.REAR_RIGHT,
                        onClick = { selectedSlotForEdit = FormationSlotId.REAR_RIGHT },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Slot Assignment Editor (Shown when a slot is tapped)
                selectedSlotForEdit?.let { activeSlot ->
                    SlotAssignmentSheet(
                        activeSlot = activeSlot,
                        formation = formation,
                        ownedShinobi = ownedShinobi,
                        onAssign = { shinobiId ->
                            viewModel.assignShinobiToSlot(shinobiId, activeSlot)
                            selectedSlotForEdit = null
                        },
                        onClear = {
                            viewModel.assignShinobiToSlot(null, activeSlot)
                            selectedSlotForEdit = null
                        },
                        onSwap = { otherSlot ->
                            viewModel.swapFormationSlots(activeSlot, otherSlot)
                            selectedSlotForEdit = null
                        },
                        onDismiss = { selectedSlotForEdit = null }
                    )
                }

                // Action Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.resetFormationToDefault() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("reset_formation_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberGlow),
                        border = BorderStroke(1.dp, AmberGlow.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("RESET CANONICAL", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { viewModel.navigateTo(AppScreen.TRAINING_HALL) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("back_to_hall_from_formation"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = JadeGreen),
                        border = BorderStroke(1.dp, JadeGreen.copy(alpha = 0.5f))
                    ) {
                        Text("TRAINING HALL", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun FormationTopBar(
    onBack: () -> Unit,
    deployedCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("formation_nav_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Return to Training Hall",
                    tint = Slate200,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = "SQUAD FORMATION",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.5.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "2-FRONT / 3-REAR TACTICAL GRID (§03, §20.2)",
                    style = MaterialTheme.typography.labelSmall,
                    color = ParchmentGold,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp
                )
            }
        }

        Surface(
            color = JadeGreenDark.copy(alpha = 0.35f),
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(1.dp, JadeGreen),
            modifier = Modifier.testTag("deployed_count_badge")
        ) {
            Text(
                text = "$deployedCount / 5 DEPLOYED",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = JadeGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp
            )
        }
    }
}

@Composable
private fun TacticalRulesCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tactical_rules_card"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        border = BorderStroke(1.dp, Slate800)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(JadeGreenDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = JadeGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "TARGETING & VANGUARD SHELTER (§20.2)",
                    style = MaterialTheme.typography.labelSmall,
                    color = ParchmentGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp
                )
                Text(
                    text = "Enemy single-target melee strikes must target Frontline units first. Rearguard units remain sheltered until frontline defense is depleted.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate400,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
private fun RowSectionHeader(title: String, subtitle: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 12.5.sp,
            letterSpacing = 0.5.sp
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = Slate400,
            fontSize = 10.5.sp
        )
    }
}

@Composable
private fun FormationSlotCard(
    slotId: FormationSlotId,
    shinobi: Shinobi?,
    hallLevel: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOccupied = shinobi != null

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .testTag("formation_slot_${slotId.name}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOccupied) Slate850 else Slate900.copy(alpha = 0.6f)
        ),
        border = BorderStroke(
            1.5.dp,
            when {
                isSelected -> ParchmentGold
                isOccupied -> JadeGreen.copy(alpha = 0.6f)
                else -> Slate800
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Header: Slot Name + Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = slotId.displayName.replace(" (Reserved)", ""),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isOccupied) ParchmentGold else Slate400,
                    fontSize = 10.sp
                )

                Surface(
                    color = if (isOccupied) JadeGreenDark else Slate800,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (isOccupied) "ACTIVE" else "OPEN",
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isOccupied) JadeGreen else Slate400,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (shinobi != null) {
                // Shinobi Name & Title
                Text(
                    text = shinobi.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 12.5.sp
                )

                Text(
                    text = shinobi.primaryRole,
                    style = MaterialTheme.typography.bodySmall,
                    color = JadeGreen,
                    fontSize = 10.sp
                )

                // Stats preview inheriting Hall Level
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(Slate950.copy(alpha = 0.6f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MiniStat("HP", "${shinobi.baseHp}")
                    MiniStat("ATK", "${shinobi.baseAtk}")
                    MiniStat("DEF", "${shinobi.baseDef}")
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Slate600,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (slotId == FormationSlotId.REAR_RIGHT) "RESERVED" else "TAP TO ASSIGN",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate600,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = Slate400, fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = Slate200, fontSize = 9.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun SlotAssignmentSheet(
    activeSlot: FormationSlotId,
    formation: SquadFormation,
    ownedShinobi: List<Shinobi>,
    onAssign: (String) -> Unit,
    onClear: () -> Unit,
    onSwap: (FormationSlotId) -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("slot_assignment_sheet"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Slate850),
        border = BorderStroke(1.5.dp, ParchmentGold)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ASSIGNING: ${activeSlot.displayName.uppercase()}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Text(
                        text = activeSlot.tacticalRoleDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate400,
                        fontSize = 10.5.sp
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Slate400,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = "Select an available recruit to place in this slot:",
                style = MaterialTheme.typography.labelSmall,
                color = ParchmentGold,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )

            // Recruits options
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (recruit in ownedShinobi) {
                    val currentSlot = formation.findSlotForShinobi(recruit.id)
                    val isAlreadyHere = currentSlot == activeSlot

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(enabled = !isAlreadyHere) { onAssign(recruit.id) }
                            .testTag("assign_option_${recruit.id}"),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAlreadyHere) Slate900 else Slate800
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isAlreadyHere) JadeGreen else Slate700
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = recruit.name,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${recruit.primaryRole} · Preferred: ${recruit.preferredRow}",
                                    color = JadeGreen,
                                    fontSize = 10.sp
                                )
                            }

                            if (isAlreadyHere) {
                                Text(
                                    text = "CURRENT",
                                    color = JadeGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            } else if (currentSlot != null) {
                                Text(
                                    text = "SWAP FROM ${currentSlot.rowName}",
                                    color = AmberGlow,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            } else {
                                Text(
                                    text = "DEPLOY",
                                    color = ParchmentGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            // Quick actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onClear,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("clear_slot_button"),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonGlow),
                    border = BorderStroke(1.dp, CrimsonGlow.copy(alpha = 0.5f))
                ) {
                    Text("CLEAR SLOT", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Slate700,
                        contentColor = Color.White
                    )
                ) {
                    Text("DONE", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }
        }
    }
}
