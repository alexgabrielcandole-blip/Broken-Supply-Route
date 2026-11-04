package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.DispatchMode
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.AmberGlowDark
import com.example.ui.theme.CrimsonAlert
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
fun DispatchScreen(
    viewModel: DispatchViewModel,
    modifier: Modifier = Modifier
) {
    val selectedMode by viewModel.selectedMode.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Slate950, Slate900, Color(0xFF0E131A))
                )
            )
            .testTag("dispatch_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(
                    top = statusBarPadding + 16.dp,
                    bottom = navBarPadding + 24.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Constrain content width for tablet / large screen adaptation
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header & Classification Tag
                DispatchHeader()

                // Story Dispatch Card (The Broken Supply Route)
                DispatchStoryCard()

                // Section Title: Mode Choice
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.mode_choice_heading),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate200,
                        fontSize = 17.sp
                    )
                    Text(
                        text = stringResource(R.string.mode_choice_subheading),
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate400,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }

                // Option 1: Guided Mode
                ModeSelectionCard(
                    mode = DispatchMode.GUIDED,
                    isSelected = selectedMode == DispatchMode.GUIDED,
                    title = stringResource(R.string.mode_guided_title),
                    tagline = stringResource(R.string.mode_guided_tag),
                    description = stringResource(R.string.mode_guided_desc),
                    icon = Icons.Default.Shield,
                    cardTestTag = "mode_guided_card",
                    radioTestTag = "mode_guided_radio",
                    onSelect = { viewModel.selectMode(DispatchMode.GUIDED) }
                )

                // Option 2: Compact Mode
                ModeSelectionCard(
                    mode = DispatchMode.COMPACT,
                    isSelected = selectedMode == DispatchMode.COMPACT,
                    title = stringResource(R.string.mode_compact_title),
                    tagline = stringResource(R.string.mode_compact_tag),
                    description = stringResource(R.string.mode_compact_desc),
                    icon = Icons.Default.Speed,
                    cardTestTag = "mode_compact_card",
                    radioTestTag = "mode_compact_radio",
                    onSelect = { viewModel.selectMode(DispatchMode.COMPACT) }
                )

                // Current Selection & Local Persistence Confirmation Card
                SelectionStatusCard(selectedMode = selectedMode)

                // Primary Next Action: Inspect START00 Recruits
                Button(
                    onClick = { viewModel.navigateTo(AppScreen.ROSTER) },
                    enabled = selectedMode != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("proceed_to_roster_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = JadeGreen,
                        contentColor = Slate950,
                        disabledContainerColor = Slate800,
                        disabledContentColor = Slate600
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedMode != null) "INSPECT START00 RECRUITS" else "SELECT A MODE TO PROCEED",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DispatchHeader() {
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
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(JadeGreen)
            )
            Text(
                text = "STEP 1 · DISPATCH & BRIEFING SETUP",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = JadeGreen,
                letterSpacing = 1.sp
            )
        }

        Surface(
            color = Slate800,
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, Slate700)
        ) {
            Text(
                text = "LOCAL SAVE ACTIVE",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = ParchmentGold,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun DispatchStoryCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dispatch_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Slate900
        ),
        border = BorderStroke(1.dp, ParchmentGold.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp, 20.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(ParchmentGold)
                )
                Column {
                    Text(
                        text = stringResource(R.string.dispatch_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ParchmentGold,
                        fontSize = 15.sp,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = stringResource(R.string.dispatch_subtitle),
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400,
                        fontSize = 11.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Slate800)
            )

            Text(
                text = stringResource(R.string.dispatch_body),
                style = MaterialTheme.typography.bodyMedium,
                color = Slate200,
                fontSize = 13.5.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun ModeSelectionCard(
    mode: DispatchMode,
    isSelected: Boolean,
    title: String,
    tagline: String,
    description: String,
    icon: ImageVector,
    cardTestTag: String,
    radioTestTag: String,
    onSelect: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) JadeGreen else Slate700,
        animationSpec = tween(200),
        label = "border_color"
    )

    val containerColor by animateColorAsState(
        targetValue = if (isSelected) Slate850 else Slate900,
        animationSpec = tween(200),
        label = "container_color"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                role = Role.RadioButton,
                onClick = onSelect
            )
            .semantics {
                this.role = Role.RadioButton
                this.contentDescription = "$title. $tagline. ${if (isSelected) "Currently selected" else "Not selected"}"
            }
            .testTag(cardTestTag),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor)
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) JadeGreenDark else Slate800),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) JadeGreen else Slate400,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Slate200,
                            fontSize = 16.sp
                        )
                        Text(
                            text = tagline,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) JadeGreen else Slate400,
                            fontSize = 11.5.sp
                        )
                    }
                }

                // Radio Button indicator with minimum interactive touch size
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .testTag(radioTestTag),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = stringResource(R.string.status_selected),
                            tint = JadeGreen,
                            modifier = Modifier.size(26.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.RadioButtonUnchecked,
                            contentDescription = stringResource(R.string.status_select),
                            tint = Slate600,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) Slate200 else Slate400,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            // Active badge
            AnimatedVisibility(visible = isSelected) {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = JadeGreenDark.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, JadeGreen.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = JadeGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "ACTIVE SELECTION (SAVED LOCALLY)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = JadeGreen,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectionStatusCard(selectedMode: DispatchMode?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("selected_mode_indicator"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selectedMode != null) Slate900 else Slate900.copy(alpha = 0.6f)
        ),
        border = BorderStroke(
            1.dp,
            if (selectedMode != null) JadeGreen.copy(alpha = 0.5f) else AmberGlowDark
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = if (selectedMode != null) JadeGreen else AmberGlow,
                modifier = Modifier.size(24.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = if (selectedMode != null) {
                        "${stringResource(R.string.selected_mode_label)}: ${selectedMode.name} MODE"
                    } else {
                        stringResource(R.string.no_selection_label)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedMode != null) Color.White else AmberGlow,
                    fontSize = 13.5.sp,
                    modifier = Modifier.testTag("selection_status_text")
                )

                Text(
                    text = stringResource(R.string.persisted_confirmation),
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate400,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
