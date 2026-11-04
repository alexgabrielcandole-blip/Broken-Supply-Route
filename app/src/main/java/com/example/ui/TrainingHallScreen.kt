package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.TrendingUp
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
import com.example.data.TrainingHallState
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.AmberGlowDark
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
fun TrainingHallScreen(
    viewModel: DispatchViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(AppScreen.ROSTER)
    }

    val hallState by viewModel.hallState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Slate950, Slate900, Color(0xFF0C1117))
                )
            )
            .testTag("training_hall_screen")
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
                // Top Bar
                TrainingHallTopBar(
                    onBack = { viewModel.navigateTo(AppScreen.ROSTER) },
                    onb01Completed = hallState.onb01Completed,
                    onb02Completed = hallState.onb02Completed
                )

                // Account Wallet Summary Bar
                WalletSummaryBar(hallState = hallState)

                // Current Shared Hall Status Card
                HallLevelStatusCard(hallState = hallState)

                // Band ONB01 Card (Levels 1 -> 5)
                Onb01BandCard(
                    hallState = hallState,
                    onCommit = { viewModel.fundAndTrainOnb01() }
                )

                // Band ONB02 Card (Levels 5 -> 10)
                Onb02BandCard(
                    hallState = hallState,
                    onCommit = { viewModel.fundAndTrainOnb02() }
                )

                // Formation Shortcut Card (Accessible once ONB02 is complete or active)
                if (hallState.onb02Completed) {
                    FormationActionCard(
                        onNavigateFormation = { viewModel.navigateTo(AppScreen.FORMATION) }
                    )
                }

                // E0 Idle Checkpoint Anchor Card
                E0CheckpointCard(isE0Unlocked = hallState.isE0Unlocked)

                // Navigation buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(AppScreen.ROSTER) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("back_to_roster_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate200),
                        border = BorderStroke(1.dp, Slate700)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ROSTER", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.FORMATION) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("nav_formation_from_hall"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (hallState.onb02Completed) ParchmentGold else Slate800,
                            contentColor = if (hallState.onb02Completed) Slate950 else Slate400
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("FORMATION", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun TrainingHallTopBar(
    onBack: () -> Unit,
    onb01Completed: Boolean,
    onb02Completed: Boolean
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
                    .testTag("hall_nav_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Return to Roster",
                    tint = Slate200,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = "SHINOBI TRAINING HALL",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.5.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "STEP 4 · ONB02 FUNDED BAND & SQUAD FORMATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = JadeGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp
                )
            }
        }

        val badgeText = when {
            onb02Completed -> "RECEIPT: ONB02"
            onb01Completed -> "RECEIPT: ONB01"
            else -> "ONB01 PENDING"
        }
        val badgeColor = if (onb01Completed) JadeGreen else AmberGlow
        val badgeBg = if (onb01Completed) JadeGreenDark.copy(alpha = 0.35f) else AmberGlowDark

        Surface(
            color = badgeBg,
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(1.dp, badgeColor),
            modifier = Modifier.testTag("receipt_badge_hall")
        ) {
            Text(
                text = badgeText,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = badgeColor,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp
            )
        }
    }
}

@Composable
private fun WalletSummaryBar(hallState: TrainingHallState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("wallet_summary_bar"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        border = BorderStroke(1.dp, Slate800)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WalletItem(label = "RYO", value = "${hallState.ryo}", color = ParchmentGold, tag = "wallet_ryo")
            WalletItem(label = "SHINOBI EXP", value = "${hallState.shinobiExp}", color = JadeGreen, tag = "wallet_exp")
            WalletItem(label = "ESSENCE", value = "${hallState.commonEssence}", color = Slate400, tag = "wallet_essence")
            WalletItem(label = "ENHANCE", value = "${hallState.enhancementMaterial}", color = Slate400, tag = "wallet_enhance")
        }
    }
}

