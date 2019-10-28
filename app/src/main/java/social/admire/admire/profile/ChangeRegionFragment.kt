package social.admire.admire.profile

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_change_region.view.*
import org.json.JSONArray
import org.json.JSONObject
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import android.view.inputmethod.InputMethodManager
import com.here.android.mpa.common.GeoCoordinate
import social.admire.admire.R
import social.admire.admire.data.ContentData
import social.admire.admire.data.FragmentsProvider
import social.admire.admire.data.Server
import social.admire.admire.main.MainActivity
import social.admire.admire.tasks.GetEventsTask
import social.admire.admire.tasks.GetPlacesTask


class ChangeRegionFragment : Fragment() {

    private val cities = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_change_region, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.change_region_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                for(i in 0 until cities.size) {
                    if(cities[i].toLowerCase().contains(view.change_region_input.text.toString().toLowerCase())) {
                        view.change_region_cities_layout.getChildAt(i).visibility = View.VISIBLE
                    } else {
                        view.change_region_cities_layout.getChildAt(i).visibility = View.GONE
                    }
                }
            }
        })

        view.change_region_button_back.setOnClickListener {
            val inputManager = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                HIDE_NOT_ALWAYS
            )

            FragmentsProvider.mainActivity.openFragment(
                FragmentsProvider.mainNavigationFragment
            )
        }

        view.change_region_input.text.clear()

        if(Server.city != null) {
            view.change_region_title_text.visibility = View.GONE
            view.change_region_title_button.visibility = View.VISIBLE
        }
    }

    fun setCities(data: JSONArray) {
        ContentData.cities = data

        view!!.change_region_input.text.clear()
        cities.clear()
        view!!.change_region_cities_layout.removeAllViews()

        for(i in 0 until data.length()) {
            cities.add(data.getJSONObject(i).getString("city"))

            val city = TextView(context)
            city.setPadding(16, 16, 16, 16)
            city.textSize = 24f
            city.text = data.getJSONObject(i).getString("city")
            city.setOnClickListener {
                setRegion(data.getJSONObject(i))
            }
            view!!.change_region_cities_layout.addView(city)
        }
    }

    private fun setRegion(region: JSONObject) {
        val inputManager = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            view!!.windowToken,
            HIDE_NOT_ALWAYS
        )

        val sp = context!!.getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE)
        sp.edit()
            .putString(Server.KEY_SELECTED_CITY, region.getString("city"))
            .putString(Server.KEY_SELECTED_LATITUDE, region.getString("latitude"))
            .putString(Server.KEY_SELECTED_LONGITUDE, region.getString("longitude"))
            .apply()

        Server.city = region.getString("city")
        Server.latitude = region.getString("latitude")
        Server.longitude = region.getString("longitude")

        if(FragmentsProvider.isStartApp) {
            FragmentsProvider.settingsFragment.setCity(region.getString("city"))
            FragmentsProvider.mainActivity.openFragment(
                FragmentsProvider.mainNavigationFragment
            )
            FragmentsProvider.mainNavigationFragment.clearState()
            FragmentsProvider.mapFragment.mapFragment.setCenter(
                GeoCoordinate(Server.latitude.toDouble(), Server.longitude.toDouble()),
                14.0
            )
            GetPlacesTask(FragmentsProvider.mainActivity).execute()
            GetEventsTask(FragmentsProvider.mainActivity).execute()
        } else {
            FragmentsProvider.mainActivity.startApp()
        }
    }
}