package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.Screen

@Composable
fun FoodAlchemistBottomNav(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(CharcoalDark)
            .border(
                width = 1.dp,
                color = CharcoalBorder.copy(alpha = 0.5f),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .padding(horizontal = 8.dp)
    ) {
        BottomNavItem(
            label = "Home",
            selected = currentScreen is Screen.Home,
            icon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            tag = "nav_home",
            onClick = { onNavigate(Screen.Home) }
        )
        BottomNavItem(
            label = "Create",
            selected = currentScreen is Screen.FusionLab,
            icon = Icons.Filled.Science,
            unselectedIcon = Icons.Outlined.Science,
            tag = "nav_create",
            onClick = { onNavigate(Screen.FusionLab) }
        )
        BottomNavItem(
            label = "Kitchen",
            selected = currentScreen is Screen.Kitchen,
            icon = Icons.Filled.Kitchen,
            unselectedIcon = Icons.Outlined.Kitchen,
            tag = "nav_kitchen",
            onClick = { onNavigate(Screen.Kitchen) }
        )
        BottomNavItem(
            label = "Saved",
            selected = currentScreen is Screen.Saved,
            icon = Icons.Filled.Bookmark,
            unselectedIcon = Icons.Outlined.BookmarkBorder,
            tag = "nav_saved",
            onClick = { onNavigate(Screen.Saved) }
        )
        BottomNavItem(
            label = "Profile",
            selected = currentScreen is Screen.Profile,
            icon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            tag = "nav_profile",
            onClick = { onNavigate(Screen.Profile) }
        )
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    selected: Boolean,
    icon: ImageVector,
    unselectedIcon: ImageVector,
    tag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(tag)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = if (selected) icon else unselectedIcon,
            contentDescription = label,
            tint = if (selected) AmberGold else TextMuted,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                color = if (selected) AmberGold else TextMuted
            ),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
