package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Recipe
import com.example.ui.components.AudioWaveformIndicator
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberGoldDark
import com.example.ui.theme.AmberGoldLight
import com.example.ui.theme.BasilGreen
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalElevated
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FoodAlchemistViewModel

@Composable
fun CookingModeScreen(
    recipe: Recipe,
    viewModel: FoodAlchemistViewModel,
    modifier: Modifier = Modifier
) {
    val currentStepIndex by viewModel.activeCookingStep.collectAsState()
    val isVoiceModeActive by viewModel.isVoiceModeActive.collectAsState()
    val isTtsSpeaking by viewModel.isTtsSpeaking.collectAsState()
    val timerSeconds by viewModel.timerSeconds.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()

    val totalSteps = recipe.steps.size.coerceAtLeast(1)
    val safeIndex = currentStepIndex.coerceIn(0, totalSteps - 1)
    val currentStep = recipe.steps.getOrNull(safeIndex)

    val stepImageRes = if (recipe.imageDrawableName == "img_potato_fritters") {
        R.drawable.img_potato_fritters
    } else {
        R.drawable.img_spicy_skillet
    }

    LaunchedEffect(safeIndex) {
        if (currentStep?.durationMinutes != null && currentStep.durationMinutes > 0 && timerSeconds == 0) {
            viewModel.setTimer(currentStep.durationMinutes * 60)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalDark)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Header: Back + Progress + Step Count
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { viewModel.navigateBack() },
                            modifier = Modifier.testTag("cooking_mode_back")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = CreamWhite
                            )
                        }

                        Text(
                            text = "Step ${safeIndex + 1} of $totalSteps",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = AmberGoldLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )

                        IconButton(
                            onClick = {
                                if (currentStep != null) {
                                    viewModel.speakStep("${currentStep.title}. ${currentStep.instruction}")
                                }
                            },
                            modifier = Modifier.testTag("tts_read_step")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Read Aloud",
                                tint = AmberGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress bar
                    LinearProgressIndicator(
                        progress = { (safeIndex + 1).toFloat() / totalSteps },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = AmberGold,
                        trackColor = CharcoalBorder
                    )
                }
            }

            // Step Image
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(220.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .border(1.dp, CharcoalBorder, RoundedCornerShape(22.dp))
                ) {
                    Image(
                        painter = painterResource(id = stepImageRes),
                        contentDescription = currentStep?.title ?: "Cooking step",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        CharcoalDark.copy(alpha = 0.6f)
                                    )
                                )
                            )
                    )
                }
            }

            // Step Title and Instruction
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = currentStep?.title ?: "Cooking Step",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = CreamWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentStep?.instruction ?: "",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = TextPrimary,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )
                    )
                }
            }

            // Optional Step Timer
            if (timerSeconds > 0 || isTimerRunning) {
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    val minutes = timerSeconds / 60
                    val seconds = timerSeconds % 60
                    val timeStr = "%02d:%02d".format(minutes, seconds)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CharcoalElevated)
                            .border(1.dp, AmberGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⏱️", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Step Timer",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                )
                                Text(
                                    text = timeStr,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = AmberGoldLight,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 22.sp
                                    )
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.toggleTimer() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isTimerRunning) FlameOrange else AmberGold,
                                contentColor = CharcoalDark
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("btn_timer_toggle")
                        ) {
                            Icon(
                                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isTimerRunning) "Pause" else "Start",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isTimerRunning) "Pause" else "Start",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Pro Tip Card
            if (!currentStep?.tip.isNullOrBlank()) {
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CharcoalElevated)
                            .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(AmberGoldDark)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Tip",
                                    tint = CreamWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Tip",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = AmberGoldLight,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = currentStep?.tip ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        lineHeight = 18.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Step Navigation Buttons: [Previous] [Next / Finish]
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.setCookingStep(safeIndex - 1, totalSteps, recipe)
                        },
                        enabled = safeIndex > 0,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("btn_step_previous")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous",
                            tint = if (safeIndex > 0) TextPrimary else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Previous",
                            color = if (safeIndex > 0) TextPrimary else TextMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = {
                            if (safeIndex < totalSteps - 1) {
                                viewModel.setCookingStep(safeIndex + 1, totalSteps, recipe)
                            } else {
                                viewModel.navigateBack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberGold,
                            contentColor = CharcoalDark
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("btn_step_next")
                    ) {
                        Text(
                            text = if (safeIndex < totalSteps - 1) "Next" else "Finish 🎉",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = if (safeIndex < totalSteps - 1) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Check,
                            contentDescription = "Next",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Voice Cooking Mode Bottom Panel (Floating)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(CharcoalElevated)
                .border(1.dp, AmberGold.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .clickable { viewModel.toggleVoiceMode(recipe) }
                .testTag("voice_cooking_panel")
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (isVoiceModeActive) AmberGold else CharcoalCard)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Mode",
                            tint = if (isVoiceModeActive) CharcoalDark else AmberGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Voice Cooking Mode",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = if (isVoiceModeActive) "Say 'Next step' while cooking" else "Tap to enable hands-free voice read",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isVoiceModeActive) AmberGoldLight else TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                AudioWaveformIndicator(
                    isSpeaking = isTtsSpeaking || isVoiceModeActive,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}
