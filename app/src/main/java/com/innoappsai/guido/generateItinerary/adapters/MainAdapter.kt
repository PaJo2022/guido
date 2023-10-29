package com.innoappsai.guido.generateItinerary.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.guido.cardstack.CardContainerAdapter
import com.innoappsai.guido.R
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel


class MainAdapter(private val context: Context) :
    CardContainerAdapter() {

    private var _list: List<PlaceUiModel> = ArrayList()

    fun setData(list: List<PlaceUiModel>) {
        _list = list
        notifyAppendData()
    }


    override fun getItem(position: Int) = _list[position]

    @SuppressLint("InflateParams")
    override fun getView(position: Int): View {
        val v = LayoutInflater.from(context).inflate(R.layout.card_view, null)
        val placeImage = v.findViewById<ImageView>(R.id.place_image)
        val placeName = v.findViewById<TextView>(R.id.place_name)
        val placeRating = v.findViewById<RatingBar>(R.id.place_rating)

        val place = getItem(position)

        Glide.with(context).load(place.photos?.firstOrNull()).into(placeImage)


        placeName.text = place.name
        placeRating.rating = place.rating?.toFloat() ?: 0f

        return v
    }

    override fun getCount(): Int = _list.size
}