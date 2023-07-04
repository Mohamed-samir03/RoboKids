package com.graduationproject.robokidsapp.ui.kidsFragments.gaming

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduationproject.robokidsapp.modelGaming.Board
import com.graduationproject.robokidsapp.modelGaming.BoardState
import com.graduationproject.robokidsapp.modelGaming.Cell
import com.graduationproject.robokidsapp.modelGaming.CellState

import kotlinx.coroutines.*

class MainActivityViewModel : ViewModel() {
    val mainBoard = Board()
    val board = MutableLiveData(mainBoard)

    private fun updateBoard() {
        board.value = mainBoard
    }

    fun boardClicked(cell: Cell) {
        if (mainBoard.setCell(cell, CellState.Star)) {
            updateBoard()
            if (mainBoard.boardState == BoardState.INCOMPLETE) {
                GlobalScope.launch {
                    delay(500)
                    withContext(Dispatchers.Main){
                        aiTurn()
                    }
                }
            }
        }
    }

    private fun aiTurn() {
        val circleWinningCell = mainBoard.findNextWinningMove(CellState.Circle)
        val startWinningCell = mainBoard.findNextWinningMove(CellState.Star)
        when {
            // If the AI can win, place a circle in that spot
            circleWinningCell != null -> mainBoard.setCell(circleWinningCell, CellState.Circle)
            // If the AI is about to lose, place a circle in a blocking spot
            startWinningCell != null -> mainBoard.setCell(startWinningCell, CellState.Circle)
            // Prioritize the middle
            mainBoard.setCell(Cell.CENTER_CENTER, CellState.Circle) -> Unit
            // Otherwise place a circle in a random spot
            else -> do {
                val nextCell = Cell.values().random()
                val placeSuccess = mainBoard.setCell(nextCell, CellState.Circle)
            } while (!placeSuccess)
        }

        updateBoard()
    }

    fun resetBoard() {
        mainBoard.clearBoard()
        updateBoard()
    }
}