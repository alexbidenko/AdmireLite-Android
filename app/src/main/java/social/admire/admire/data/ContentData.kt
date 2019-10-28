package social.admire.admire.data

import org.json.JSONArray
import social.admire.admire.support.NetworkChecker
import social.admire.admire.events.data.EventObject
import social.admire.admire.places.data.PlaceObject

object ContentData {

    var placesData = ArrayList<PlaceObject>()
    var eventsData = ArrayList<EventObject>()

    var cities: JSONArray? = null

    var placesNetworkChecker: NetworkChecker? = null
    var eventsNetworkChecker: NetworkChecker? = null
    var citiesNetworkChecker: NetworkChecker? = null

    fun jsonToArray(json: JSONArray, addHttp: Boolean = false): Array<String> {
        val ans = ArrayList<String>()
        for(i in 0 until json.length()) {
            ans.add(
                if(addHttp && !json.getString(i).startsWith("http"))
                    "https://admire.social/images/" + json.getString(i)
                else json.getString(i)
            )
        }
        return ans.toTypedArray()
    }

    fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double) : Double {
        return 6371 * 1000 * Math.acos(
            Math.sin(lat1 * Math.PI / 180) * Math.sin(lat2 * Math.PI / 180) +
                    Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                    Math.cos(lon1 * Math.PI / 180 - lon2 * Math.PI / 180)
        )
    }
}