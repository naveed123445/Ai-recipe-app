package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberGoldDark
import com.example.ui.theme.AmberGoldLight
import com.example.ui.theme.BasilGreen
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalElevated
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FoodAlchemistViewModel

@Composable
fun ProfileScreen(
    viewModel: FoodAlchemistViewModel,
    modifier: Modifier = Modifier
) {
    val kitchenStock by viewModel.kitchenIngredients.collectAsState()
    val savedRecipes by viewModel.savedRecipes.collectAsState()

    var isVegetarian by remember { mutableStateOf(false) }
    var isGlutenFree by remember { mutableStateOf(false) }
    var isHighProtein by remember { mutableStateOf(true) }
    var isVoiceAutoRead by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // User Avatar and Name
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CharcoalElevated)
                    .border(1.dp, CharcoalBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(AmberGoldDark)
                ) {
                    Text(text = "🧙‍♂️", fontSize = 32.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Master Alchemist",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = CreamWhite,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Kitchen Scientist Level 5",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AmberGoldLight,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        // Alchemist Stats Cards
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard(
                    title = "Recipes Invented",
                    value = "${12 + savedRecipes.size}",
                    icon = "🧪",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Pantry Items",
                    value = "${kitchenStock.size}",
                    icon = "🧺",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Meals Cooked",
                    value = "18",
                    icon = "🍳",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Dietary Preferences
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CharcoalElevated)
                    .border(1.dp, CharcoalBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Dietary Preferences",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = isVegetarian,
                        onClick = { isVegetarian = !isVegetarian },
                        label = { Text("Vegetarian") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberGoldDark,
                            selectedLabelColor = CreamWhite,
                            containerColor = CharcoalCard,
                            labelColor = TextSecondary
                        )
                    )
                    FilterChip(
                        selected = isGlutenFree,
                        onClick = { isGlutenFree = !isGlutenFree },
                        label = { Text("Gluten-Free") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberGoldDark,
                            selectedLabelColor = CreamWhite,
                            containerColor = CharcoalCard,
                            labelColor = TextSecondary
                        )
                    )
                    FilterChip(
                        selected = isHighProtein,
                        onClick = { isHighProtein = !isHighProtein },
                        label = { Text("High Protein") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberGoldDark,
                            selectedLabelColor = CreamWhite,
                            containerColor = CharcoalCard,
                            labelColor = TextSecondary
                        )
                    )
                }
            }
        }

        // Voice Cooking Preferences
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CharcoalElevated)
                    .border(1.dp, CharcoalBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Voice Read Aloud",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Automatically narrate cooking steps hands-free",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }

                Switch(
                    checked = isVoiceAutoRead,
                    onCheckedChange = { isVoiceAutoRead = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AmberGold,
                        checkedTrackColor = AmberGoldDark.copy(alpha = 0.5f),
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = CharcoalCard
                    )
                )
            }
        }

        // AI Engine Info
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CharcoalCard)
                    .border(1.dp, BasilGreen.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Gemini",
                            tint = AmberGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gemini 3.5 Flash Engine",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Food Alchemist uses Gemini 3.5 Flash to synthesize culinary pairings, calculate cook chemistry, and suggest smart ingredient substitutions.",
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

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CharcoalElevated)
            .border(1.dp, CharcoalBorder, RoundedCornerShape(16.dp))
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Text(text = icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                color = AmberGoldLight,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
            )
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextSecondary,
                fontSize = 10.sp
            ),
            maxLines = 1
        )
    }
}
