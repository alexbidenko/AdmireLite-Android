package social.admire.admire.tasks

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.AsyncTask
import org.json.JSONArray
import social.admire.admire.data.ContentData
import social.admire.admire.data.FragmentsProvider
import social.admire.admire.data.Server
import social.admire.admire.main.MainActivity
import social.admire.admire.places.data.PlaceObject
import social.admire.admire.places.data.PlacesDBHelper
import social.admire.admire.support.NetworkChecker
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

internal class GetPlacesTask(
    val context: MainActivity
) : AsyncTask<String, String, String>() {

    override fun doInBackground(vararg params: String?): String {
        var response = ""

        try {
            val url = URL(Server.url + "/back/get-places-by-coord.php?latitude=${Server.latitude}&longitude=${Server.longitude}")

            val conn = url.openConnection() as HttpURLConnection
            conn.readTimeout = 8000
            conn.connectTimeout = 8000
            conn.requestMethod = "GET"
            conn.doInput = true
            conn.doOutput = true

            val os = conn.outputStream
            val writer = BufferedWriter(
                OutputStreamWriter(os, "UTF-8")
            )

            writer.flush()
            writer.close()
            os.close()
            val responseCode = conn.responseCode

            response = if (responseCode == HttpsURLConnection.HTTP_OK) {
                val br = BufferedReader(InputStreamReader(conn.inputStream))
                br.readText()
            } else {
                ""
            }
        } catch (e: Exception) {}

        return response
    }

    override fun onPostExecute(result: String) {
        val dbHelper = PlacesDBHelper(context)

        if(result == "") {
            try {
                ContentData.placesData = dbHelper.getPlaces()
            } catch (e: Exception) {}

            if(ContentData.placesNetworkChecker == null)
                ContentData.placesNetworkChecker =
                    NetworkChecker(NetworkChecker.KEY_PLACES)
            else context.unregisterReceiver(ContentData.placesNetworkChecker)

            val intentFilter = IntentFilter()
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)

            if(ContentData.placesNetworkChecker == null) {
                context.registerReceiver(ContentData.placesNetworkChecker, intentFilter)
            }
        } else {
            if(ContentData.placesNetworkChecker != null) {
                context.unregisterReceiver(ContentData.placesNetworkChecker)
            }

            val allData = JSONArray(result)
            ContentData.placesData.clear()
            for (i in 0 until allData.length()) {
                val d = allData.getJSONObject(i)
                ContentData.placesData.add(
                    PlaceObject.fromJSON(d)
                )
            }

            dbHelper.setPlaces(ContentData.placesData)
        }

        if(!FragmentsProvider.mainActivity.isDataUse)
            FragmentsProvider.placesFragment.update()

        FragmentsProvider.mapFragment.mapFragment.drawPlaces(ContentData.placesData)
    }
}