@Composable
private fun WalletItem(
    label: String,
    value: String,
    color: Color,
    tag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.testTag(tag)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Slate400,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun HallLevelStatusCard(hallState: TrainingHallState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hall_level_display"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        border = BorderStroke(1.dp, JadeGreen.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "SHARED SQUAD BASELINE",
                    style = MaterialTheme.typography.labelSmall,
                    color = ParchmentGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Hall Level ${hallState.hallLevel} (Cap ${hallState.hallCap})",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )
                Text(
                    text = "All 4 START00 recruits inherit this baseline directly with standard-issue gear (+0).",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate400,
                    fontSize = 11.5.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(JadeGreenDark),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Lv ${hallState.hallLevel}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = JadeGreen,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun Onb01BandCard(
    hallState: TrainingHallState,
    onCommit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("onb01_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Slate850),
        border = BorderStroke(
            1.dp,
            if (hallState.onb01Completed) JadeGreen else AmberGlow.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (hallState.onb01Completed) JadeGreen else AmberGlow)
                    )
                    Text(
                        text = "BAND ONB01: INTRODUCTORY (LEVEL 1 → 5)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.5.sp
                    )
                }

                Surface(
                    color = if (hallState.onb01Completed) JadeGreenDark else Slate800,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (hallState.onb01Completed) "COMMITTED" else "FUNDED STEP",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (hallState.onb01Completed) JadeGreen else AmberGlow,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Slate950.copy(alpha = 0.5f))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LedgerRow(label = "Band Grant:", amount = "+40 Ryo / +60 EXP", color = JadeGreen)
                LedgerRow(label = "4 Levels (1→5):", amount = "-32 Ryo / -48 EXP", color = AmberGlow)
                LedgerRow(label = "Remainder Post-ONB01:", amount = "8 Ryo / 12 EXP", color = ParchmentGold)
            }

            Button(
                onClick = onCommit,
                enabled = !hallState.onb01Completed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("train_onb01_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = JadeGreen,
                    contentColor = Slate950,
                    disabledContainerColor = Slate800,
                    disabledContentColor = JadeGreen
                )
            ) {
                if (hallState.onb01Completed) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = JadeGreen
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BAND ONB01 COMPLETED · LEVEL 5 ACTIVE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = JadeGreen
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "COMMIT ONB01: TRAIN TO LEVEL 5",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun Onb02BandCard(
    hallState: TrainingHallState,
    onCommit: () -> Unit
) {
    val isAvailable = hallState.onb01Completed
    val isCompleted = hallState.onb02Completed

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("onb02_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAvailable) Slate850 else Slate900.copy(alpha = 0.6f)
        ),
        border = BorderStroke(
            1.dp,
            when {
                isCompleted -> JadeGreen
                isAvailable -> AmberGlow.copy(alpha = 0.6f)
                else -> Slate800
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCompleted -> JadeGreen
                                    isAvailable -> AmberGlow
                                    else -> Slate600
                                }
                            )
                    )
                    Text(
                        text = "BAND ONB02: ADVANCEMENT (LEVEL 5 → 10)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isAvailable) Color.White else Slate400,
                        fontSize = 13.5.sp
                    )
                }

                Surface(
                    color = when {
                        isCompleted -> JadeGreenDark
                        isAvailable -> Slate800
                        else -> Slate900
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = when {
                            isCompleted -> "COMMITTED"
                            isAvailable -> "READY"
                            else -> "LOCKED"
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            isCompleted -> JadeGreen
                            isAvailable -> AmberGlow
                            else -> Slate600
                        },
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Slate950.copy(alpha = 0.5f))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LedgerRow(label = "Wallet Prior to Band:", amount = "8 Ryo / 12 Shinobi EXP", color = Slate400)
                LedgerRow(label = "Band ONB02 Grant:", amount = "+56 Ryo / +84 Shinobi EXP", color = JadeGreen)
                LedgerRow(label = "5 Levels (5→10 @ 12R/18EXP):", amount = "-60 Ryo / -90 Shinobi EXP", color = AmberGlow)
                LedgerRow(label = "Net Account Remainder:", amount = "4 Ryo / 6 Shinobi EXP", color = ParchmentGold)
            }

            Text(
                text = "Advances entire squad to Level 10 baseline. Fully funds 5-slot tactical grid deployment per §03 and §52.2.",
                style = MaterialTheme.typography.bodySmall,
                color = Slate400,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Button(
                onClick = onCommit,
                enabled = isAvailable && !isCompleted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("train_onb02_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmberGlow,
                    contentColor = Slate950,
                    disabledContainerColor = Slate800,
                    disabledContentColor = if (isCompleted) JadeGreen else Slate600
                )
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = JadeGreen
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BAND ONB02 COMPLETED · LEVEL 10 ACTIVE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = JadeGreen
                    )
                } else if (!isAvailable) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Slate600
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LOCKED · COMPLETE BAND ONB01 FIRST",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Slate600
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "COMMIT ONB02: TRAIN TO LEVEL 10",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FormationActionCard(
    onNavigateFormation: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("formation_action_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Slate850),
        border = BorderStroke(1.dp, ParchmentGold.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = "SQUAD FORMATION UNLOCKED",
                    style = MaterialTheme.typography.labelSmall,
                    color = ParchmentGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "2-Front / 3-Rear Positioning Grid",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
                Text(
                    text = "Configure frontline vanguard interceptors and protected rearguard units (§§03, 20.2).",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate400,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = onNavigateFormation,
                modifier = Modifier
                    .height(44.dp)
                    .testTag("proceed_to_formation_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ParchmentGold,
                    contentColor = Slate950
                )
            ) {
                Text("TACTICAL GRID", fontWeight = FontWeight.Black, fontSize = 11.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun LedgerRow(label: String, amount: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Slate400,
            fontSize = 11.5.sp
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 11.5.sp
        )
    }
}

@Composable
private fun E0CheckpointCard(isE0Unlocked: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("e0_status_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        border = BorderStroke(
            1.dp,
            if (isE0Unlocked) JadeGreen.copy(alpha = 0.5f) else Slate800
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isE0Unlocked) JadeGreenDark else Slate800),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isE0Unlocked) Icons.Default.AccessTime else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (isE0Unlocked) JadeGreen else Slate400,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "CHECKPOINT E0 · IDLE ANCHOR",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isE0Unlocked) Color.White else Slate400,
                            fontSize = 13.5.sp
                        )
                        Text(
                            text = if (isE0Unlocked) "IDLE01 Accrual Active" else "Unlocks at ONB01 Completion",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isE0Unlocked) JadeGreen else AmberGlow,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    color = if (isE0Unlocked) JadeGreenDark.copy(alpha = 0.35f) else Slate850,
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, if (isE0Unlocked) JadeGreen else Slate700)
                ) {
                    Text(
                        text = if (isE0Unlocked) "UNLOCKED" else "LOCKED",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isE0Unlocked) JadeGreen else Slate400,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            AnimatedVisibility(visible = isE0Unlocked) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Base hourly idle accrual vector (§42):",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate200,
                        fontSize = 11.5.sp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Slate850)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        RateChip("10 Ryo/h", ParchmentGold)
                        RateChip("20 EXP/h", JadeGreen)
                        RateChip("2 Ess/h", Slate400)
                        RateChip("1 Enh/h", Slate400)
                    }
                    Text(
                        text = "Accrual runs at 100% for the first 24h, then 25% for 48h (capped at 72h / 36 full-rate hours) per §32.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate400,
                        fontSize = 10.5.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RateChip(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
    )
}
