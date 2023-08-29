package com.guido.app

import kotlin.random.Random

object Colors{
    val colorList = listOf(
        R.color.color_sound_compose_selected_color1,
        R.color.color_sound_compose_selected_color2,
        R.color.color_sound_compose_selected_color3,
        R.color.color_sound_compose_selected_color4,
        R.color.color_sound_compose_selected_color5,
        R.color.color_sound_compose_selected_color6,
        R.color.color_sound_compose_selected_color7,
        R.color.color_sound_compose_selected_color8
    )

    fun getColorsBasedOnIndex(): Int {
        val randomIndex = Random.nextInt(0,colorList.size-1)
        return colorList[randomIndex]
    }

    val darkColorList = listOf(
        R.color.card_color1,
        R.color.card_color2,
        R.color.card_color3,
        R.color.card_color4,
        R.color.card_color5,
        R.color.card_color6,
        R.color.card_color7,
        R.color.card_color8
    )

    fun getDarkColorsBasedOnIndex(): Int {
        val randomIndex = Random.nextInt(0,darkColorList.size-1)
        return darkColorList[randomIndex]
    }
}
