package social.admire.admire.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.here.android.mpa.common.GeoCoordinate
import io.reactivex.subjects.ReplaySubject
import social.admire.admire.*
import social.admire.admire.data.ContentData
import social.admire.admire.data.FragmentsProvider
import social.admire.admire.data.FragmentsProvider.authorizationFragment
import social.admire.admire.data.FragmentsProvider.changeRegionFragment
import social.admire.admire.data.FragmentsProvider.eventsFragment
import social.admire.admire.data.FragmentsProvider.imagesSliderFragment
import social.admire.admire.data.FragmentsProvider.isStartApp
import social.admire.admire.data.FragmentsProvider.mainActivity
import social.admire.admire.data.FragmentsProvider.mainNavigationFragment
import social.admire.admire.data.FragmentsProvider.mapFragment
import social.admire.admire.data.FragmentsProvider.openedEventFragment
import social.admire.admire.data.FragmentsProvider.openedPlaceFragment
import social.admire.admire.data.FragmentsProvider.settingsFragment
import social.admire.admire.data.FragmentsProvider.placesFragment
import social.admire.admire.data.FragmentsProvider.profileFragment
import social.admire.admire.data.Server
import social.admire.admire.events.fragments.EventsFragment
import social.admire.admire.events.fragments.OpenedEventFragment
import social.admire.admire.locator.FollowLocation
import social.admire.admire.main.mvp.MainActivityPresenter
import social.admire.admire.main.mvp.MainActivityView
import social.admire.admire.map.MapFragment
import social.admire.admire.places.fragments.OpenedPlaceFragment
import social.admire.admire.places.fragments.PlacesFragment
import social.admire.admire.profile.AuthorizationFragment
import social.admire.admire.profile.ChangeRegionFragment
import social.admire.admire.profile.ProfileFragment
import social.admire.admire.profile.SettingsFragment
import social.admire.admire.support.fragments.ImagesSliderFragment
import social.admire.admire.support.fragments.MainNavigationFragment
import social.admire.admire.tasks.GetCitiesTask
import social.admire.admire.tasks.GetEventsTask
import social.admire.admire.tasks.GetPlacesTask
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : MvpAppCompatActivity(), MainActivityView {

    @InjectPresenter
    lateinit var mainActivityPresenter: MainActivityPresenter

    private lateinit var activeFragment: Fragment
    private val historyFragments = ArrayList<Fragment>()

    var isDataUse = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivityPresenter.initFragments()
    }

    override fun onResume() {
        super.onResume()

        if(isDataUse && ContentData.placesData.isNotEmpty() && ContentData.eventsData.isNotEmpty()) {
            placesFragment.update()
            eventsFragment.update()
        }
        isDataUse = false

        val sp = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val login = sp.getString(ProfileFragment.KEY_LOGIN, null)
        val password = sp.getString(ProfileFragment.KEY_PASSWORD, null)

        if(login != null && password != null) {
            AuthorizationFragment.singIn(this, login, password, profileFragment.view!!)
        }
    }

    override fun initFragments() {
        mainActivity = this

        AuthorizationFragment.singInSubject = ReplaySubject.create()
        AuthorizationFragment.registrationSubject = ReplaySubject.create()

        mainNavigationFragment = MainNavigationFragment()
        mapFragment = MapFragment()
        placesFragment = PlacesFragment()
        eventsFragment = EventsFragment()
        settingsFragment = SettingsFragment()
        openedPlaceFragment = OpenedPlaceFragment()
        openedEventFragment = OpenedEventFragment()
        imagesSliderFragment = ImagesSliderFragment()
        changeRegionFragment = ChangeRegionFragment()
        authorizationFragment = AuthorizationFragment()
        profileFragment = ProfileFragment()

        activeFragment = mainNavigationFragment

        supportFragmentManager.beginTransaction()
            .add(R.id.main_container, changeRegionFragment)
            .commit()

        val sp = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        if(sp.contains(Server.KEY_SELECTED_CITY)) {
            Server.city = sp.getString(Server.KEY_SELECTED_CITY, "") ?: ""
            Server.latitude = sp.getString(Server.KEY_SELECTED_LATITUDE, "") ?: ""
            Server.longitude = sp.getString(Server.KEY_SELECTED_LONGITUDE, "") ?: ""

            supportFragmentManager.beginTransaction().hide(changeRegionFragment).commit()

            startApp()
        } else {
            GetCitiesTask().execute()
        }

        if(sp.getBoolean(KEY_IS_FOLLOW_LOCATION, false)) {
            startFollowLocation()
        }
    }

    fun backFragment() {
        if(historyFragments.isNotEmpty()) {
            openFragment(historyFragments.last(), false)
            historyFragments.remove(historyFragments.last())
        } else {
            finish()
        }
    }

    override fun onBackPressed() {
        backFragment()
    }

    fun startApp() {
        isStartApp = true
        supportFragmentManager.beginTransaction().hide(changeRegionFragment).commit()

        supportFragmentManager.beginTransaction()
            .add(R.id.main_container, activeFragment)
            .add(R.id.main_container, openedPlaceFragment).hide(openedPlaceFragment)
            .add(R.id.main_container, openedEventFragment).hide(openedEventFragment)
            .add(R.id.main_container, imagesSliderFragment).hide(imagesSliderFragment)
            .add(R.id.main_container, authorizationFragment).hide(authorizationFragment)
            .add(R.id.main_container, mapFragment).hide(mapFragment)
            .add(R.id.main_container, profileFragment).hide(profileFragment)
            .commit()

        if(ContentData.placesData.isEmpty() || ContentData.eventsData.isEmpty()) {
            GetPlacesTask(this).execute()
            GetEventsTask(this).execute()
        } else {
            isDataUse = true
        }
    }

    fun openFragment(f: Fragment, addToBackStack: Boolean = true) {
        supportFragmentManager.beginTransaction()
            .hide(activeFragment).show(f)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            .commit()
        if(f == mainNavigationFragment && addToBackStack) {
            historyFragments.clear()
        } else if(addToBackStack) {
            fragmentAddToHistory(activeFragment)
        }
        activeFragment = f
    }

    private fun fragmentAddToHistory(f: Fragment) {
        while(historyFragments.contains(f)) {
            historyFragments.remove(historyFragments.last())
        }
        historyFragments.add(f)
    }

    fun startFollowLocation() {
        val missingPermissions = ArrayList<String>()

        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (result != PackageManager.PERMISSION_GRANTED) {
            missingPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (missingPermissions.isNotEmpty()) {
            val permissions = missingPermissions
                .toTypedArray()
            ActivityCompat.requestPermissions(this, permissions,
                REQUEST_CODE_ASK_PERMISSIONS
            )
        } else {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

            val grantResults = IntArray(permissions.size)
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED)
            onRequestPermissionsResult(
                REQUEST_CODE_ASK_PERMISSIONS, permissions,
                grantResults
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        val sp = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> {
                for (index in permissions.indices.reversed()) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        settingsFragment.setNoPermission(false)
                        return
                    }
                }
                sp.edit().putBoolean(KEY_IS_FOLLOW_LOCATION, true).apply()

                val followLocation = Intent(this, FollowLocation::class.java)
                startService(followLocation)
            }
        }
    }

    companion object {
        const val APP_PREFERENCES = "social.admire.admire"

        const val KEY_IS_FOLLOW_LOCATION = "is_follow_location"
        const val REQUEST_CODE_ASK_PERMISSIONS = 1
    }
}
