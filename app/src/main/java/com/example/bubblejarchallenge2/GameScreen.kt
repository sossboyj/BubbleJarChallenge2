package com.example.bubblejarchallenge2

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
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
    // — Game state —
    val jars = remember {
        mutableStateListOf<Jar>().apply {
            generateGame().forEach { add(it) }
        }
    }
    var selected by remember { mutableStateOf<Int?>(null) }
    var moves by remember { mutableIntStateOf(0) }
    var won by remember { mutableStateOf(false) }

    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Unified click handler:
    fun onJarClick(idx: Int) {
        val sel = selected
        if (sel == null) {
            // only select if non-empty
            if (jars[idx].isNotEmpty()) selected = idx
        } else {
            // try move if different
            if (sel != idx) {
                if (moveBubble(jars[sel], jars[idx])) {
                    moves++
                    if (checkWin(jars)) won = true
                }
            }
            selected = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bubble Jar Challenge") },
                actions = {
                    TextButton(
                        onClick = {
                            // Reset everything
                            jars.clear()
                            generateGame().forEach { jars.add(it) }
                            selected = null
                            moves = 0
                            won = false
                        }
                    ) {
                        Text("New Game")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (isLandscape) {
                Row(Modifier.fillMaxSize()) {
                    // Left side: instructions + jars
                    Column(
                        Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(8.dp))
                        MovesDisplay(moves)
                        Instructions()
                        JarsPane(jars, selected, ::onJarClick)
                    }
                    // Right side: controls
                    ControlPane(moves, won) {
                        jars.clear()
                        generateGame().forEach { jars.add(it) }
                        selected = null
                        moves = 0
                        won = false
                    }
                }
            } else {
                // Portrait layout
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(12.dp))
                    MovesDisplay(moves)
                    Instructions()
                    JarsPane(jars, selected, ::onJarClick)
                    ControlPane(moves, won) {
                        jars.clear()
                        generateGame().forEach { jars.add(it) }
                        selected = null
                        moves = 0
                        won = false
                    }
                }
            }
        }
    }
}

@Composable
private fun Instructions() {
    Text(
        text = "Tap a jar to pick its top bubble.\n" +
                "Tap another jar to move it if valid.\n" +
                "Max 4 per jar.",
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    )
}

@Composable
private fun MovesDisplay(moves: Int) {
    Text(
        text = "Moves: $moves",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun JarsPane(
    jars: List<Jar>,
    selected: Int?,
    onJarClick: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(jars, key = { idx, _ -> idx }) { idx, jar ->
            key(idx) {
                JarView(
                    jar = jar,
                    isSelected = (selected == idx),
                    onClick = { onJarClick(idx) }
                )
            }
        }
    }
}

@Composable
private fun ControlPane(
    moves: Int,
    won: Boolean,
    onReset: () -> Unit
) {
    Column(
        Modifier
            .padding(16.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MovesDisplay(moves)
        if (won) {
            Text(
                text = "🎉 You Won!",
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
        // empty slots
        repeat(4 - jar.size) {
            Spacer(Modifier.size(40.dp).padding(2.dp))
        }
        // bubbles
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
