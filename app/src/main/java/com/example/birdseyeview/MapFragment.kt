import DatabaseHandler.Companion.COLUMN_HOTSPOTLATITUDE
import DatabaseHandler.Companion.COLUMN_HOTSPOTLONGITUDE
import DatabaseHandler.Companion.COLUMN_HOTSPOTNAME
import DatabaseHandler.Companion.COLUMN_SPECIESDATE
import DatabaseHandler.Companion.COLUMN_SPECIESDESCRIPTION
import DatabaseHandler.Companion.COLUMN_SPECIESGENDER
import DatabaseHandler.Companion.COLUMN_SPECIESNAME
import DatabaseHandler.Companion.COLUMN_SPECIESTYPEOFSIGHTING
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.birdseyeview.DatabaseHelper
import com.example.birdseyeview.R
import com.example.birdseyeview.SightingsFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.PolyUtil
import okhttp3.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class MapFragment : Fragment() {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    //private val maxDistanceInKilometers = 4
    private val apiKey = "347eb9dq4b5b" //old api - e5c74jk55n17
    private val directionsApiKey = "AIzaSyARvigmYK01stjUXbx7oq2krCuUAz2PE5w" // old api key AIzaSyDEZpWaKLI_c92mhqGS-UrwoF_8UPAv4qU
    private lateinit var LogObservation : Button
    private lateinit var DisplayObservation : Button

    private lateinit var getDirectionsButton: Button
    private var destinationLocation: LatLng? = null
    private var currentPolyline: Polyline? = null

    //variables for the clicked hotspot
    private var selectedHotspotName: String? = null
    private var selectedHotspotLatitude: Double? = null
    private var selectedHotspotLongitude: Double? = null
    private var maxTravelDistance: String? = null


    companion object {
        fun newInstance(maxTravelDistance: String): MapFragment {
            val fragment = MapFragment()
            val args = Bundle()
            args.putString("maxTravelDistance", maxTravelDistance)
            fragment.arguments = args
            return fragment
        }
    }

    @SuppressLint("Range")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        LogObservation = view.findViewById(R.id.logObservationButton)
        DisplayObservation = view.findViewById(R.id.DisplayObservationButton)
        maxTravelDistance = arguments?.getString("maxTravelDistance")
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(map: GoogleMap) {
                googleMap = map
                enableMyLocation()
                fetchUserLocationAndHotspots(maxTravelDistance)

                // Set up a marker click listener
                googleMap.setOnMarkerClickListener { marker ->
                    // Check if the clicked marker is not the user's location marker
                    if (marker.title != "Your Location") {
                        // Store the details of the selected hotspot
                        selectedHotspotName = marker.title
                        selectedHotspotLatitude = marker.position.latitude
                        selectedHotspotLongitude = marker.position.longitude

                        // Enable the Get Directions button
                        getDirectionsButton.isEnabled = true
                        // Store the destination coordinates for the clicked marker
                        destinationLocation = marker.position

                        // Retrieve and display sighting details for the selected hotspot
                        val databaseHelper = DatabaseHelper()
                        databaseHelper.getSightingsFromFirebase { sightings ->
                            for (sighting in sightings) {
                                if (sighting.birdingHotspotName == selectedHotspotName) {
                                    showSightingDetailsDialog(sighting)
                                    break  // Break out of the loop once the sighting is found
                                }
                            }
                        }
                    }
                    false // Return false to allow the default behavior (showing the marker info window)
                }

            }
        })

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        getDirectionsButton = view.findViewById(R.id.getDirectionsButton)
        getDirectionsButton.isEnabled = false // Disable the button initially

        getDirectionsButton.setOnClickListener {
            // Check if a destination location has been set
            if (destinationLocation != null) {
                Log.d("Button Click", "Get Directions button clicked") // Log the button click
                // Call the function to get directions to the selected hotspot
                getDirectionsToHotspot(destinationLocation!!)
            } else {
                Log.d("Button Click", "Get Directions button clicked, but destinationLocation is null")
            }
        }
        LogObservation.setOnClickListener {
            // Check if a hotspot has been selected
            if (selectedHotspotName != null && selectedHotspotLatitude != null && selectedHotspotLongitude != null) {
                // Create a bundle to pass hotspot details
                val bundle = Bundle()
                bundle.putString("hotspotName", selectedHotspotName)
                bundle.putString("hotspotLatitude", selectedHotspotLatitude.toString())
                bundle.putString("hotspotLongitude", selectedHotspotLongitude.toString())

                val sightingFragment = SightingsFragment() // Replace with your main content fragment
                sightingFragment.arguments = bundle
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.drawer_layout, sightingFragment) // Replace 'drawer_layout' with your actual container ID
                transaction.addToBackStack(null)
                transaction.commit()
                Log.d("buttonClickedSi", "LogObservation button clicked")
            } else {
                // Display a message to the user indicating that they need to select a hotspot first
                showToast("Please select a hotspot before logging an observation.")
            }
        }
        DisplayObservation.setOnClickListener {
            val databaseHelper = DatabaseHelper()
            databaseHelper.getSightingsFromFirebase { sightings ->
                // Display total number of observations in TextView
                val DisplayObsNumber: TextView = view.findViewById(R.id.textViewDisplaynumber)
                DisplayObsNumber.text = "Total Observations: ${sightings.size}"

                for (sighting in sightings) {
                    try {
                        val location = LatLng(sighting.birdingHotspotLatitude.toDouble(), sighting.birdingHotspotLongitude.toDouble())
                        val observationDetails =
                            "Species: ${sighting.speciesName}\nDescription: ${sighting.speciesDescription}\nGender: ${sighting.speciesGender}\nType of Sighting: ${sighting.speciesTypeSighting}\nDate: ${sighting.speciesCaptureDate}"

                        val observationMarker = MarkerOptions()
                            .position(location)
                            .title(sighting.birdingHotspotName)
                            .snippet(observationDetails)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))

                        googleMap.addMarker(observationMarker)
                    } catch (e: Exception) {
                        Log.e("DisplayObservation", "Error adding marker: ${e.message}")
                    }
                }
            }
        }




        return view
    }
    override fun onResume() {
        super.onResume()
        // Ensure maxTravelDistance is available within the class scope
        maxTravelDistance?.let {
            fetchUserLocationAndHotspots(it)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
        }
    }

    private fun fetchUserLocationAndHotspots(maxTravelDistance: String?) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val userLocation = LatLng(location.latitude, location.longitude)
                        googleMap.addMarker(MarkerOptions().position(userLocation).title("Your Location"))
                        fetchHotspotsFromEBird(userLocation,maxTravelDistance)
                    }
                }
        }
    }


    //Fetches the data from Ebird API
    private fun fetchHotspotsFromEBird(userLocation: LatLng, maxTravelDistance: String?) {
        val url = "https://api.ebird.org/v2/ref/hotspot/geo?lat=${userLocation.latitude}&lng=${userLocation.longitude}&dist=$maxTravelDistance"
        val request = Request.Builder()
            .url(url)
            .header("x-ebirdapitoken", apiKey)
            .build()

        val client = OkHttpClient()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()

                    // Log the API response
                    responseBody?.let { Log.d("API Response", it) }

                    if (!responseBody.isNullOrEmpty()) {
                        // Proceed to parse the response and add markers
                        responseBody?.let { parseHotspotsAndAddMarkers(it) }
                    } else {
                        Log.w("API Response", "No hotspots found within the specified range.")
                    }
                } else {
                    Log.e("API Response", "Failed to fetch eBird data: ${response.code}")
                    response.body?.string()?.let { Log.e("API Response", it) }
                }
            } catch (e: IOException) {
                Log.e("API Request", "Failed to make eBird API request: ${e.message}")
            }
        }
    }

    private fun parseHotspotsAndAddMarkers(responseBody: String) {
        if (responseBody.isNullOrEmpty()) {
            Log.w("API Response", "Response body is null or empty.")
            return
        }

        // Split the response into lines
        val lines = responseBody.split("\n")

        for (line in lines) {
            val parts = line.split(",")

            if (parts.size >= 7) {
                val name = parts[6]
                val latitude = parts[4].toDoubleOrNull()
                val longitude = parts[5].toDoubleOrNull()

                Log.d("Hotspot Info", "Name: $name, Latitude: $latitude, Longitude: $longitude")

                if (name != null && latitude != null && longitude != null) {
                    val hotspotLocation = LatLng(latitude, longitude)
                    val markerOptions = MarkerOptions()
                        .position(hotspotLocation)
                        .title(name)
                        .snippet("Lat: $latitude, Lng: $longitude")

                    requireActivity().runOnUiThread {
                        googleMap.addMarker(markerOptions)
                    }
                }
            }
        }
    }

    private fun getDirectionsToHotspot(destination: LatLng) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val origin = "${location.latitude},${location.longitude}"
                        val destinationStr = "${destination.latitude},${destination.longitude}"
                        val apiKey = directionsApiKey

                        val url = "https://maps.googleapis.com/maps/api/directions/json?origin=$origin&destination=$destinationStr&key=$apiKey"

                        val request = Request.Builder()
                            .url(url)
                            .build()

                        val client = OkHttpClient()

                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val response = client.newCall(request).execute()
                                if (response.isSuccessful) {
                                    val responseBody = response.body?.string()
                                    if (!responseBody.isNullOrEmpty()) {
                                        Log.d("Directions Response", responseBody) // Log the directions response
                                        // Parse the directions response here and draw the route on the map
                                        val directionsData = parseDirectionsResponse(responseBody)
                                        drawRouteOnMap(directionsData)
                                    } else {
                                        Log.w("Directions Response", "No data received from Google Directions API.")
                                    }
                                } else {
                                    Log.e("Directions Response", "Failed to fetch directions: ${response.code}")
                                    response.body?.string()?.let { Log.e("Directions Response", it) }
                                }
                            } catch (e: IOException) {
                                Log.e("Directions Request", "Failed to make directions API request: ${e.message}")
                            }
                        }
                    }
                }
        }
    }

    private fun parseDirectionsResponse(responseBody: String): DirectionsData {
        // Parse the JSON response to extract relevant data
        val data = DirectionsData()

        try {
            val jsonObject = JSONObject(responseBody)
            val routes = jsonObject.getJSONArray("routes")
            if (routes.length() > 0) {
                val route = routes.getJSONObject(0)
                val overviewPolyline = route.getJSONObject("overview_polyline")
                data.polyline = overviewPolyline.getString("points")
            }
        } catch (e: Exception) {
            Log.e("Directions Parsing", "Failed to parse directions response: ${e.message}")
        }

        return data
    }

    private fun drawRouteOnMap(directionsData: DirectionsData) {
        // Decode the polyline and add a Polyline to the map
        val polylineOptions = PolylineOptions()
            .addAll(PolyUtil.decode(directionsData.polyline))
            .width(10f)
            .color(Color.BLUE)

        requireActivity().runOnUiThread {
            if (currentPolyline != null) {
                currentPolyline?.remove()
            }
            currentPolyline = googleMap.addPolyline(polylineOptions)
        }
    }
    private fun showObservationDetailsDialog(observationDetails: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Observation Details")
        builder.setMessage(observationDetails)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
    private fun showSightingDetailsDialog(sighting: Sighting) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sighting Details")
        builder.setMessage(
            "Species: ${sighting.speciesName}\n" +
                    "Description: ${sighting.speciesDescription}\n" +
                    "Gender: ${sighting.speciesGender}\n" +
                    "Type of Sighting: ${sighting.speciesTypeSighting}\n" +
                    "Date: ${sighting.speciesCaptureDate}"
        )
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }


}
data class Sighting(
    val speciesId: String = "",
    val birdingHotspotName: String = "",
    val birdingHotspotLatitude: String = "",
    val birdingHotspotLongitude: String = "",
    val speciesName: String = "",
    val speciesDescription: String = "",
    val speciesGender: String = "",
    val speciesTypeSighting: String = "",
    val speciesCaptureDate: String = ""
) {
    // Add a no-argument constructor
    constructor() : this("", "", "", "", "", "", "", "", "")
}



data class DirectionsData(
    var polyline: String = ""
)
