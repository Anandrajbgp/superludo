package com.example.superludo.model

import androidx.compose.ui.graphics.Color

enum class PlayerColor(val color: Color, val nameString: String, val startTrackIndex: Int) {
    RED(Color(0xFFE53935), "Red", 0),
    GREEN(Color(0xFF43A047), "Green", 13),
    YELLOW(Color(0xFFFFB300), "Yellow", 26),
    BLUE(Color(0xFF1E88E5), "Blue", 39)
}

data class Piece(
    val id: String, // e.g. "GREEN_0"
    val playerColor: PlayerColor,
    val pieceIndex: Int,
    val positionState: PieceState = PieceState.YARD, // YARD, TRACK, HOME_STRETCH, HOME
    val trackPosition: Int = -1, // -1 if in yard, 0 to 51 for main track, 0 to 5 for home stretch, 6 for home
    val stepsTaken: Int = -1 // -1 in yard, 0 to 56 total steps to reach home
)

enum class PieceState {
    YARD,
    TRACK,
    HOME_STRETCH,
    HOME
}

data class BoardPosition(val row: Int, val col: Int)

data class TrackCell(
    val index: Int,
    val row: Int,
    val col: Int,
    val isSafe: Boolean = false,
    val isStartingPointFor: PlayerColor? = null
)
