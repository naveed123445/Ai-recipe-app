package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlowingGradientButton
import com.example.ui.components.IngredientChipWithDismiss
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberGoldLight
import com.example.ui.theme.BasilGreen
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalElevated
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FoodAlchemistViewModel

@Composable
fun ScanIngredientsScreen(
    viewModel: FoodAlchemistViewModel,
    modifier: Modifier = Modifier
) {
    val scannedList by viewModel.scannedIngredients.collectAsState()
    var selectedHighlight by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    // Pulsing scanner reticle animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalDark)
    ) {
        // Main Viewfinder Photo
        Image(
            painter = painterResource(id = R.drawable.img_kitchen_scan),
            contentDescription = "Kitchen ingredients camera scan",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark gradient from top for clear header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            CharcoalDark.copy(alpha = 0.85f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Top Navigation
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 8.dp, end = 16.dp)
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier.testTag("scan_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = CreamWhite
                )
            }

            Text(
                text = "Scan Ingredients",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = CreamWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(CharcoalDark.copy(alpha = 0.6f))
            ) {
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = "Flash",
                    tint = AmberGold,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // --- Interactive Detected Bounding Boxes on Countertop ---
        // 1. Potato Bounding Box (Top Left)
        BoundingBoxOverlay(
            label = "Potato",
            confidence = "95%",
            xPercent = 0.15f,
            yPercent = 0.26f,
            widthDp = 100.dp,
            heightDp = 95.dp,
            isSelected = selectedHighlight == "Potato",
            alpha = alphaPulse,
            onClick = {
                selectedHighlight = if (selectedHighlight == "Potato") null else "Potato"
            }
        )

        // 2. Tomato Bounding Box (Center Right)
        BoundingBoxOverlay(
            label = "Tomato",
            confidence = "98%",
            xPercent = 0.52f,
            yPercent = 0.32f,
            widthDp = 110.dp,
            heightDp = 105.dp,
            isSelected = selectedHighlight == "Tomato",
            alpha = alphaPulse,
            onClick = {
                selectedHighlight = if (selectedHighlight == "Tomato") null else "Tomato"
            }
        )

        // 3. Red Onion Bounding Box (Center Left)
        BoundingBoxOverlay(
            label = "Onion",
            confidence = "93%",
            xPercent = 0.22f,
            yPercent = 0.44f,
            widthDp = 95.dp,
            heightDp = 95.dp,
            isSelected = selectedHighlight == "Onion",
            alpha = alphaPulse,
            onClick = {
                selectedHighlight = if (selectedHighlight == "Onion") null else "Onion"
            }
        )

        // 4. Egg Bounding Box (Bottom Center)
        BoundingBoxOverlay(
            label = "Egg",
            confidence = "96%",
            xPercent = 0.45f,
            yPercent = 0.52f,
            widthDp = 90.dp,
            heightDp = 90.dp,
            isSelected = selectedHighlight == "Egg",
            alpha = alphaPulse,
            onClick = {
                selectedHighlight = if (selectedHighlight == "Egg") null else "Egg"
            }
        )

        // --- Bottom Sheet Panel (matches user mockup) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(CharcoalDark)
                .border(
                    width = 1.dp,
                    color = CharcoalBorder.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                )
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header of panel
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Tap on ingredients to edit",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                // Add quick garlic or herb
                                viewModel.addScannedIngredient("Garlic", "🧄")
                            }
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add manually",
                            tint = AmberGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add More",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = AmberGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2x2 Grid of Detected Ingredient Chips
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp)
                ) {
                    items(scannedList) { ing ->
                        IngredientChipWithDismiss(
                            emoji = ing.emoji,
                            name = ing.name,
                            onDismiss = { viewModel.removeScannedIngredient(ing.name) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Primary CTA: "Use These Ingredients ->"
                GlowingGradientButton(
                    text = "Use These Ingredients",
                    onClick = { viewModel.useScannedIngredients() },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Proceed",
                            tint = CharcoalDark,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    testTag = "btn_use_these_ingredients"
                )
            }
        }
    }
}

@Composable
private fun BoundingBoxOverlay(
    label: String,
    confidence: String,
    xPercent: Float,
    yPercent: Float,
    widthDp: androidx.compose.ui.unit.Dp,
    heightDp: androidx.compose.ui.unit.Dp,
    isSelected: Boolean,
    alpha: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = (xPercent * 340).dp,
                top = (yPercent * 680).dp
            )
    ) {
        Box(
            modifier = Modifier
                .size(widthDp, heightDp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = if (isSelected) 2.5.dp else 1.5.dp,
                    color = if (isSelected) AmberGold else CreamWhite.copy(alpha = alpha),
                    shape = RoundedCornerShape(12.dp)
                )
                .background(
                    if (isSelected) AmberGold.copy(alpha = 0.2f) else Color.Transparent
                )
                .clickable { onClick() }
        ) {
            // Label tag on top of box
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 6.dp, y = (-12).dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) AmberGold else CharcoalDark.copy(alpha = 0.85f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "$label $confidence",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isSelected) CharcoalDark else CreamWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}
