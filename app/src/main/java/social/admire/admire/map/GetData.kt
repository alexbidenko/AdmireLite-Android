package social.admire.admire.map

import io.reactivex.subjects.PublishSubject
import okhttp3.*
import org.json.JSONArray
import social.admire.admire.data.Server
import social.admire.admire.places.data.PlaceObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

object GetData {

    val placesSubject = PublishSubject.create<ArrayList<PlaceObject>>()
    val eventsSubject = PublishSubject.create<ArrayList<EventObject>>()

    fun getPlaces(latitude: Double, longitude: Double) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(Server.url + "/back/get-places-by-coord.php?latitude=$latitude&longitude=$longitude")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                response.body()?.string().let {
                    val places = ArrayList<PlaceObject>()
                    for(i in 0 until JSONArray(it).length()) {
                        places.add(PlaceObject.fromJSON(
                            JSONArray(it).getJSONObject(i)
                        ))
                    }
                    placesSubject.onNext(places)
                }
            }

        })
    }

    fun getEvents() {

    }
}