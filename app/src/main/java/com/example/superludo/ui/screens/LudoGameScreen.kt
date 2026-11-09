package com.example.superludo.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superludo.model.PlayerColor
import com.example.superludo.ui.board.LudoBoardView
import com.example.superludo.ui.theme.SuperludoTheme
import com.example.superludo.viewmodel.LudoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LudoGameScreen(
    viewModel: LudoViewModel = remember { LudoViewModel() }
) {
    var showRulesDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.SportsEsports, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Super Ludo 3D/2D", fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = { showRulesDialog = true }) {
                        Icon(Icons.Rounded.Info, contentDescription = "Rules")
                    }
                    IconButton(onClick = { viewModel.resetGame() }) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Restart Game")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            val isLandscape = maxWidth > maxHeight

            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Board
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        LudoBoardView(
                            pieces = viewModel.pieces,
                            currentPlayer = viewModel.currentPlayer,
                            onPieceClick = { viewModel.onPieceClick(it) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Right: Control Panel & Status
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PlayerStatusCard(viewModel = viewModel)
                        DiceRollerSection(viewModel = viewModel)
                        ActionLogCard(log = viewModel.gameLog)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PlayerStatusCard(viewModel = viewModel)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        LudoBoardView(
                            pieces = viewModel.pieces,
                            currentPlayer = viewModel.currentPlayer,
                            onPieceClick = { viewModel.onPieceClick(it) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    DiceRollerSection(viewModel = viewModel)

                    Spacer(modifier = Modifier.height(8.dp))

                    ActionLogCard(log = viewModel.gameLog)
                }
            }
        }

        if (showRulesDialog) {
            AlertDialog(
                onDismissRequest = { showRulesDialog = false },
                title = { Text("Ludo Game Rules") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("1. Roll a 6 to bring a piece out of your yard onto the start track.")
                        Text("2. Click on your highlighted pieces to move them forward according to the dice roll.")
                        Text("3. Landing on an opponent's piece captures it and sends it back to their yard.")
                        Text("4. Safe zones protect your pieces from being captured.")
                        Text("5. Get all 4 pieces to the center home area to win the game!")
                    }
                },
                confirmButton = {
                    Button(onClick = { showRulesDialog = false }) {
                        Text("Got it")
                    }
                }
            )
        }

        if (viewModel.winner != null) {
            AlertDialog(
                onDismissRequest = { viewModel.resetGame() },
                title = { Text("Game Over!") },
                text = {
                    Text("🎉 ${viewModel.winner?.nameString} has won the game!")
                },
                confirmButton = {
                    Button(onClick = { viewModel.resetGame() }) {
                        Text("Play Again")
                    }
                }
            )
        }
    }
}

@Composable
fun PlayerStatusCard(viewModel: LudoViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = viewModel.currentPlayer.color.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(viewModel.currentPlayer.color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Current Turn: ${viewModel.currentPlayer.nameString}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            if (viewModel.isRolling) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            }
        }
    }
}

@Composable
fun DiceRollerSection(viewModel: LudoViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { viewModel.rollDice() },
            enabled = !viewModel.isDiceRolled && viewModel.winner == null && !viewModel.isRolling,
            colors = ButtonDefaults.buttonColors(containerColor = viewModel.currentPlayer.color),
            modifier = Modifier.height(56.dp)
        ) {
            Icon(Icons.Rounded.Casino, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (viewModel.isDiceRolled) "Rolled: ${viewModel.diceValue}" else "Roll Dice",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ActionLogCard(log: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(
            text = log,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_6")
@Composable
fun LudoGameScreenPreview() {
    SuperludoTheme {
        LudoGameScreen()
    }
}
