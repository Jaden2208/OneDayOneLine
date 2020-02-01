package com.whalez.onedayoneline.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.aminography.primecalendar.PrimeCalendar
import com.aminography.primecalendar.common.CalendarFactory
import com.aminography.primecalendar.common.CalendarType
import com.aminography.primedatepicker.PickType
import com.aminography.primedatepicker.fragment.PrimeDatePickerBottomSheet
import com.whalez.onedayoneline.R
import kotlinx.android.synthetic.main.activity_post.*

class PostActivity : AppCompatActivity(), PrimeDatePickerBottomSheet.OnDayPickedListener {

    private val TAG = "kkk_PostActivity"

    private lateinit var pickedDay: PrimeCalendar

    companion object{
        const val PICKER_TAG = "PrimeDatePickerBottomSheet"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        // back 버튼 클릭
        btn_back.setOnClickListener { finish() }

        // 날짜 변경 버튼 클릭
        var datePicker: PrimeDatePickerBottomSheet
        val today = CalendarFactory.newInstance(CalendarType.CIVIL)
        pickedDay = today
        btn_change_date.setOnClickListener {
            val pickType = PickType.SINGLE
            datePicker = PrimeDatePickerBottomSheet.newInstance(
                currentDateCalendar = today,
                pickType = pickType,
                pickedSingleDayCalendar = pickedDay // can be null
//                pickedRangeStartCalendar, // can be null
//                pickedRangeEndCalendar, // can be null
//                pickedMultipleDaysList, // can be null
//                minDateCalendar, // can be null
//                maxDateCalendar, // can be null
//                typefacePath // can be null
            )
            datePicker.setOnDateSetListener(this)
            datePicker.show(supportFragmentManager, PICKER_TAG)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onSingleDayPicked(singleDay: PrimeCalendar) {
        val year = singleDay.year
        val month = singleDay.month + 1
        val day = singleDay.dayOfMonth
        val weekday = singleDay.weekDayNameShort
        pickedDay = singleDay
        tv_date.text = "${year}년 ${month}월 ${day}일 ${weekday}요일"
    }

    override fun onMultipleDaysPicked(multipleDays: List<PrimeCalendar>) {}

    override fun onRangeDaysPicked(startDay: PrimeCalendar, endDay: PrimeCalendar) {}

}
