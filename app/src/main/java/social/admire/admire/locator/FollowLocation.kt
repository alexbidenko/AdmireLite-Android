package social.admire.admire.locator

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import social.admire.admire.data.ContentData
import social.admire.admire.places.data.PlaceObject
import social.admire.admire.places.data.PlacesDBHelper

class FollowLocation: Service() {

    private lateinit var locationManager: LocationManager
    private lateinit var places: ArrayList<PlaceObject>
    private val triggeredNotifications = ArrayList<Long>()

    private val locationListener = object : LocationListener {

        override fun onLocationChanged(location: Location) {
            changeLocation(location)
        }

        override fun onProviderDisabled(provider: String) {
        }

        override fun onProviderEnabled(provider: String) {
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        }
    }

    private fun changeLocation(location: Location?) {
        if (location == null) {
            return
        } else if (location.provider == LocationManager.GPS_PROVIDER || location.provider == LocationManager.NETWORK_PROVIDER) {
            for(i in 0 until places.size) {
                if(ContentData.getDistance(
                        location.latitude,
                        location.longitude,
                        places[i].latitude,
                        places[i].longitude
                    ) < 200 &&
                    !triggeredNotifications.contains(places[i].id)) {
                    triggeredNotifications.add(places[i].id)

                    val intent = Intent(this, LocationNearNotify::class.java)
                    intent.putExtra("title", places[i].title)
                    intent.putExtra(KEY_PLACE_ID, places[i].id)

                    val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)

                    val alarmForNextEvent = getSystemService(ALARM_SERVICE) as AlarmManager
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmForNextEvent.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis() + 100,
                            pendingIntent
                        )
                    } else {
                        alarmForNextEvent.set(
                            AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis() + 100,
                            pendingIntent
                        )
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationManager1 = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            val channelFollowLocation = NotificationChannel("follow_location",
                "Места рядом с вами",
                NotificationManager.IMPORTANCE_DEFAULT)
            mNotificationManager1.createNotificationChannel(channelFollowLocation)
        }

        val dBHelper = PlacesDBHelper(this)

        places = dBHelper.getPlaces()

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                (1000 * 300).toLong(),
                100f,
                locationListener
            )
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                (1000 * 300).toLong(),
                100f,
                locationListener
            )
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val KEY_PLACE_ID = "place_id"
    }
}