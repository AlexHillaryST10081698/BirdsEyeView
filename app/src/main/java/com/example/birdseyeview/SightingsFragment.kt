package com.example.birdseyeview

import DatabaseHandler
import MapFragment
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SightingsFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    //variables
    private lateinit var SpeciesName: EditText
    private lateinit var SpeciesDescription: EditText
    private lateinit var SpeciesGender: EditText
    private lateinit var SpeciesDate: EditText
    private lateinit var SpeciesTypeOfSighting: EditText
    private lateinit var SaveButton : Button
    private lateinit var SpeciesID: EditText
    private lateinit var DisplaySightingsButton : Button
    private lateinit var dbHelper: DatabaseHelper

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
        val view = inflater.inflate(R.layout.fragment_sightings, container, false)

        dbHelper = DatabaseHelper()
        // Retrieve the hotspot details from the arguments
        val hotspotName = arguments?.getString("hotspotName")
        val hotspotLatitude = arguments?.getString("hotspotLatitude")
        val hotspotLongitude = arguments?.getString("hotspotLongitude")

        SpeciesID = view.findViewById(R.id.speciesIDText)
        SpeciesName = view.findViewById(R.id.SpeciedNameText)
        SpeciesDescription = view.findViewById(R.id.descriptionText)
        SpeciesGender = view.findViewById(R.id.MaleFemaleText)
        SpeciesDate = view.findViewById(R.id.dateText)
        SaveButton = view.findViewById(R.id.SaveBtn)


        // Find the Spinner view by its ID
        SpeciesTypeOfSighting= view.findViewById(R.id.TypeofSighting)




        SaveButton.setOnClickListener {

            val BIRDINGHOTSPOTNAME = hotspotName.toString()
            val BIRDINGHOTSPOTLATITUDE = hotspotLatitude.toString()
            val BIRDINGHOTSPOTLONGITUDE = hotspotLongitude.toString()
            val SPECIESID = SpeciesID.text.toString().trim()
            val SPECIESNAME = SpeciesName.text.toString().trim()
            val SPECIESDESCRIPTION = SpeciesDescription.text.toString().trim()
            val SPECIESGENDER = SpeciesGender.text.toString().trim()
            val SPECIESTYPESIGHTING = SpeciesTypeOfSighting.text.toString().trim()
            val SPECIESCAPTUREDATE = SpeciesDate.text.toString().trim()

            dbHelper.addSightingData(
                SPECIESID,
                BIRDINGHOTSPOTNAME,
                BIRDINGHOTSPOTLATITUDE,
                BIRDINGHOTSPOTLONGITUDE,
                SPECIESNAME,
                SPECIESDESCRIPTION,
                SPECIESGENDER,
                SPECIESTYPESIGHTING,
                SPECIESCAPTUREDATE
            ) { success ->
                if (success) {
                    Toast.makeText(requireContext(), "Sighting Captured", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to capture sighting", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SightingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}