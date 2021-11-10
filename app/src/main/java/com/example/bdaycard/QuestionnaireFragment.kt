package com.example.bdaycard

import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout

class QuestionnaireFragment : Fragment(R.layout.fragment_questionnaire) {

    override fun onStart() {
        super.onStart()
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .build()
        val fab = view?.findViewById<FloatingActionButton>(R.id.fab)
        val dateOfBirth = view?.findViewById<TextInputLayout>(R.id.dob)
        dateOfBirth?.editText?.setOnClickListener{
            datePicker.show(childFragmentManager, "Tag")
        }

      datePicker.addOnPositiveButtonClickListener { date ->
          dateOfBirth?.editText?.setText(date.toString())
          fab?.visibility = View.VISIBLE
      }
        fab?.setOnClickListener {
            findNavController().navigate(R.id.action_questionnaireFragment_to_mainFragment)
        }
    }
}