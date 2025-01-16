package com.example.birdseyeview

import MapFragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SettingsFragment : Fragment() {
    //Variables
    private lateinit var MaxTravelDistance: EditText
    private lateinit var CaptureButton : Button
    private lateinit var ProceedToMapButton : Button
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private lateinit var maximumDistance : TextView
    private lateinit var appearanceTitle : TextView
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var isDarkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        MaxTravelDistance = view.findViewById(R.id.maxDistanceEditTxt)
        CaptureButton = view.findViewById(R.id.CaptureUnitAndDisBtn)
        ProceedToMapButton = view.findViewById(R.id.ProceedToMap)
        imageView = view.findViewById(R.id.imageView)
        textView = view.findViewById(R.id.textView)
        maximumDistance = view.findViewById(R.id.maximumDistance)
        appearanceTitle = view.findViewById(R.id.appearanceTitle)


        val spinner: Spinner = view.findViewById(R.id.spinnerUnit)
        val items = listOf("Miles", "Kilometer")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        imageView.setOnClickListener {
            isDarkMode = !isDarkMode
            updateUI()
        }

        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position) as String
                // Handle the selected item
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case when nothing is selected
            }
        }
        CaptureButton.setOnClickListener{

            val maxTravelDistance = MaxTravelDistance.text.toString()
            Toast.makeText(requireContext(), "Max Distance Saved", Toast.LENGTH_SHORT).show()
            Toast.makeText(requireContext(), "Please click the Proceed to Map button", Toast.LENGTH_SHORT).show()
        }

        ProceedToMapButton.setOnClickListener {
            val maxTravelDistance = MaxTravelDistance.text.toString()
            val mapFragment = MapFragment.newInstance(maxTravelDistance)
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.drawer_layout, mapFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        return view
    }

    private fun updateUI() {
        val backgroundColor: Int
        val textColor: Int

        if (isDarkMode) {
            backgroundColor = ContextCompat.getColor(requireContext(), R.color.darkBackground)
            textColor = ContextCompat.getColor(requireContext(), R.color.lightText)
        } else {
            backgroundColor = ContextCompat.getColor(requireContext(), R.color.lightBackground)
            textColor = ContextCompat.getColor(requireContext(), R.color.darkText)
        }

        // Change text color of EditText
        MaxTravelDistance.setTextColor(textColor)
        maximumDistance.setTextColor(textColor)
        textView.setTextColor(textColor)
        appearanceTitle.setTextColor(textColor)

        // Change background color of the fragment's view
        view?.setBackgroundColor(backgroundColor)

        // Change text color of TextViews and buttons
        // Add similar lines for other TextViews and buttons if needed
        CaptureButton.setTextColor(textColor)
        ProceedToMapButton.setTextColor(textColor)

        // Change text color of Spinner and its items
        // Assuming your Spinner has an ID called "spinnerUnit"
        val spinner: Spinner = view?.findViewById(R.id.spinnerUnit) ?: return
        spinner.setSelection(spinner.selectedItemPosition) // Refresh spinner items
        val spinnerTextColor = ContextCompat.getColor(requireContext(), R.color.spinnerText)
        spinner.setBackgroundColor(backgroundColor)
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}