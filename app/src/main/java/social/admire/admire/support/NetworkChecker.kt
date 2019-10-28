package social.admire.admire.support

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import social.admire.admire.data.FragmentsProvider
import social.admire.admire.tasks.GetCitiesTask
import social.admire.admire.tasks.GetEventsTask
import social.admire.admire.tasks.GetPlacesTask


class NetworkChecker(private val key: String) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if(isOnline(context!!)) {
            when(key) {
                KEY_PLACES -> GetPlacesTask(FragmentsProvider.mainActivity).execute()
                KEY_EVENTS -> GetEventsTask(FragmentsProvider.mainActivity).execute()
                KEY_CITIES -> GetCitiesTask().execute()
            }
        }
    }

    private fun isOnline(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            netInfo != null && netInfo.isConnected
        } catch (e: NullPointerException) {
            e.printStackTrace()
            false
        }
    }

    companion object {
        const val KEY_PLACES = "places"
        const val KEY_EVENTS = "events"
        const val KEY_CITIES = "cities"
    }
}