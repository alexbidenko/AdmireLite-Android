package social.admire.admire.tasks

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.AsyncTask
import org.json.JSONArray
import social.admire.admire.data.ContentData
import social.admire.admire.data.FragmentsProvider
import social.admire.admire.data.Server
import social.admire.admire.events.data.EventObject
import social.admire.admire.events.data.EventsDBHelper
import social.admire.admire.main.MainActivity
import social.admire.admire.support.NetworkChecker
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection

internal class GetEventsTask(
    val context: MainActivity
) : AsyncTask<String, String, String>() {

    override fun doInBackground(vararg params: String?): String {
        var response = ""

        try {
            val url = URL(
                Server.url + "/back-py/get-events.py?city=" + URLEncoder.encode(
                    Server.city, "UTF-8"))

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
        val dbHelper = EventsDBHelper(context)

        if(result == "") {
            try {
                ContentData.eventsData = dbHelper.getEvents()
            } catch (e: Exception) {}

            if(ContentData.eventsNetworkChecker == null)
                ContentData.eventsNetworkChecker =
                    NetworkChecker(NetworkChecker.KEY_EVENTS)
            else context.unregisterReceiver(ContentData.eventsNetworkChecker)

            val intentFilter = IntentFilter()
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)

            if(ContentData.eventsNetworkChecker == null) {
                context.registerReceiver(ContentData.eventsNetworkChecker, intentFilter)
            }
        } else {
            if(ContentData.eventsNetworkChecker != null) {
                context.unregisterReceiver(ContentData.eventsNetworkChecker)
            }

            val allData = JSONArray(result.substringAfter("<body>").substringBeforeLast("</body>"))
            ContentData.eventsData.clear()
            for (i in 0 until allData.length()) {
                val d = allData.getJSONObject(i)
                ContentData.eventsData.add(
                    EventObject.fromJSON(d)
                )
            }

            dbHelper.setEvents(ContentData.eventsData)
        }

        if(!FragmentsProvider.mainActivity.isDataUse)
            FragmentsProvider.eventsFragment.update()

        FragmentsProvider.mapFragment.mapFragment.drawEvents(ContentData.eventsData)
    }
}