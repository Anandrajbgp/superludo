package com.example.superludo.ui.board

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.example.superludo.model.Piece
import com.example.superludo.model.PieceState
import com.example.superludo.model.PlayerColor

@Composable
fun LudoBoardView(
    pieces: List<Piece>,
    currentPlayer: PlayerColor,
    onPieceClick: (Piece) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .padding(8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFFFFDE7))
            .border(BorderStroke(6.dp, Color(0xFF2D3748)), RoundedCornerShape(20.dp))
            .shadow(8.dp, RoundedCornerShape(20.dp))
    ) {
        val boardWidth = maxWidth
        val boardHeight = maxHeight

        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellWidth = size.width / 15f
            val cellHeight = size.height / 15f

            // Red Yard (Top-Left)
            drawRect(
                color = PlayerColor.RED.color,
                topLeft = Offset(0f, 0f),
                size = androidx.compose.ui.geometry.Size(cellWidth * 6f, cellHeight * 6f)
            )
            // Green Yard (Top-Right)
            drawRect(
                color = PlayerColor.GREEN.color,
                topLeft = Offset(cellWidth * 9f, 0f),
                size = androidx.compose.ui.geometry.Size(cellWidth * 6f, cellHeight * 6f)
            )
            // Yellow Yard (Bottom-Right)
            drawRect(
                color = PlayerColor.YELLOW.color,
                topLeft = Offset(cellWidth * 9f, cellHeight * 9f),
                size = androidx.compose.ui.geometry.Size(cellWidth * 6f, cellHeight * 6f)
            )
            // Blue Yard (Bottom-Left)
            drawRect(
                color = PlayerColor.BLUE.color,
                topLeft = Offset(0f, cellHeight * 9f),
                size = androidx.compose.ui.geometry.Size(cellWidth * 6f, cellHeight * 6f)
            )

            // Inner white background for yards
            val yardInset = cellWidth * 0.75f
            val yardSize = cellWidth * 4.5f

            // Red Yard Inner
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(yardInset, yardInset),
                size = androidx.compose.ui.geometry.Size(yardSize, yardSize),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
            )
            // Green Yard Inner
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(cellWidth * 9f + yardInset, yardInset),
                size = androidx.compose.ui.geometry.Size(yardSize, yardSize),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
            )
            // Yellow Yard Inner
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(cellWidth * 9f + yardInset, cellHeight * 9f + yardInset),
                size = androidx.compose.ui.geometry.Size(yardSize, yardSize),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
            )
            // Blue Yard Inner
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(yardInset, cellHeight * 9f + yardInset),
                size = androidx.compose.ui.geometry.Size(yardSize, yardSize),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
            )

            // Grid lines for tracks
            for (i in 0..15) {
                drawLine(
                    color = Color(0xFFB0BEC5),
                    start = Offset(cellWidth * i, 0f),
                    end = Offset(cellWidth * i, size.height),
                    strokeWidth = 1f
                )
                drawLine(
                    color = Color(0xFFB0BEC5),
                    start = Offset(0f, cellHeight * i),
                    end = Offset(size.width, cellHeight * i),
                    strokeWidth = 1f
                )
            }

            // Center Home Triangles
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val leftCenter = cellWidth * 6f
            val rightCenter = cellWidth * 9f
            val topCenter = cellHeight * 6f
            val bottomCenter = cellHeight * 9f

            // Left Triangle (Red)
            drawPath(
                path = Path().apply {
                    moveTo(leftCenter, topCenter)
                    lineTo(centerX, centerY)
                    lineTo(leftCenter, bottomCenter)
                    close()
                },
                color = PlayerColor.RED.color
            )
            // Top Triangle (Green)
            drawPath(
                path = Path().apply {
                    moveTo(leftCenter, topCenter)
                    lineTo(centerX, centerY)
                    lineTo(rightCenter, topCenter)
                    close()
                },
                color = PlayerColor.GREEN.color
            )
            // Right Triangle (Yellow)
            drawPath(
                path = Path().apply {
                    moveTo(rightCenter, topCenter)
                    lineTo(centerX, centerY)
                    lineTo(rightCenter, bottomCenter)
                    close()
                },
                color = PlayerColor.YELLOW.color
            )
            // Bottom Triangle (Blue)
            drawPath(
                path = Path().apply {
                    moveTo(leftCenter, bottomCenter)
                    lineTo(centerX, centerY)
                    lineTo(rightCenter, bottomCenter)
                    close()
                },
                color = PlayerColor.BLUE.color
            )
        }

        // Overlay Grid for Home Stretches, Starting Cells, and Yard Circles
        Column(modifier = Modifier.fillMaxSize()) {
            for (r in 0 until 15) {
                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    for (c in 0 until 15) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(BorderStroke(0.5.dp, Color(0xFFCFD8DC))),
                            contentAlignment = Alignment.Center
                        ) {
                            val cellBg = getCellBackground(r, c)
                            if (cellBg != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(cellBg)
                                )
                            }

                            // Yard base circle spots (4 per yard)
                            val yardCircleColor = getYardCircleColor(r, c)
                            if (yardCircleColor != null) {
                                Box(
                                    modifier = Modifier
                                        .size(boardWidth / 15f * 0.65f)
                                        .clip(CircleShape)
                                        .background(yardCircleColor.copy(alpha = 0.25f))
                                        .border(2.dp, yardCircleColor, CircleShape)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Render Pieces as Location Pins
        pieces.forEach { piece ->
            val fraction = getPieceFraction(piece)
            if (fraction != null) {
                val posX = boardWidth * fraction.second
                val posY = boardHeight * fraction.first
                val isCurrentPlayerPiece = piece.playerColor == currentPlayer

                Box(
                    modifier = Modifier
                        .offset(posX - boardWidth / 30f, posY - boardHeight / 25f)
                        .size(boardWidth / 15f * 0.85f)
                        .clickable { onPieceClick(piece) },
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = piece.playerColor.color,
                        shadowElevation = if (isCurrentPlayerPiece) 6.dp else 3.dp,
                        border = BorderStroke(2.dp, if (isCurrentPlayerPiece) Color.White else Color.DarkGray),
                        modifier = Modifier.size(boardWidth / 15f * 0.75f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(boardWidth / 15f * 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getCellBackground(row: Int, col: Int): Color? {
    // Home stretches
    if (row == 7 && col in 1..5) return PlayerColor.RED.color.copy(alpha = 0.85f)
    if (col == 7 && row in 1..5) return PlayerColor.GREEN.color.copy(alpha = 0.85f)
    if (row == 7 && col in 9..13) return PlayerColor.YELLOW.color.copy(alpha = 0.85f)
    if (col == 7 && row in 9..13) return PlayerColor.BLUE.color.copy(alpha = 0.85f)

    // Starting cells
    if (row == 13 && col == 6) return PlayerColor.RED.color
    if (row == 6 && col == 1) return PlayerColor.GREEN.color
    if (row == 1 && col == 8) return PlayerColor.YELLOW.color
    if (row == 8 && col == 13) return PlayerColor.BLUE.color

    return null
}

fun getYardCircleColor(row: Int, col: Int): Color? {
    if (row in 1..4 && col in 1..4 && ((row == 1 || row == 4) && (col == 1 || col == 4))) {
        return PlayerColor.RED.color
    }
    if (row in 1..4 && col in 10..13 && ((row == 1 || row == 4) && (col == 10 || col == 13))) {
        return PlayerColor.GREEN.color
    }
    if (row in 10..13 && col in 10..13 && ((row == 10 || row == 13) && (col == 10 || col == 13))) {
        return PlayerColor.YELLOW.color
    }
    if (row in 10..13 && col in 1..4 && ((row == 10 || row == 13) && (col == 1 || col == 4))) {
        return PlayerColor.BLUE.color
    }
    return null
}

fun getPieceFraction(piece: Piece): Pair<Float, Float>? {
    val cellFraction = 1f / 15f
    val halfCell = cellFraction / 2f

    val (r, c) = when (piece.positionState) {
        PieceState.YARD -> getYardGrid(piece.playerColor, piece.pieceIndex)
        PieceState.HOME -> Pair(7f, 7f)
        PieceState.HOME_STRETCH -> getHomeStretchGrid(piece.playerColor, piece.trackPosition)
        PieceState.TRACK -> getTrackGrid(piece.playerColor, piece.trackPosition)
    }

    return Pair(r * cellFraction + halfCell, c * cellFraction + halfCell)
}

fun getYardGrid(player: PlayerColor, index: Int): Pair<Float, Float> {
    val baseRow = when (player) {
        PlayerColor.RED, PlayerColor.GREEN -> 1f
        PlayerColor.BLUE, PlayerColor.YELLOW -> 10f
    }
    val baseCol = when (player) {
        PlayerColor.RED, PlayerColor.BLUE -> 1f
        PlayerColor.GREEN, PlayerColor.YELLOW -> 10f
    }

    val offsets = listOf(
        Pair(0f, 0f),
        Pair(0f, 3f),
        Pair(3f, 0f),
        Pair(3f, 3f)
    )
    val off = offsets[index % 4]
    return Pair(baseRow + off.first, baseCol + off.second)
}

fun getHomeStretchGrid(player: PlayerColor, stretchPos: Int): Pair<Float, Float> {
    return when (player) {
        PlayerColor.RED -> Pair(7f, 1f + stretchPos.toFloat())
        PlayerColor.GREEN -> Pair(1f + stretchPos.toFloat(), 7f)
        PlayerColor.YELLOW -> Pair(7f, 13f - stretchPos.toFloat())
        PlayerColor.BLUE -> Pair(13f - stretchPos.toFloat(), 7f)
    }
}

val mainTrackGrid = listOf(
    Pair(13, 6), Pair(12, 6), Pair(11, 6), Pair(10, 6), Pair(9, 6),
    Pair(8, 5), Pair(8, 4), Pair(8, 3), Pair(8, 2), Pair(8, 1), Pair(8, 0),
    Pair(7, 0),
    Pair(6, 0), Pair(6, 1), Pair(6, 2), Pair(6, 3), Pair(6, 4), Pair(6, 5),
    Pair(5, 6), Pair(4, 6), Pair(3, 6), Pair(2, 6), Pair(1, 6), Pair(0, 6),
    Pair(0, 7),
    Pair(0, 8), Pair(1, 8), Pair(2, 8), Pair(3, 8), Pair(4, 8), Pair(5, 8),
    Pair(6, 9), Pair(6, 10), Pair(6, 11), Pair(6, 12), Pair(6, 13), Pair(6, 14),
    Pair(7, 14),
    Pair(8, 14), Pair(8, 13), Pair(8, 12), Pair(8, 11), Pair(8, 10), Pair(8, 9),
    Pair(9, 8), Pair(10, 8), Pair(11, 8), Pair(12, 8), Pair(13, 8), Pair(14, 8),
    Pair(14, 7),
    Pair(14, 6)
)

fun getTrackGrid(player: PlayerColor, trackPosition: Int): Pair<Float, Float> {
    val actualIndex = (player.startTrackIndex + trackPosition) % 52
    val gridCoord = mainTrackGrid.getOrElse(actualIndex) { Pair(7, 7) }
    return Pair(gridCoord.first.toFloat(), gridCoord.second.toFloat())
}
