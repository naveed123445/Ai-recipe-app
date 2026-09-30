package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Recipe
import com.example.ui.components.IngredientAvatar
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
fun HomeScreen(
    viewModel: FoodAlchemistViewModel,
    modifier: Modifier = Modifier
) {
    val kitchenStock by viewModel.kitchenIngredients.collectAsState()
    val madeForYou by viewModel.madeForYouRecipes.collectAsState()
    val stockIdeas by viewModel.stockMealIdeas.collectAsState()
    val isGeneratingStockIdeas by viewModel.isGeneratingStockIdeas.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AmberGoldDark)
                    ) {
                        Text(text = "👨‍🍳", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 22.sp)) {
                                append("Food ")
                            }
                            withStyle(SpanStyle(color = AmberGold, fontWeight = FontWeight.Bold, fontSize = 22.sp)) {
                                append("Alchemist")
                            }
                        }
                    )
                }

                IconButton(
                    onClick = { /* Notification view */ },
                    modifier = Modifier.testTag("notification_button")
                ) {
                    Box {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AmberGold)
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            }
        }

        // Catchphrase Headline
        item {
            Column {
                Text(
                    text = "Turn what you have\ninto something amazing.",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 32.sp
                    )
                )
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = {
                    Text(
                        text = "Add or search ingredients...",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextSecondary
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmberGold,
                    unfocusedBorderColor = CharcoalBorder,
                    focusedContainerColor = CharcoalElevated,
                    unfocusedContainerColor = CharcoalElevated,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_ingredients_input")
            )
        }

        // Quick Actions Row (Scan Ingredients & Add Manually)
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Scan Ingredients Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    AmberGoldDark.copy(alpha = 0.4f),
                                    CharcoalElevated
                                )
                            )
                        )
                        .border(1.dp, AmberGold.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                        .clickable { viewModel.navigateTo(Screen.Scan) }
                        .testTag("action_scan_ingredients")
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(AmberGold)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Scan",
                                tint = CharcoalDark,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Scan",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Ingredients",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }

                // Add Manually Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(CharcoalElevated)
                        .border(1.dp, CharcoalBorder, RoundedCornerShape(18.dp))
                        .clickable { viewModel.navigateTo(Screen.Kitchen) }
                        .testTag("action_add_manually")
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(CharcoalCard)
                                .border(1.dp, CharcoalBorder, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = AmberGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Add",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Manually",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Hero Banner Card: Fusion Lab ✨
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, AmberGold.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
                    .clickable { viewModel.navigateTo(Screen.FusionLab) }
                    .testTag("hero_banner_fusion_lab")
            ) {
                // Background Image
                Image(
                    painter = painterResource(id = R.drawable.img_fusion_hero),
                    contentDescription = "Fusion Lab Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )

                // Dark Amber Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    CharcoalDark.copy(alpha = 0.92f),
                                    CharcoalDark.copy(alpha = 0.75f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Text & Arrow
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(18.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🧪 Fusion Lab",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = CreamWhite,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "✨", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Create a completely new dish\nfrom your ingredients using AI.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AmberGold)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Enter Fusion Lab",
                            tint = CharcoalDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Your Kitchen Section
        item {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🧺 Your Kitchen",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                    TextButton(
                        onClick = { viewModel.navigateTo(Screen.Kitchen) },
                        modifier = Modifier.testTag("see_all_kitchen")
                    ) {
                        Text(
                            text = "See All",
                            color = AmberGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(kitchenStock.take(6)) { item ->
                        IngredientAvatar(
                            emoji = item.emoji,
                            name = item.name,
                            onClick = { viewModel.navigateTo(Screen.Kitchen) }
                        )
                    }
                    // Plus Add Avatar
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { viewModel.navigateTo(Screen.Kitchen) }
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
                                    contentDescription = "Add Ingredient",
                                    tint = AmberGold,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Add",
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

        // Button to generate new meal ideas based on available stock (USER PROMPT SPECIFIC REQUIREMENT)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                CharcoalElevated,
                                CharcoalCard
                            )
                        )
                    )
                    .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .clickable { viewModel.generateMealIdeasFromStock() }
                    .testTag("btn_generate_meal_ideas_from_stock")
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AmberGold.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Generate ideas",
                                tint = AmberGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Generate Ideas from Kitchen Stock",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Match dishes with your ${kitchenStock.size} pantry items",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    if (isGeneratingStockIdeas) {
                        CircularProgressIndicator(
                            color = AmberGold,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Generate",
                            tint = AmberGoldLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Made for You Section
        item {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "✨ Made for you",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = "AI created recipes from your ingredients",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }
                    TextButton(
                        onClick = { viewModel.navigateTo(Screen.Saved) },
                        modifier = Modifier.testTag("see_all_made_for_you")
                    ) {
                        Text(
                            text = "See All",
                            color = AmberGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(madeForYou) { recipe ->
                        RecipeHomeCard(
                            recipe = recipe,
                            onCardClick = { viewModel.navigateTo(Screen.RecipeDetail(recipe)) },
                            onToggleFavorite = { viewModel.toggleSaveRecipe(recipe) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeHomeCard(
    recipe: Recipe,
    onCardClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageRes = if (recipe.imageDrawableName == "img_potato_fritters") {
        R.drawable.img_potato_fritters
    } else {
        R.drawable.img_spicy_skillet
    }

    Box(
        modifier = modifier
            .width(220.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(CharcoalElevated)
            .border(1.dp, CharcoalBorder, RoundedCornerShape(20.dp))
            .clickable { onCardClick() }
            .testTag("recipe_card_${recipe.id}")
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = recipe.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Match tag badge
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CharcoalDark.copy(alpha = 0.8f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = "${recipe.matchPercentage}% Stock Match",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BasilGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }

                // Favorite button
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CharcoalDark.copy(alpha = 0.65f))
                    ) {
                        Icon(
                            imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (recipe.isFavorite) FlameOrange else TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        lineHeight = 20.sp
                    ),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Time",
                            tint = AmberGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${recipe.cookTimeMinutes} min",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }
                    Text(text = "•", color = TextMuted)
                    Text(
                        text = recipe.difficulty,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}
