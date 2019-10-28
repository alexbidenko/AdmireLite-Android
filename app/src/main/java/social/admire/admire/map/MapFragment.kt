package social.admire.admire.map

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.here.android.mpa.common.GeoCoordinate
import kotlinx.android.synthetic.main.fragment_main_navigation.view.*
import kotlinx.android.synthetic.main.map_fragment.view.*

import social.admire.admire.R
import social.admire.admire.data.FragmentsProvider
import social.admire.admire.places.data.PlaceObject
import social.admire.admire.events.data.EventObject

class MapFragment : Fragment() {

    private lateinit var viewModel: MapViewModel
    val mapFragment = MapFragmentView(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.map_button_back.setOnClickListener {
            FragmentsProvider.mainNavigationFragment.view!!.main_navigation.selectedItemId =
                FragmentsProvider.mainNavigationFragment.selectedItem
            FragmentsProvider.mainActivity.backFragment()
        }

        mapFragment.initMapFragment()
    }

    fun setCenter(place: PlaceObject) {
        mapFragment.setCenter(GeoCoordinate(place.latitude, place.longitude))
    }

    fun setCenter(event: EventObject) {
        if(event.latitude != null && event.longitude != null)
            mapFragment.setCenter(GeoCoordinate(event.latitude, event.longitude))
    }
}
