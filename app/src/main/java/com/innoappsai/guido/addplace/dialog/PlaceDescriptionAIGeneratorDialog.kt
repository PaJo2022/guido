package com.innoappsai.guido.addplace.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innoappsai.guido.R
import com.innoappsai.guido.adapters.PlaceDateTimeAdapter

class PlaceDescriptionAIGeneratorDialog(context: Context) : Dialog(context) {

    companion object{
        enum class DateTimeType{
            DAY,FROM,TO
        }
    }

    private lateinit var placeDateTimeAdapter: PlaceDateTimeAdapter
    private var data: ArrayList<String> = ArrayList()
    private var type : DateTimeType ?= null

    private var _onPlaceDateOrTimeSelected: ((String,DateTimeType?) -> Any?)? = null
    fun setOnPlaceDateOrTimeSelected(onPlaceDateOrTimeSelected: ((String,DateTimeType?) -> Any?)) {
        _onPlaceDateOrTimeSelected = onPlaceDateOrTimeSelected
    }

    fun setData(title: String,type : DateTimeType, data: ArrayList<String>) {
        findViewById<TextView>(R.id.dialog_title).text = title
        this.type = type
        this.data = data
        placeDateTimeAdapter.setPlaceDateOrTime(data)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.descriotion_generator_ai_dialog_layout)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        placeDateTimeAdapter = PlaceDateTimeAdapter(context)

        findViewById<RecyclerView>(R.id.rv_custom_alert_dialog).apply {
            adapter = placeDateTimeAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        placeDateTimeAdapter.setOnPlaceDateOrTimeSelected {
            _onPlaceDateOrTimeSelected?.invoke(it,type)
            dismiss()
        }
    }


}
