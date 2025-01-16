import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.Date

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "BirdsEyeViewDatabase"
        private const val DATABASE_VERSION = 1

        // Define the table and columns
        const val TABLE_NEWUSER = "RegisterUser"
        const val COLUMN_USERNAME = "Username"
        const val COLUMN_FULLNAME = "Fullname"
        const val COLUMN_EMAIL = "Email"
        const val COLUMN_PASSWORD = "Password"

        const val COLUMN_SPECIESID = "SpeciesID"
        const val COLUMN_SPECIESNAME = "SpeciesName"
        const val COLUMN_SPECIESDESCRIPTION = "SpeciesDescription"
        const val COLUMN_SPECIESGENDER = "SpeciesGender"
        const val COLUMN_SPECIESTYPEOFSIGHTING = "SpeciesTypeofSighting"
        const val COLUMN_SPECIESDATE = "SpeciesDate"

        const val TABLE_SIGHTING = "TotalSightings"
        const val COLUMN_HOTSPOTNAME = "BirdingHotspotName"
        const val COLUMN_HOTSPOTLATITUDE = "BirdingHotspotLatitude"
        const val COLUMN_HOTSPOTLONGITUDE = "BirdingHotspotLongitude"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create the table
        val createTableQuery = "CREATE TABLE $TABLE_NEWUSER ($COLUMN_USERNAME TEXT PRIMARY KEY, $COLUMN_FULLNAME TEXT, $COLUMN_EMAIL TEXT, $COLUMN_PASSWORD TEXT);"
        db.execSQL(createTableQuery)
        val createSightingTableQuery = "CREATE TABLE $TABLE_SIGHTING " +
                "($COLUMN_SPECIESID TEXT PRIMARY KEY NOT NULL," +
                "$COLUMN_HOTSPOTNAME TEXT," +
                "$COLUMN_HOTSPOTLATITUDE REAL," +
                "$COLUMN_HOTSPOTLONGITUDE REAL," +
                "$COLUMN_SPECIESNAME TEXT," +
                "$COLUMN_SPECIESDESCRIPTION TEXT," +
                "$COLUMN_SPECIESGENDER TEXT," +
                "$COLUMN_SPECIESTYPEOFSIGHTING TEXT," +
                "$COLUMN_SPECIESDATE TEXT);"
        db.execSQL(createSightingTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades, if necessary
    }

    // Insert a new user into the database
    fun insertUser(username: String, fullname: String, email: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USERNAME, username)
        values.put(COLUMN_FULLNAME, fullname)
        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_PASSWORD, password)
        return db.insert(TABLE_NEWUSER, null, values)
    }
    // Insert a A birding Sighting into the database
    fun insertObservation(speciesID: String,birdingHotspotName: String, birdingHotspotLatitude: String, birdingHotspotLongitude: String, speciesName: String, speciesDescription: String, speciesGender: String, speciesTypeOfSighting: String, dateCaptured: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_SPECIESID, speciesID)
        values.put(COLUMN_HOTSPOTNAME, birdingHotspotName)
        values.put(COLUMN_HOTSPOTLATITUDE, birdingHotspotLatitude)
        values.put(COLUMN_HOTSPOTLONGITUDE, birdingHotspotLongitude)
        values.put(COLUMN_SPECIESNAME, speciesName)
        values.put(COLUMN_SPECIESDESCRIPTION, speciesDescription)
        values.put(COLUMN_SPECIESGENDER, speciesGender)
        values.put(COLUMN_SPECIESTYPEOFSIGHTING, speciesTypeOfSighting)
        values.put(COLUMN_SPECIESDATE, dateCaptured)
        return db.insert(TABLE_SIGHTING, null, values)
    }

    // Retrieve all users from the database
    fun getAllUsers(): Cursor {
        val db = this.readableDatabase
        return db.query(TABLE_NEWUSER, null, null, null, null, null, null)
    }
    fun getAllSightings(): Cursor {
        val db = this.readableDatabase
        return db.query(TABLE_SIGHTING, null, null, null, null, null, null)
    }




}
