package com.graduationproject.robokidsapp.ui.kidsFragments.gaming

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.databinding.FragmentTicTacToeBinding
import com.graduationproject.robokidsapp.modelGaming.Board
import com.graduationproject.robokidsapp.modelGaming.BoardState
import com.graduationproject.robokidsapp.modelGaming.Cell
import com.graduationproject.robokidsapp.ui.kidsFragments.ContentEnterSplashFragment.Companion.arduinoBluetooth


class TicTacToeFragment : Fragment() {
    private var _binding: FragmentTicTacToeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mNavController: NavController

    val vm: MainActivityViewModel by viewModels()
    private var flag = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
    }

    override fun onResume() {
        super.onResume()
        arduinoBluetooth.sendMessage("tttGame-") // send result to arduino bluetooth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
        _binding = FragmentTicTacToeBinding.inflate(inflater, container, false)


        vm.board.observe(requireActivity(), updateBoard)
        bindClickEvents()

        binding.ivExit.setOnClickListener {
            mNavController.currentBackStackEntry?.let { backEntry -> mNavController.popBackStack(backEntry.destination.id,true) }
        }

        return binding.root
    }



    val updateBoard = Observer { board: Board ->
        binding.square0.setImageResource(board.topLeft.res)
        binding.square1.setImageResource(board.topCenter.res)
        binding.square2.setImageResource(board.topRight.res)
        binding.square3.setImageResource(board.centerLeft.res)
        binding.square4.setImageResource(board.centerCenter.res)
        binding.square5.setImageResource(board.centerRight.res)
        binding.square6.setImageResource(board.bottomLeft.res)
        binding.square7.setImageResource(board.bottomCenter.res)
        binding.square8.setImageResource(board.bottomRight.res)
        when (board.boardState) {
            BoardState.STAR_WON -> {
                setupBoard(true)
                flag = true
                showWinningMessage(getString(R.string.you_won))

                arduinoBluetooth.sendMessage("tttWin-") // send result to arduino bluetooth
            }
            BoardState.CIRCLE_WON -> {
                setupBoard(true)
                flag = false
                showWinningMessage(getString(R.string.you_lost))

                arduinoBluetooth.sendMessage("tttLost-") // send result to arduino bluetooth
            }
            BoardState.DRAW -> {
                setupBoard(true)
                flag = false
                showWinningMessage(getString(R.string.it_draw))

                arduinoBluetooth.sendMessage("tttDraw-") // send result to arduino bluetooth
            }
            BoardState.INCOMPLETE -> {
                setupBoard()
                hideWinningMessage()
            }
        }
    }


    private fun setupBoard(disable: Boolean = false) {
        binding.square0.isEnabled = !disable
        binding.square1.isEnabled = !disable
        binding.square2.isEnabled = !disable
        binding.square3.isEnabled = !disable
        binding.square4.isEnabled = !disable
        binding.square5.isEnabled = !disable
        binding.square6.isEnabled = !disable
        binding.square7.isEnabled = !disable
        binding.square8.isEnabled = !disable

        binding.square0.alpha = if (disable) 0.5f else 1f
        binding.square1.alpha = if (disable) 0.5f else 1f
        binding.square2.alpha = if (disable) 0.5f else 1f
        binding.square3.alpha = if (disable) 0.5f else 1f
        binding.square4.alpha = if (disable) 0.5f else 1f
        binding.square5.alpha = if (disable) 0.5f else 1f
        binding.square6.alpha = if (disable) 0.5f else 1f
        binding.square7.alpha = if (disable) 0.5f else 1f
        binding.square8.alpha = if (disable) 0.5f else 1f
    }

    private fun bindClickEvents() {
        binding.square0.setOnClickListener { vm.boardClicked(Cell.TOP_LEFT) }
        binding.square1.setOnClickListener { vm.boardClicked(Cell.TOP_CENTER) }
        binding.square2.setOnClickListener { vm.boardClicked(Cell.TOP_RIGHT) }
        binding.square3.setOnClickListener { vm.boardClicked(Cell.CENTER_LEFT) }
        binding.square4.setOnClickListener { vm.boardClicked(Cell.CENTER_CENTER) }
        binding.square5.setOnClickListener { vm.boardClicked(Cell.CENTER_RIGHT) }
        binding.square6.setOnClickListener { vm.boardClicked(Cell.BOTTOM_LEFT) }
        binding.square7.setOnClickListener { vm.boardClicked(Cell.BOTTOM_CENTER) }
        binding.square8.setOnClickListener { vm.boardClicked(Cell.BOTTOM_RIGHT) }
        binding.buttonReset.setOnClickListener {
            arduinoBluetooth.sendMessage("tttGame-") // send result to arduino bluetooth

            vm.resetBoard()
            binding.animationView.visibility = View.GONE
        }
    }

    private fun showWinningMessage(message: String) {
        binding.textWinningMessage.visibility = View.VISIBLE
        binding.textWinningMessage.text = message
        if(flag)
            binding.animationView.visibility = View.VISIBLE
    }

    private fun hideWinningMessage() {
        binding.textWinningMessage.visibility = View.GONE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}