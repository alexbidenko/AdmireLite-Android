package social.admire.admire.map

import android.app.usage.UsageEvents
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.*
import android.support.v4.app.Fragment
import com.here.android.mpa.common.GeoCoordinate
import com.here.android.mpa.common.Image
import com.here.android.mpa.common.OnEngineInitListener
import com.here.android.mpa.mapping.Map
import com.here.android.mpa.mapping.SupportMapFragment
import com.here.android.mpa.mapping.MapMarker
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.here.android.mpa.common.ViewObject
import com.here.android.mpa.mapping.MapGesture
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.map_marker.view.*
import social.admire.admire.R
import social.admire.admire.data.ContentData
import social.admire.admire.data.FragmentsProvider
import social.admire.admire.data.Server
import social.admire.admire.events.data.EventObject
import social.admire.admire.places.data.PlaceObject

class MapFragmentView(val fragment: Fragment) {

    var map: Map? = null

    fun initMapFragment() {
        val mapFragment = fragment.childFragmentManager.findFragmentById(R.id.map_map) as SupportMapFragment

        mapFragment.init { error ->
            if (error == OnEngineInitListener.Error.NONE) {
                map = mapFragment.map

                if(listPlacesMarkers.isEmpty()) {
                    drawPlaces(ContentData.placesData)
                }
                if(listEventsMarkers.isEmpty()) {
                    drawEvents(ContentData.eventsData)
                }

                val gc = GeoCoordinate(Server.latitude.toDouble(), Server.longitude.toDouble())
                setCenter(
                    gc,
                    14.0
                )
                checkedLocations.add(gc)

                GetData.placesSubject.subscribe(object : Observer<ArrayList<PlaceObject>> {
                    override fun onComplete() {}

                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(t: ArrayList<PlaceObject>) {
                        drawPlaces(t)
                    }

                    override fun onError(e: Throwable) {}

                })

                mapFragment.mapGesture.addOnGestureListener(object : MapGesture.OnGestureListener {
                    override fun onPanStart() {}

                    override fun onPanEnd() {
                        setMarkersVisible()

                        if(map!!.zoomLevel > 14.0) {
                            var isDoUpdate = true
                            checkedLocations.forEach {
                                if (map!!.center.latitude < it.latitude + checkedRadius &&
                                    map!!.center.latitude > it.latitude - checkedRadius &&
                                    map!!.center.longitude < it.longitude + checkedRadius &&
                                    map!!.center.longitude > it.longitude - checkedRadius
                                ) isDoUpdate = false
                            }

                            if (isDoUpdate) {
                                if (checkedLocations.size > 20) checkedLocations.removeAt(0)
                                checkedLocations.add(map!!.center)
                                GetData.getPlaces(
                                    map!!.center.latitude,
                                    map!!.center.longitude
                                )
                            }
                        }
                    }

                    override fun onMultiFingerManipulationStart() {}

                    override fun onMultiFingerManipulationEnd() {}

                    override fun onMapObjectsSelected(list: List<ViewObject>): Boolean {
                        for(i in 0 until listPlacesMarkers.size) {
                            if(listPlacesMarkers[i] == list[0] as MapMarker) {
                                FragmentsProvider.mainActivity.openFragment(
                                    FragmentsProvider.openedPlaceFragment
                                )
                                FragmentsProvider.openedPlaceFragment.update(listPlaces[i])
                            }
                        }
                        for(i in 0 until listEventsMarkers.size) {
                            if(listEventsMarkers[i] == list[0] as MapMarker) {
                                FragmentsProvider.mainActivity.openFragment(
                                    FragmentsProvider.openedEventFragment
                                )
                                FragmentsProvider.openedEventFragment.update(listEvents[i])
                            }
                        }

                        return false
                    }

                    override fun onTapEvent(pointF: PointF): Boolean { return false }

                    override fun onDoubleTapEvent(pointF: PointF): Boolean { return false }

                    override fun onPinchLocked() {}

                    override fun onPinchZoomEvent(v: Float, pointF: PointF): Boolean { return false }

                    override fun onRotateLocked() {}

                    override fun onRotateEvent(v: Float): Boolean { return false }

                    override fun onTiltEvent(v: Float): Boolean { return false }

                    override fun onLongPressEvent(pointF: PointF): Boolean { return false }

                    override fun onLongPressRelease() {}

                    override fun onTwoFingerTapEvent(pointF: PointF): Boolean { return false }
                })

                setMarkersVisible()
            }
        }
    }

