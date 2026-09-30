package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Recipe
import com.example.ui.components.GlowingGradientButton
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
import com.example.ui.viewmodel.Screen

@Composable
fun RecipeDetailScreen(
    recipe: Recipe,
    viewModel: FoodAlchemistViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentServings by viewModel.currentRecipeServings.collectAsState()
    val savedRecipes by viewModel.savedRecipes.collectAsState()
    val isSaved = savedRecipes.any { it.title == recipe.title }

    val imageRes = if (recipe.imageDrawableName == "img_potato_fritters") {
        R.drawable.img_potato_fritters
    } else {
        R.drawable.img_spicy_skillet
    }

    val scaleFactor = currentServings.toFloat() / recipe.servings.coerceAtLeast(1)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalDark)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Hero Photo Section with Back and Actions
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = recipe.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        CharcoalDark.copy(alpha = 0.6f),
                                        Color.Transparent,
                                        CharcoalDark
                                    )
                                )
                            )
                    )

                    // Top Action Icons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, start = 8.dp, end = 12.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.navigateBack() },
                            modifier = Modifier.testTag("recipe_detail_back")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(CharcoalDark.copy(alpha = 0.65f))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = CreamWhite
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = { viewModel.toggleSaveRecipe(recipe) },
                                modifier = Modifier.testTag("recipe_favorite_button")
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(CharcoalDark.copy(alpha = 0.65f))
                                ) {
                                    Icon(
                                        imageVector = if (isSaved) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (isSaved) FlameOrange else CreamWhite
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(
                                            Intent.EXTRA_SUBJECT,
                                            "Recipe: ${recipe.title}"
                                        )
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "Check out this AI-invented recipe: ${recipe.title}!\n\n${recipe.description}\n\nCooked with Food Alchemist."
                                        )
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Recipe"))
                                },
                                modifier = Modifier.testTag("recipe_share_button")
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(CharcoalDark.copy(alpha = 0.65f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = CreamWhite
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Recipe Header & Description
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = CreamWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = recipe.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            // Stat Badges
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    StatPill(
                        icon = Icons.Default.Timer,
                        text = "${recipe.cookTimeMinutes} min",
                        color = AmberGold
                    )
                    StatPill(
                        icon = Icons.Default.OutdoorGrill,
                        text = recipe.difficulty,
                        color = BasilGreen
                    )
                    StatPill(
                        icon = Icons.Default.Restaurant,
                        text = "$currentServings servings",
                        color = AmberGoldLight
                    )
                }
            }

            // Action Row: Save | Share | Cook Mode
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    ActionPillButton(
                        icon = if (isSaved) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        label = if (isSaved) "Saved" else "Save",
                        modifier = Modifier.weight(1f),
                        isActive = isSaved,
                        onClick = { viewModel.toggleSaveRecipe(recipe) }
                    )
                    ActionPillButton(
                        icon = Icons.Default.Share,
                        label = "Share",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "Cook ${recipe.title} with Food Alchemist!")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share"))
                        }
                    )
                    ActionPillButton(
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        label = "Cook Mode",
                        modifier = Modifier.weight(1.2f),
                        isPrimary = true,
                        onClick = { viewModel.navigateTo(Screen.CookingMode(recipe, 0)) }
                    )
                }
            }

            // Ingredients Header & Scale Selector
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Ingredients",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )

                    // Serving Scaler [- 2 +]
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(CharcoalElevated)
                            .border(1.dp, CharcoalBorder, RoundedCornerShape(20.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Scale",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        IconButton(
                            onClick = { viewModel.adjustServings(-1) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease servings",
                                tint = AmberGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "$currentServings",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                        IconButton(
                            onClick = { viewModel.adjustServings(1) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase servings",
                                tint = AmberGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Ingredient Items List
            items(recipe.ingredients) { ing ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CharcoalElevated)
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = ing.emoji, fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = ing.name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Text(
                        text = if (scaleFactor == 1f) ing.amount else "${ing.amount} (scaled)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AmberGoldLight,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // Smart Substitutions Card
            if (recipe.smartSubstitutions.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(CharcoalElevated)
                            .border(1.dp, BasilGreen.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🍃", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Smart Substitutions",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = BasilGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            recipe.smartSubstitutions.forEach { sub ->
                                Text(
                                    text = "• $sub",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        lineHeight = 18.sp
                                    ),
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Chef's Pro Tips
            if (recipe.chefTips.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(CharcoalCard)
                            .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Tip",
                                    tint = AmberGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Alchemy Chef's Secrets",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = AmberGoldLight,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            recipe.chefTips.forEach { tip ->
                                Text(
                                    text = "• $tip",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        lineHeight = 18.sp
                                    ),
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Nutrition Facts Summary
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Nutrition per Serving",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        NutrientBox("Calories", "${recipe.calories} kcal", Modifier.weight(1f))
                        NutrientBox("Protein", "${recipe.proteinGrams}g", Modifier.weight(1f))
                        NutrientBox("Carbs", "${recipe.carbsGrams}g", Modifier.weight(1f))
                        NutrientBox("Fat", "${recipe.fatGrams}g", Modifier.weight(1f))
                    }
                }
            }
        }

        // Bottom Sticky Action Button: Cook It Now
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            CharcoalDark.copy(alpha = 0.95f),
                            CharcoalDark
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            GlowingGradientButton(
                text = "👨‍🍳 Cook It Now",
                onClick = { viewModel.navigateTo(Screen.CookingMode(recipe, 0)) },
                testTag = "btn_cook_it_now"
            )
        }
    }
}

@Composable
private fun StatPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(CharcoalElevated)
            .border(1.dp, CharcoalBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextPrimary,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun ActionPillButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isPrimary) AmberGoldDark else CharcoalElevated)
            .border(
                width = 1.dp,
                color = if (isActive || isPrimary) AmberGold else CharcoalBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isPrimary) CharcoalDark else if (isActive) FlameOrange else TextPrimary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (isPrimary) CharcoalDark else TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun NutrientBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CharcoalCard)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextMuted,
                fontSize = 10.sp
            )
        )
    }
}
