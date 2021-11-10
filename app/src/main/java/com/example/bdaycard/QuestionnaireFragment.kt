package com.example.bdaycard

import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class QuestionnaireFragment : Fragment(R.layout.fragment_questionnaire) {

    override fun onStart() {
        super.onStart()
        val datePicker = getDatePicker()
        initView(datePicker)
    }

    private fun initView(datePicker: MaterialDatePicker<Long>) {
        val fab = view?.findViewById<FloatingActionButton>(R.id.fab)
        val dateOfBirth = view?.findViewById<TextInputLayout>(R.id.dob)
        dateOfBirth?.editText?.setOnClickListener {
            datePicker.show(childFragmentManager, "Tag")
        }

        datePicker.addOnPositiveButtonClickListener {
            dateOfBirth?.editText?.setText(datePicker.headerText)
            fab?.visibility = View.VISIBLE
        }
        fab?.setOnClickListener {
            findNavController().navigate(R.id.action_questionnaireFragment_to_mainFragment)
        }
    }

    private fun getDatePicker(): MaterialDatePicker<Long> {
        val calendarEnd: Calendar = Calendar.getInstance()
        val year = 1999
        val endMonth = 10
        val endDate = 10
        calendarEnd.set(year, endMonth - 1, endDate)
        val maxDate = calendarEnd.timeInMillis
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setOpenAt(maxDate)
                .setValidator(DateValidatorPointBackward.before(maxDate))
        return MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setCalendarConstraints(constraintsBuilder.build())
            .build()
    }
}