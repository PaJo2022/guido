package com.innoappsai.guido.generateItinerary.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import com.asynctaskcoffee.cardstack.CardContainerAdapter
import com.google.api.Context
import com.innoappsai.guido.R
import com.innoappsai.guido.model.placesUiModel.PlaceUiModel
//
//class MainAdapter(private val list: ArrayList<PlaceUiModel>, context: Context) :
//    CardContainerAdapter() {
//
//    var layoutInflater: LayoutInflater = LayoutInflater.from(context)
//
//    override fun getItem(position: Int) = list[position]
//
//    @SuppressLint("InflateParams")
//    override fun getView(position: Int): View {
//        val v = layoutInflater.inflate(R.layout.card_view, null)
//        val userImageView = v.findViewById<ImageView>(R.id.userImage)
//        val genderImageView = v.findViewById<ImageView>(R.id.genderImage)
//        val userName = v.findViewById<TextView>(R.id.userName)
//        val ageAndLastSeen = v.findViewById<TextView>(R.id.ageAndLastSeen)
//
//        val user = getItem(position)
//
//        Picasso.get().load(user.userImage).into(userImageView)
//        genderImageView.setImageResource(user.userGender)
//
//        userName.text = user.userName
//        ageAndLastSeen.text = user.userAgeLastSeen
//
//        return v
//    }
//
//    override fun getCount(): Int = list.size
//}