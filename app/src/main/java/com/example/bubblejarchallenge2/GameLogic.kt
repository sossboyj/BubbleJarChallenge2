package com.example.bubblejarchallenge2

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color

typealias Bubble = Color
typealias Jar = MutableList<Bubble>

private val bubbleColors = listOf(
    Color.Red,
    Color.Blue,
    Color.Green,
    Color.Yellow,
    Color.Magenta,
    Color.Cyan
)
private const val BUBBLES_PER_COLOR = 4
private const val JAR_COUNT = 8
private const val JAR_CAPACITY = 4

/**Generate 6 full jars (4 bubbles each) + 2 empty jars. **/
fun generateGame(): List<Jar> {
    val all = bubbleColors
        .flatMap { color -> List(BUBBLES_PER_COLOR) { color } }
        .shuffled()

    val jars: List<Jar> = List(JAR_COUNT) { mutableStateListOf() }
    for (i in 0 until 6) {
        val start = i * BUBBLES_PER_COLOR
        jars[i].addAll(all.subList(start, start + BUBBLES_PER_COLOR))
    }
    return jars
}

/**Can move if source not empty and target has room (<4).**/
fun canMove(from: Jar, to: Jar): Boolean {
    if (from.isEmpty()) return false
    if (to.size >= JAR_CAPACITY) return false
    return true
}

/** Remove the **top bubble**/
fun moveBubble(from: Jar, to: Jar): Boolean {
    return if (canMove(from, to)) {
        val bubble = from.removeAt(0)
        to.add(0, bubble)
        true
    } else false
}

/**True if every jar is empty or holds 4 of the same color. **/
fun checkWin(jars: List<Jar>): Boolean =
    jars.all { it.isEmpty() || (it.size == JAR_CAPACITY && it.distinct().size == 1) }
