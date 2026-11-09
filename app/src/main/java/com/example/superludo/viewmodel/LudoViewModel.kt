package com.example.superludo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.superludo.model.PlayerColor
import com.example.superludo.model.Piece
import com.example.superludo.model.PieceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class LudoViewModel : ViewModel() {

    private val _players = listOf(PlayerColor.GREEN, PlayerColor.YELLOW, PlayerColor.BLUE, PlayerColor.RED)

    var currentTurnIndex by mutableStateOf(0)
        private set

    val currentPlayer: PlayerColor
        get() = _players[currentTurnIndex]

    var diceValue by mutableStateOf(1)
        private set

    var isDiceRolled by mutableStateOf(false)
        private set

    var isRolling by mutableStateOf(false)
        private set

    var winner by mutableStateOf<PlayerColor?>(null)
        private set

    var gameLog by mutableStateOf("Game started! Green's turn to roll.")
        private set

    // Pieces for all players
    private val _pieces = mutableStateOf(
        _players.flatMap { player ->
            List(4) { index ->
                Piece(id = "${player.name}_$index", playerColor = player, pieceIndex = index)
            }
        }
    )
    val pieces: List<Piece>
        get() = _pieces.value

    val safeTrackIndices = listOf(0, 8, 13, 21, 26, 34, 39, 47) // Example safe spots on 52-track board

    fun rollDice() {
        if (isDiceRolled || winner != null || isRolling) return

        isRolling = true
        // Simulate roll
        val rolled = Random.nextInt(1, 7)
        diceValue = rolled
        isRolling = false
        isDiceRolled = true
        gameLog = "${currentPlayer.nameString} rolled a $rolled."

        // Check if player has any valid moves
        if (!hasValidMoves(currentPlayer, rolled)) {
            gameLog = "${currentPlayer.nameString} rolled $rolled but has no valid moves."
            // Auto advance turn after a short delay or allow click to pass
        }
    }

    private fun hasValidMoves(player: PlayerColor, roll: Int): Boolean {
        val playerPieces = pieces.filter { it.playerColor == player }
        return playerPieces.any { piece ->
            when (piece.positionState) {
                PieceState.YARD -> roll == 6
                PieceState.TRACK, PieceState.HOME_STRETCH -> {
                    val nextSteps = piece.stepsTaken + roll
                    nextSteps <= 56
                }
                PieceState.HOME -> false
            }
        }
    }

    fun onPieceClick(piece: Piece) {
        if (!isDiceRolled || winner != null || piece.playerColor != currentPlayer) return

        val roll = diceValue
        val updatedPieces = _pieces.value.toMutableList()
        val index = updatedPieces.indexOfFirst { it.id == piece.id }
        if (index == -1) return

        val clickedPiece = updatedPieces[index]

        when (clickedPiece.positionState) {
            PieceState.YARD -> {
                if (roll == 6) {
                    // Bring out to start
                    updatedPieces[index] = clickedPiece.copy(
                        positionState = PieceState.TRACK,
                        trackPosition = clickedPiece.playerColor.startTrackIndex,
                        stepsTaken = 0
                    )
                    gameLog = "${currentPlayer.nameString} brought a piece out!"
                    finishTurn(extraTurn = true)
                } else {
                    gameLog = "Need a 6 to bring a piece out of yard."
                    return
                }
            }
            PieceState.TRACK, PieceState.HOME_STRETCH -> {
                val newSteps = clickedPiece.stepsTaken + roll
                if (newSteps > 56) {
                    gameLog = "Exact roll needed to enter home! (Steps: $newSteps/56)"
                    return
                }

                if (newSteps == 56) {
                    updatedPieces[index] = clickedPiece.copy(
                        positionState = PieceState.HOME,
                        trackPosition = 6,
                        stepsTaken = 56
                    )
                    gameLog = "${currentPlayer.nameString} reached HOME!"
                } else if (newSteps > 50) {
                    // Home stretch (0 to 5 in home stretch)
                    val stretchPos = newSteps - 51
                    updatedPieces[index] = clickedPiece.copy(
                        positionState = PieceState.HOME_STRETCH,
                        trackPosition = stretchPos,
                        stepsTaken = newSteps
                    )
                    gameLog = "${currentPlayer.nameString} moved into home stretch."
                } else {
                    // Main track (0 to 51)
                    val newTrackPos = (clickedPiece.playerColor.startTrackIndex + newSteps) % 52

                    // Check captures on main track if not safe
                    if (!safeTrackIndices.contains(newTrackPos)) {
                        // Check if opponent piece is there
                        val opponentPieceIndex = updatedPieces.indexOfFirst {
                            it.playerColor != currentPlayer &&
                            it.positionState == PieceState.TRACK &&
                            it.trackPosition == newTrackPos
                        }
                        if (opponentPieceIndex != -1) {
                            val opponent = updatedPieces[opponentPieceIndex]
                            updatedPieces[opponentPieceIndex] = opponent.copy(
                                positionState = PieceState.YARD,
                                trackPosition = -1,
                                stepsTaken = -1
                            )
                            gameLog = "${currentPlayer.nameString} captured ${opponent.playerColor.nameString}'s piece!"
                        }
                    }

                    updatedPieces[index] = clickedPiece.copy(
                        positionState = PieceState.TRACK,
                        trackPosition = newTrackPos,
                        stepsTaken = newSteps
                    )
                    gameLog = "${currentPlayer.nameString} moved piece forward $roll steps."
                }

                // Check win condition for current player
                val playerPieces = updatedPieces.filter { it.playerColor == currentPlayer }
                if (playerPieces.all { it.positionState == PieceState.HOME }) {
                    winner = currentPlayer
                    gameLog = "🎉 ${currentPlayer.nameString} WON THE GAME! 🎉"
                } else {
                    val extraTurn = (roll == 6)
                    finishTurn(extraTurn = extraTurn)
                }
            }
            PieceState.HOME -> {
                gameLog = "Piece is already home."
                return
            }
        }

        _pieces.value = updatedPieces
    }

    private fun finishTurn(extraTurn: Boolean) {
        if (winner != null) return
        isDiceRolled = false
        if (!extraTurn) {
            currentTurnIndex = (currentTurnIndex + 1) % _players.size
            gameLog = "Turn passed to ${currentPlayer.nameString}. Roll the dice!"
        } else {
            gameLog = "Rolled a 6! ${currentPlayer.nameString} gets another turn!"
        }
    }

    fun resetGame() {
        _pieces.value = _players.flatMap { player ->
            List(4) { index ->
                Piece(id = "${player.name}_$index", playerColor = player, pieceIndex = index)
            }
        }
        currentTurnIndex = 0
        diceValue = 1
        isDiceRolled = false
        isRolling = false
        winner = null
        gameLog = "New game started! Green's turn."
    }
}
