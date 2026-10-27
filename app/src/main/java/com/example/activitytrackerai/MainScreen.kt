package com.example.activitytrackerai

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(
    onSignOut: () -> Unit = {}
) {
    val tabs = listOf("Chat", "Στατιστικά", "Στόχοι", "Χάρτης", "Προφίλ")
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 0.dp // <-- αφαιρεί το κενό στις άκρες
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            title,
                            maxLines = 1,
                            softWrap = false,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> ChatScreen()
            1 -> StatsScreen()
            2 -> GoalsScreen()
            3 -> MapScreen()
            4 -> ProfileScreen(onSignOut = onSignOut)
        }
    }
}
