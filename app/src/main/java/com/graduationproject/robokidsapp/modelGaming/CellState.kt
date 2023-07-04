package com.graduationproject.robokidsapp.modelGaming

import androidx.annotation.DrawableRes
import com.graduationproject.robokidsapp.R


sealed class CellState(@DrawableRes val res: Int) {
    object Blank : CellState(R.drawable.ic_blank)
    object Star : CellState(R.drawable.ic_x)
    object Circle : CellState(R.drawable.ic_o)
}
