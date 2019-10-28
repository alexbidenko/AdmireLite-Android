package social.admire.admire.data

import social.admire.admire.profile.ChangeRegionFragment
import social.admire.admire.support.fragments.ImagesSliderFragment
import social.admire.admire.support.fragments.MainNavigationFragment
import social.admire.admire.profile.SettingsFragment
import social.admire.admire.events.fragments.EventsFragment
import social.admire.admire.events.fragments.OpenedEventFragment
import social.admire.admire.main.MainActivity
import social.admire.admire.map.MapFragment
import social.admire.admire.places.fragments.OpenedPlaceFragment
import social.admire.admire.places.fragments.PlacesFragment
import social.admire.admire.profile.AuthorizationFragment
import social.admire.admire.profile.ProfileFragment

object FragmentsProvider {

    lateinit var mainActivity: MainActivity
    var isStartApp = false

    lateinit var mainNavigationFragment: MainNavigationFragment
    lateinit var mapFragment: MapFragment

    lateinit var placesFragment: PlacesFragment
    lateinit var eventsFragment: EventsFragment
    lateinit var settingsFragment: SettingsFragment

    lateinit var openedPlaceFragment: OpenedPlaceFragment
    lateinit var openedEventFragment: OpenedEventFragment
    lateinit var imagesSliderFragment: ImagesSliderFragment

    lateinit var changeRegionFragment: ChangeRegionFragment
    lateinit var authorizationFragment: AuthorizationFragment
    lateinit var profileFragment: ProfileFragment
}