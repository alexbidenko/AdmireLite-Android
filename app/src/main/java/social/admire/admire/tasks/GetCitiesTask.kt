package social.admire.admire.tasks

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.AsyncTask
import org.json.JSONArray
import social.admire.admire.data.ContentData
import social.admire.admire.data.FragmentsProvider
import social.admire.admire.support.NetworkChecker
import social.admire.admire.data.Server
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

internal class GetCitiesTask : AsyncTask<String, String, String>() {

    override fun doInBackground(vararg params: String?): String {
        var response = ""

        try {
            val url = URL(Server.url + "/back/cities-ru.json")

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
        if(result == "") {
            if(ContentData.citiesNetworkChecker == null)
                ContentData.citiesNetworkChecker =
                    NetworkChecker(NetworkChecker.KEY_CITIES)
            else FragmentsProvider.mainActivity.unregisterReceiver(ContentData.citiesNetworkChecker)

            val intentFilter = IntentFilter()
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            FragmentsProvider.mainActivity.registerReceiver(ContentData.citiesNetworkChecker, intentFilter)
        } else {
            if(ContentData.citiesNetworkChecker != null) {
                FragmentsProvider.mainActivity.unregisterReceiver(ContentData.citiesNetworkChecker)
            }

            FragmentsProvider.changeRegionFragment.setCities(JSONArray(result))
        }
    }
}