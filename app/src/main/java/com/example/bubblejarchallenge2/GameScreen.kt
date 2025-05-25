package com.example.bubblejarchallenge2

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BubbleGameScreen() {
    // Stateful jars
    val jars = remember {
        mutableStateListOf<Jar>().apply {
            generateGame().forEach { add(it) }
        }
    }
    var selectedJarIndex by remember { mutableStateOf<Int?>(null) }
    var moves by remember { mutableStateOf(0) }
    var gameWon by remember { mutableStateOf(false) }

    // Orientation detection
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bubble Jar Challenge") },
                actions = {
                    TextButton(onClick = {
                        // Reset game
                        jars.clear()
                        generateGame().forEach { jars.add(it) }
                        selectedJarIndex = null
                        moves = 0
                        gameWon = false
                    }) {
                        Text("New Game")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (isLandscape) {
                Row(modifier = Modifier.fillMaxSize()) {
                    JarsPane(jars, selectedJarIndex, onJarClick = { idx ->
                        handleMove(
                            idx, jars, selectedJarIndex,
                            onMove = { moves++ },
                            onWin = { gameWon = true }
                        ).also { selectedJarIndex = null }
                    })
                    ControlPane(moves, gameWon) {
                        jars.clear()
                        generateGame().forEach { jars.add(it) }
                        selectedJarIndex = null
                        moves = 0
                        gameWon = false
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    MovesDisplay(moves)
                    JarsPane(jars, selectedJarIndex, onJarClick = { idx ->
                        handleMove(
                            idx, jars, selectedJarIndex,
                            onMove = { moves++ },
                            onWin = { gameWon = true }
                        ).also { selectedJarIndex = null }
                    })
                    ControlPane(moves, gameWon) {
                        jars.clear()
                        generateGame().forEach { jars.add(it) }
                        selectedJarIndex = null
                        moves = 0
                        gameWon = false
                    }
                }
            }
        }
    }
}

@Composable
private fun MovesDisplay(moves: Int) {
    Text(
        text = "Moves: $moves",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

private fun handleMove(
    index: Int,
    jars: MutableList<Jar>,
    selectedIndex: Int?,
    onMove: () -> Unit,
    onWin: () -> Unit
) {
    if (selectedIndex == null) return
    if (selectedIndex != index) {
        val moved = moveBubble(jars[selectedIndex], jars[index])
        if (moved) {
            onMove()
            if (checkWin(jars)) onWin()
        }
    }
}

@Composable
private fun JarsPane(
    jars: List<Jar>,
    selectedIndex: Int?,
    onJarClick: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(jars, key = { idx, _ -> idx }) { idx, jar ->
            key(idx) {
                JarView(
                    jar = jar,
                    isSelected = (selectedIndex == idx),
                    onClick = { onJarClick(idx) }
                )
            }
        }
    }
}

@Composable
private fun ControlPane(
    moves: Int,
    gameWon: Boolean,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (gameWon) {
            Text(
                "🎉 You Won!",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Button(onClick = onReset) {
            Text("Restart")
        }
    }
}

@Composable
fun JarView(
    jar: Jar,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(60.dp)
            .height(240.dp)
            .background(Color(0xFFEEEEEE))
            .clickable { onClick() }
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.DarkGray
            )
            .padding(4.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Empty slots
        repeat(4 - jar.size) {
            Spacer(
                Modifier
                    .size(40.dp)
                    .padding(2.dp)
            )
        }
        // Bubbles
        jar.forEach { bubble ->
            Box(
                Modifier
                    .size(40.dp)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(bubble)
                    .animateContentSize()
            )
        }
    }
}
