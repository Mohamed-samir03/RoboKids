package com.graduationproject.robokidsapp.ui.kidsFragments.gaming

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.graduationproject.robokidsapp.R
import com.graduationproject.robokidsapp.databinding.FragmentMemoryGameBinding
import com.graduationproject.robokidsapp.modelGaming.MemoryCard
import com.graduationproject.robokidsapp.ui.kidsFragments.ContentEnterSplashFragment.Companion.arduinoBluetooth
import kotlinx.coroutines.*

class MemoryGameFragment : Fragment() {

    private var _binding: FragmentMemoryGameBinding? = null
    private val binding get() = _binding!!
    private lateinit var mNavController: NavController
    private lateinit var buttons: List<ImageButton>
    private lateinit var cards: List<MemoryCard>
    private var indexOfSingleSelectedCard: Int? = null
    private var count = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNavController = findNavController()
    }

    override fun onResume() {
        super.onResume()
        arduinoBluetooth.sendMessage("memoryGame-") // send result to arduino bluetooth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMemoryGameBinding.inflate(inflater, container, false)

        val images = mutableListOf(R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6)

        // Add each image twice so we can create pairs
        images.addAll(images)
        // Randomize the order of images
        images.shuffle()

        buttons = listOf(binding.imageButton1,binding.imageButton2,binding.imageButton3,binding.imageButton4,binding.imageButton5,binding.imageButton6,binding.imageButton7,binding.imageButton8,binding.imageButton9,binding.imageButton10,binding.imageButton11,binding.imageButton12)

        cards = buttons.indices.map { index ->
            MemoryCard(images[index])
        }

        buttons.forEachIndexed { index, Button ->
            Button.setOnClickListener {
                // Update models
                val animation = AnimationUtils.loadAnimation(activity,R.anim.fade_in)
                Button.startAnimation(animation)
                updateModels(index)
                // Update the UI for the game
                updateViews()
            }
        }

        binding.exitMemoryGame.setOnClickListener {
            mNavController.currentBackStackEntry?.let { backEntry -> mNavController.popBackStack(backEntry.destination.id,true) }
        }

        binding.restartGame.setOnClickListener {
            val action = MemoryGameFragmentDirections.actionMemoryGameFragmentSelf()
            mNavController.navigate(action)
        }

        return binding.root
    }

    private fun updateViews() {
        cards.forEachIndexed { index, card ->
            val button = buttons[index]
            if (card.isMatched) {
                button.alpha = 0.8f
                button.setBackgroundResource(R.drawable.bg_memory_game_true)
                if (count == 6){
                    arduinoBluetooth.sendMessage("mgWin-") // send result to arduino bluetooth

                    GlobalScope.launch {
                        withContext(Dispatchers.Main){
                            buttons.forEachIndexed { index, Button ->
                                Button.isEnabled = false
                            }
                            binding.animationView.visibility = View.VISIBLE
                            binding.animationView2.visibility = View.VISIBLE
                            delay(1500)
                            binding.animationView.visibility = View.GONE
                            binding.animationView2.visibility = View.GONE
                        }
                    }
                }
            }else{
                button.setBackgroundResource(R.drawable.bg_memory_game_false)
            }
            button.setImageResource(if (card.isFaceUp){
                card.identifier
            } else R.drawable.ask)
        }
    }

    private fun updateModels(position: Int) {
        val card = cards[position]
        // Error checking:
        if (card.isFaceUp) {
            Toast.makeText(requireContext(), "Invalid move!", Toast.LENGTH_SHORT).show()
            return
        }
        // Three cases
        // 0 cards previously flipped over => restore cards + flip over the selected card
        // 1 card previously flipped over => flip over the selected card + check if the images match
        // 2 cards previously flipped over => restore cards + flip over the selected card
        if (indexOfSingleSelectedCard == null) {
            // 0 or 2 selected cards previously
            restoreCards()
            indexOfSingleSelectedCard = position
        } else {
            // exactly 1 card was selected previously
            checkForMatch(indexOfSingleSelectedCard!!, position)
            indexOfSingleSelectedCard = null
        }
        card.isFaceUp = !card.isFaceUp
    }

    private fun restoreCards() {
        for (card in cards) {
            if (!card.isMatched) {
                card.isFaceUp = false
            }
        }
    }

    private fun checkForMatch(position1: Int, position2: Int) {
        if (cards[position1].identifier == cards[position2].identifier) {
            count += 1
            cards[position1].isMatched = true
            cards[position2].isMatched = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}