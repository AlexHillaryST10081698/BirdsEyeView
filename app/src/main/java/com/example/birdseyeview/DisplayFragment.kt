package com.example.birdseyeview

import DatabaseHandler
import MapFragment
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class DisplayFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var ShowData : Button
    private lateinit var ShowDataonMap : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("Range")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =inflater.inflate(R.layout.fragment_display, container, false)
        val sightingsListView = view.findViewById<ListView>(R.id.sightingsListView)
        ShowData = view.findViewById(R.id.ShowDataBtn)
        ShowDataonMap = view.findViewById(R.id.ShowDataonMapBtn)



        ShowData.setOnClickListener {
            // Call the method to get all sightings
            val dbHelper = DatabaseHandler(requireContext())
            val cursor = dbHelper.getAllSightings()

            // Create an array or a list to store the sightings
            val sightingsList = ArrayList<String>()

            // Iterate through the cursor and extract data
            while (cursor.moveToNext()) {
                val speciesID = cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_SPECIESID))
                val hotspotName = cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_HOTSPOTNAME))
                val hotspotLatitude = cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_HOTSPOTLATITUDE))
                val hotspotLongitude = cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_HOTSPOTLONGITUDE))
                val speciesName = cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_SPECIESNAME))
                val speciesDescription = cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_SPECIESDESCRIPTION))
                val speciesGender = cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_SPECIESGENDER))
                val speciesTypeOfSighting = cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_SPECIESTYPEOFSIGHTING))
                val speciesDate = cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_SPECIESDATE))

                // Create a string to represent each sighting, you can format it as needed
                val sightingText = "SpeciesID: $speciesID.,Hotspot: $hotspotName, Latitude: $hotspotLatitude, Longitude: $hotspotLongitude ,Species: $speciesName, Description: $speciesDescription, Gender: $speciesGender ,Type of Sighting: $speciesTypeOfSighting, Date: $speciesDate"

                // Add the sighting text to the list
                sightingsList.add(sightingText)
            }

            // Close the cursor and the database
            cursor.close()
            dbHelper.close()
            // Create an ArrayAdapter to bind data to the ListView
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, sightingsList)

            // Set the adapter for the ListView
            sightingsListView.adapter = adapter

        }
        ShowDataonMap.setOnClickListener {
            val mapFragment = MapFragment() // Replace 'MapFragment()' with your Map Fragment class
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.drawer_layout, mapFragment) // 'R.id.fragment_container' is the container ID for the MapFragment
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DisplayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}