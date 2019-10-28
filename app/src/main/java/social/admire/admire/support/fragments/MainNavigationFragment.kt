package social.admire.admire.support.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main_navigation.view.*
import social.admire.admire.R
import social.admire.admire.data.FragmentsProvider
import social.admire.admire.data.FragmentsProvider.eventsFragment
import social.admire.admire.data.FragmentsProvider.mapFragment
import social.admire.admire.data.FragmentsProvider.placesFragment
import social.admire.admire.data.FragmentsProvider.settingsFragment

class MainNavigationFragment : Fragment() {

    private lateinit var activeFragment: Fragment
    var selectedItem = R.id.main_navigation_places

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_navigation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.beginTransaction()
            .add(R.id.main_navigation_container, placesFragment)
            .add(R.id.main_navigation_container, eventsFragment)
            .hide(eventsFragment)
            .add(R.id.main_navigation_container, settingsFragment)
            .hide(settingsFragment).commit()

        activeFragment = placesFragment

        view.main_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.main_navigation_places -> {
                    selectedItem = R.id.main_navigation_places
                    setFragment(placesFragment)
                }
                R.id.main_navigation_events -> {
                    selectedItem = R.id.main_navigation_events
                    setFragment(eventsFragment)
                }
                R.id.main_navigation_map -> {
                    FragmentsProvider.mainActivity.openFragment(mapFragment)
                }
                R.id.main_navigation_profile -> {
                    selectedItem = R.id.main_navigation_profile
                    setFragment(settingsFragment)
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun setFragment(f: Fragment) {
        childFragmentManager.beginTransaction()
            .hide(activeFragment).show(f)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit()

        activeFragment = f
    }

    fun clearState() {
        setFragment(placesFragment)
    }
}