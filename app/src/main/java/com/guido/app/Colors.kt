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
        R.color.color_sound_compose_selected_color8,
        R.color.color_sound_compose_selected_color9,
        R.color.color_sound_compose_selected_color10,
        R.color.color_sound_compose_selected_color11,
        R.color.color_sound_compose_selected_color12,
        R.color.color_sound_compose_selected_color13,
        R.color.color_sound_compose_selected_color14,
        R.color.color_sound_compose_selected_color15,
        R.color.color_sound_compose_selected_color16,
        R.color.color_sound_compose_selected_color17,
        R.color.color_sound_compose_selected_color18,
        R.color.color_sound_compose_selected_color19,
        R.color.color_sound_compose_selected_color20,
        R.color.color_sound_compose_selected_color21,
        R.color.color_sound_compose_selected_color22,
        R.color.color_sound_compose_selected_color23,
        R.color.color_sound_compose_selected_color24,
        R.color.color_sound_compose_selected_color25,
        R.color.color_sound_compose_selected_color26,
        R.color.color_sound_compose_selected_color27,
        R.color.color_sound_compose_selected_color28,
        R.color.color_sound_compose_selected_color29,
        R.color.color_sound_compose_selected_color30,
        R.color.color_sound_compose_selected_color31,
        R.color.color_sound_compose_selected_color32,
        R.color.color_sound_compose_selected_color33,
        R.color.color_sound_compose_selected_color34,
        R.color.color_sound_compose_selected_color35,
        R.color.color_sound_compose_selected_color36,
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