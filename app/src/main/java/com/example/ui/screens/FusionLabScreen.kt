package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DefaultIngredients
import com.example.data.model.Ingredient
import com.example.ui.components.GlowingGradientButton
import com.example.ui.components.IngredientAvatar
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberGoldDark
import com.example.ui.theme.AmberGoldLight
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
import com.example.ui.viewmodel.Screen

data class GoalOption(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val description: String
)

@Composable
fun FusionLabScreen(
    viewModel: FoodAlchemistViewModel,
    modifier: Modifier = Modifier
) {
    val selectedIngredients by viewModel.selectedFusionIngredients.collectAsState()
    val selectedGoal by viewModel.selectedGoal.collectAsState()
    val isInventing by viewModel.isInventing.collectAsState()
    val kitchenStock by viewModel.kitchenIngredients.collectAsState()
    val stockIdeas by viewModel.stockMealIdeas.collectAsState()

    var showIngredientPicker by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    val goals = listOf(
        GoalOption("Quick", "Quick", "⚡", "Under 20 minutes"),
        GoalOption("Healthy", "Healthy", "🥗", "Nutrient-packed"),
        GoalOption("Spicy", "Spicy", "🌶️", "Fiery & bold"),
        GoalOption("Comfort Food", "Comfort Food", "🍲", "Warm & hearty"),
        GoalOption("Unique", "Unique", "✨", "Wild culinary fusion"),
        GoalOption("Any Style", "Any Style", "👨‍🍳", "Chef's intuition"),
        GoalOption("Sweet", "Sweet", "🍰", "Dessert or sweet treat"),
        GoalOption("Kid-Friendly", "Kid-Friendly", "🧒", "Mild & fun flavors")
    )

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Got It", color = AmberGold)
                }
            },
            title = {
                Text("About Fusion Lab", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Fusion Lab acts as your personal AI culinary scientist. Select whatever ingredients you have in your kitchen, pick a goal, and watch as Gemini API synthesizes a completely new recipe tailored to your pantry.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            containerColor = CharcoalElevated
        )
    }

    if (showIngredientPicker) {
        AlertDialog(
            onDismissRequest = { showIngredientPicker = false },
            confirmButton = {
                TextButton(onClick = { showIngredientPicker = false }) {
                    Text("Done", color = AmberGold)
                }
            },
            title = {
                Text("Select Ingredients", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(280.dp)
                ) {
                    items(DefaultIngredients.commonIngredients) { item ->
                        val isPicked = selectedIngredients.any { it.name.equals(item.name, ignoreCase = true) }
                        IngredientAvatar(
                            emoji = item.emoji,
                            name = item.name,
                            isSelected = isPicked,
                            onClick = {
                                viewModel.toggleFusionIngredient(item)
                            }
                        )
                    }
                }
            },
            containerColor = CharcoalElevated
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.testTag("fusion_lab_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = "Fusion Lab",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = CreamWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )

                IconButton(
                    onClick = { showInfoDialog = true },
                    modifier = Modifier.testTag("fusion_info_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = TextSecondary
                    )
                }
            }
        }

        // Subtitle prompt
        item {
            Text(
                buildAnnotatedString {
                    append("Tell me what you have, and I'll ")
                    withStyle(SpanStyle(color = AmberGoldLight, fontWeight = FontWeight.Bold)) {
                        append("invent")
                    }
                    append(" something ")
                    withStyle(SpanStyle(color = AmberGoldLight, fontWeight = FontWeight.Bold)) {
                        append("delicious.")
                    }
                },
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextSecondary,
                    fontSize = 16.sp
                )
            )
        }

        // Section: What do you have?
        item {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "What do you have?",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )

                    Text(
                        text = "${selectedIngredients.size} selected",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AmberGold,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(selectedIngredients) { item ->
                        IngredientAvatar(
                            emoji = item.emoji,
                            name = item.name,
                            isSelected = true,
                            onClick = {
                                viewModel.removeFusionIngredient(item.name)
                            }
                        )
                    }

                    // Add more ingredients button
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { showIngredientPicker = true }
                                .testTag("btn_add_more_ingredients")
                                .padding(6.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(CharcoalElevated)
                                    .border(1.dp, AmberGold.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add More",
                                    tint = AmberGold,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Add more",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = AmberGold,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section: What do you want? (Goal Selector Grid)
        item {
            Column {
                Text(
                    text = "What do you want?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                ) {
                    items(goals) { goal ->
                        val isSelected = selectedGoal == goal.id
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isSelected) AmberGoldDark.copy(alpha = 0.3f) else CharcoalElevated)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) AmberGold else CharcoalBorder,
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .clickable { viewModel.selectGoal(goal.id) }
                                .testTag("goal_option_${goal.id.lowercase().replace(" ", "_")}")
                                .padding(vertical = 12.dp, horizontal = 6.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = goal.iconEmoji,
                                    fontSize = 24.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = goal.name,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isSelected) CreamWhite else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Invent a Recipe Action Button
        item {
            if (isInventing) {
                AlchemistBrewingAnimation()
            } else {
                GlowingGradientButton(
                    text = "✨ Invent a Recipe",
                    onClick = { viewModel.inventRecipe() },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Invent",
                            tint = CharcoalDark,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    testTag = "btn_invent_recipe"
                )
            }
        }

        // Secondary Action: Generate Ideas From Stock (User explicitly requested!)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CharcoalElevated)
                    .border(1.dp, CharcoalBorder, RoundedCornerShape(16.dp))
                    .clickable { viewModel.generateMealIdeasFromStock() }
                    .testTag("btn_fusion_generate_stock_ideas")
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🧺", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Generate Ideas from Kitchen Stock",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Use all ${kitchenStock.size} pantry items to invent concepts",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Go",
                        tint = AmberGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Stock Ideas Quick Cards
        if (stockIdeas.isNotEmpty()) {
            item {
                Column {
                    Text(
                        text = "Suggested Concepts",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    stockIdeas.forEach { idea ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(CharcoalCard)
                                .border(1.dp, CharcoalBorder, RoundedCornerShape(14.dp))
                                .clickable { viewModel.navigateTo(Screen.RecipeDetail(idea)) }
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = idea.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "${idea.cookTimeMinutes} min • ${idea.difficulty} • ${idea.matchPercentage}% match",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = AmberGoldLight,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "View",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlchemistBrewingAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "brew")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CharcoalElevated)
            .border(1.dp, AmberGold.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                color = AmberGold,
                modifier = Modifier.size(60.dp),
                strokeWidth = 3.dp
            )
            Text(
                text = "⚗️",
                fontSize = 28.sp,
                modifier = Modifier.rotate(rotation * 0.1f)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Brewing Culinary Alchemy...",
            style = MaterialTheme.typography.titleMedium.copy(
                color = AmberGoldLight,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Synthesizing flavor notes, cook times, and smart techniques.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        )
    }
}
