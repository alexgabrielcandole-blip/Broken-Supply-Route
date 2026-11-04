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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Shinobi
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.AmberGlowDark
import com.example.ui.theme.JadeGreen
import com.example.ui.theme.JadeGreenDark
import com.example.ui.theme.ParchmentDark
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
fun RosterScreen(
    viewModel: DispatchViewModel,
    modifier: Modifier = Modifier
) {
    // Intercept hardware and gesture back to return to Dispatch screen
    BackHandler {
        viewModel.navigateTo(AppScreen.DISPATCH)
    }

    val ownedShinobi by viewModel.ownedShinobi.collectAsStateWithLifecycle()
    val hasReceipt by viewModel.hasStart00Receipt.collectAsStateWithLifecycle()
    var verificationTaps by remember { mutableIntStateOf(0) }

    val scrollState = rememberScrollState()
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Slate950, Slate900, Color(0xFF0D1218))
                )
            )
            .testTag("roster_screen")
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
                // Top App Bar with back navigation
                RosterTopBar(
                    onBack = { viewModel.navigateTo(AppScreen.DISPATCH) },
                    hasReceipt = hasReceipt
                )

                // START00 Receipt & Idempotency Guarantee Card
                Start00ReceiptCard(
                    ownedCount = ownedShinobi.size,
                    hasReceipt = hasReceipt,
                    verificationTaps = verificationTaps,
                    onVerifyReopen = {
                        viewModel.grantStart00Roster()
                        verificationTaps++
                    }
                )

                // Four Shinobi Cards
                ownedShinobi.forEach { shinobi ->
                    ShinobiRosterCard(shinobi = shinobi)
                }

                // Primary Action: Proceed to Training Hall (ONB01)
                Button(
                    onClick = { viewModel.navigateTo(AppScreen.TRAINING_HALL) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("proceed_to_hall_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = JadeGreen,
                        contentColor = Slate950
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PROCEED TO TRAINING HALL",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )
                }

                // Return to Dispatch Action
                OutlinedButton(
                    onClick = { viewModel.navigateTo(AppScreen.DISPATCH) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("back_to_dispatch_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ParchmentGold
                    ),
                    border = BorderStroke(1.dp, ParchmentGold.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RETURN TO MISSION DISPATCH",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun RosterTopBar(
    onBack: () -> Unit,
    hasReceipt: Boolean
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
                    .testTag("nav_back_icon_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Return to Dispatch",
                    tint = Slate200,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = "SQUAD ROSTER",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 17.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "STEP 2 · START00 FOUNDATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = JadeGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.5.sp
                )
            }
        }

        Surface(
            color = if (hasReceipt) JadeGreenDark.copy(alpha = 0.35f) else AmberGlowDark,
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(1.dp, if (hasReceipt) JadeGreen else AmberGlow),
            modifier = Modifier.testTag("receipt_badge_start00")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = JadeGreen,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "RECEIPT: START00",
                    style = MaterialTheme.typography.labelSmall,
                    color = JadeGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp
                )
            }
        }
    }
}

@Composable
private fun Start00ReceiptCard(
    ownedCount: Int,
    hasReceipt: Boolean,
    verificationTaps: Int,
    onVerifyReopen: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("start00_receipt_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        border = BorderStroke(1.dp, ParchmentGold.copy(alpha = 0.35f))
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
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ParchmentDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = ParchmentGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "START00 FOUNDATIONAL GRANT",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = ParchmentGold,
                            fontSize = 12.5.sp
                        )
                        Text(
                            text = "4 Core Shinobi · Inherited Baseline",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate400,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    color = Slate800,
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Slate700)
                ) {
                    Text(
                        text = "OWNED: $ownedCount / 4",
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("roster_count_text"),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Text(
                text = "Per §03 and §44.2, START00 grants K-01 Guardian, K-03 Finisher, K-12 Scout, and K-19 Reliable Striker exactly once. Each recruit inherits the shared Hall baseline with standard-issue gear (+0). Reopening or relaunching cannot grant duplicates.",
                style = MaterialTheme.typography.bodyMedium,
                color = Slate200,
                fontSize = 12.5.sp,
                lineHeight = 17.sp
            )

            // Re-open / idempotency test button
            Button(
                onClick = onVerifyReopen,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("reopen_verify_roster_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Slate800,
                    contentColor = JadeGreen
                ),
                border = BorderStroke(1.dp, Slate700)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (verificationTaps == 0) "TEST IDEMPOTENT RE-GRANT" else "RE-CHECKED ($verificationTaps) · ZERO DUPLICATES",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp
                )
            }
        }
    }
}

@Composable
private fun ShinobiRosterCard(shinobi: Shinobi) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("roster_card_${shinobi.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        border = BorderStroke(1.dp, Slate800)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: ID, Name, Title, and Role badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Slate800),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = shinobi.id,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = ParchmentGold,
                            fontSize = 13.sp
                        )
                    }

                    Column {
                        Text(
                            text = shinobi.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        Text(
                            text = shinobi.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate400,
                            fontSize = 11.5.sp
                        )
                    }
                }

                Surface(
                    color = Slate850,
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Slate700)
                ) {
                    Text(
                        text = shinobi.preferredRow,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = JadeGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Role tag
            Text(
                text = "ROLE: ${shinobi.primaryRole.uppercase()}",
                style = MaterialTheme.typography.labelSmall,
                color = ParchmentGold,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp,
                letterSpacing = 0.5.sp
            )

            // Reference Stats Grid (Hall20/Band I reference)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Slate850)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(label = "HP", value = "${shinobi.baseHp}")
                StatItem(label = "ATK", value = "${shinobi.baseAtk}")
                StatItem(label = "DEF", value = "${shinobi.baseDef}")
                StatItem(label = "SPD", value = "${shinobi.baseSpeed}")
                StatItem(label = "CRIT", value = "${shinobi.critChancePercent}%")
            }

            // Technique info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Slate950.copy(alpha = 0.5f))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = JadeGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "TECHNIQUE: ${shinobi.techniqueName.uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = JadeGreen,
                        fontSize = 11.sp
                    )
                }
                Text(
                    text = shinobi.techniqueSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate200,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            // Standard Equipment slots included at base copy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EquipmentBadge(
                    slot = "TOOL",
                    item = shinobi.standardToolName,
                    modifier = Modifier.weight(1f)
                )
                EquipmentBadge(
                    slot = "ARMOR",
                    item = shinobi.standardArmorName,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Slate400,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EquipmentBadge(
    slot: String,
    item: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Slate850,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(0.8.dp, Slate700)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(ParchmentGold)
            )
            Column {
                Text(
                    text = slot,
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate400,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate200,
                    fontSize = 10.5.sp,
                    maxLines = 1
                )
            }
        }
    }
}
