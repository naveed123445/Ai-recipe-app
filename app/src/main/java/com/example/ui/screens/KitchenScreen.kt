package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlowingGradientButton
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberGoldDark
import com.example.ui.theme.AmberGoldLight
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalCard
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalElevated
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FoodAlchemistViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun KitchenScreen(
    viewModel: FoodAlchemistViewModel,
    modifier: Modifier = Modifier
) {
    val stock by viewModel.kitchenIngredients.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }

    var newName by remember { mutableStateOf("") }
    var newEmoji by remember { mutableStateOf("🥔") }
    var newAmount by remember { mutableStateOf("1") }
    var newCategory by remember { mutableStateOf("Produce") }

    val categories = listOf("All", "Produce", "Dairy & Eggs", "Meat & Protein", "Pantry", "Spices")

    val filteredStock = if (selectedCategory == "All") {
        stock
    } else {
        stock.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "Add to Kitchen",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Ingredient Name") },
                        placeholder = { Text("e.g. Avocado, Bell Pepper") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberGold,
                            unfocusedBorderColor = CharcoalBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_new_ingredient_name")
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = newEmoji,
                            onValueChange = { newEmoji = it },
                            label = { Text("Emoji") },
                            placeholder = { Text("🥑") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AmberGold,
                                unfocusedBorderColor = CharcoalBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_new_ingredient_emoji")
                        )

                        OutlinedTextField(
                            value = newAmount,
                            onValueChange = { newAmount = it },
                            label = { Text("Quantity") },
                            placeholder = { Text("2 pcs") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AmberGold,
                                unfocusedBorderColor = CharcoalBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_new_ingredient_amount")
                        )
                    }

                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories.filter { it != "All" }) { cat ->
                            val isSel = newCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSel) AmberGoldDark else CharcoalCard)
                                    .clickable { newCategory = cat }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSel) CreamWhite else TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            viewModel.addKitchenItem(newName, newEmoji, newCategory, newAmount)
                            newName = ""
                            newAmount = "1"
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                    modifier = Modifier.testTag("btn_confirm_add_ingredient")
                ) {
                    Text("Add to Pantry", color = CharcoalDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = TextSecondary)
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "🧺 Your Kitchen",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = CreamWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp
                        )
                    )
                    Text(
                        text = "${stock.size} ingredients currently in stock",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberGold,
                        contentColor = CharcoalDark
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("btn_open_add_dialog")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Add", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) AmberGoldDark else CharcoalElevated)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) AmberGold else CharcoalBorder,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isSelected) CreamWhite else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }

        // Action CTA: Invent Dish from Available Stock
        item {
            GlowingGradientButton(
                text = "✨ Invent Dish from Stock",
                onClick = { viewModel.navigateTo(Screen.FusionLab) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Invent",
                        tint = CharcoalDark,
                        modifier = Modifier.size(20.dp)
                    )
                },
                testTag = "btn_kitchen_invent_dish"
            )
        }

        // Inventory Items List
        items(filteredStock) { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CharcoalElevated)
                    .border(1.dp, CharcoalBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(CharcoalCard)
                    ) {
                        Text(text = item.emoji, fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = "${item.quantity} • ${item.category}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.removeKitchenItem(item.id) },
                    modifier = Modifier.testTag("delete_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove ${item.name}",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