    fun drawPlaces(places: ArrayList<PlaceObject>) {
        if(map != null) {
            places.forEach {
                if(!listPlaces.contains(it))
                    addMapMarker(fragment.context!!, it, TYPE_PLACE)
            }
        }
    }

    fun drawEvents(events: ArrayList<EventObject>) {
        if(map != null) {
            events.forEach {
                if(it.latitude != null && it.longitude != null && !listEvents.contains(it))
                    addMapMarker(fragment.context!!, it, TYPE_EVENT)
            }
        }
    }

    fun setMarkersVisible() {
        if(map!!.zoomLevel < 14.0) {
            listPlacesMarkers.forEach {
                it.isVisible = false
            }
            listEventsMarkers.forEach {
                it.isVisible = false
            }
        } else {
            listPlacesMarkers.forEach {
                it.isVisible = true
            }
            listEventsMarkers.forEach {
                it.isVisible = true
            }
        }
    }

    private fun addMapMarker(context: Context, data: Any, type: Int) {
        val image = if(type == TYPE_PLACE) {
            (data as PlaceObject).avatar_small
        } else {
            (data as EventObject).photo_50
        }
        Glide.with(fragment)
            .asBitmap()
            .load(image)
            .circleCrop()
            .into(object : CustomTarget<Bitmap>() {

                override fun onLoadCleared(placeholder: Drawable?) {}

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val markerImage = Image()
                    markerImage.bitmap = createViewMarker(context, resource, type)

                    val marker = if(type == TYPE_PLACE) {
                        val place = data as PlaceObject
                        MapMarker(GeoCoordinate(place.latitude, place.longitude), markerImage)
                    } else {
                        val event = data as EventObject
                        MapMarker(GeoCoordinate(event.latitude!!, event.longitude!!), markerImage)
                    }

                    marker.isVisible = map!!.zoomLevel > 14.0

                    if(type == TYPE_PLACE) {
                        listPlacesMarkers.add(marker)
                        listPlaces.add(data as PlaceObject)
                    } else {
                        listEventsMarkers.add(marker)
                        listEvents.add(data as EventObject)
                    }

                    if(map != null) {
                        map!!.addMapObject(marker)
                    }
                }
            })
    }

    fun setCenter(location: GeoCoordinate, zoomLevel: Double = 17.0) {
        map!!.zoomLevel = zoomLevel
        map!!.setCenter(location, Map.Animation.NONE)

        if(zoomLevel != 17.0) {
            checkedLocations.add(location)
        }
    }

    companion object {

        private val checkedLocations = ArrayList<GeoCoordinate>()

        private val listPlacesMarkers = ArrayList<MapMarker>()
        private val listPlaces = ArrayList<PlaceObject>()
        private val listEventsMarkers = ArrayList<MapMarker>()
        private val listEvents = ArrayList<EventObject>()

        const val checkedRadius = 0.8

        const val TYPE_PLACE = 1
        const val TYPE_EVENT = 2

        fun createViewMarker(context: Context, bitmap: Bitmap, type: Int): Bitmap {
            val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.map_marker, null)
            view.map_marker_image.setImageBitmap(bitmap)

            view.map_marker_type.setImageResource(
                if(type == TYPE_PLACE) R.drawable.ic_location_on_marker_24dp else R.drawable.ic_event_marker_24dp
            )

            view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)
            val result = Bitmap.createBitmap(
                view.measuredWidth,
                view.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            val c = Canvas(result)
            view.draw(c)
            return result
        }
    }
}