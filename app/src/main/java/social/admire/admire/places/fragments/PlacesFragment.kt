package social.admire.admire.places.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_places.view.*
import social.admire.admire.data.ContentData
import social.admire.admire.locator.FollowLocation
import social.admire.admire.data.FragmentsProvider
import social.admire.admire.R
import social.admire.admire.places.support.PlacesListLayoutAdapter
import social.admire.admire.tasks.GetPlacesTask

class PlacesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_places, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.places_list_layout.layoutManager = LinearLayoutManager(context)
        view.places_list_layout.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if((recyclerView.layoutManager as LinearLayoutManager).itemCount <=
                        (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() + 3) {
                    val count = (recyclerView.adapter as PlacesListLayoutAdapter).count
                    (recyclerView.adapter as PlacesListLayoutAdapter).count =
                        if(count != ContentData.placesData.size && count + 5 < ContentData.placesData.size) {
                            count + 5
                        } else {
                            ContentData.placesData.size
                        }
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
            }
        })

        view.places_swipe_refresh.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorSilver
        )
        view.places_swipe_refresh.setOnRefreshListener {
            view.places_swipe_refresh.isRefreshing = true

            GetPlacesTask(FragmentsProvider.mainActivity).execute()
        }
    }

    fun update() {
        if(view!!.places_list_layout.adapter == null) view!!.places_list_layout.adapter =
            PlacesListLayoutAdapter()
        view!!.places_list_layout.adapter!!.notifyDataSetChanged()

        view!!.places_progress_load.visibility = View.GONE
        view!!.places_swipe_refresh.isRefreshing = false

        val toOpenId = FragmentsProvider.mainActivity.intent.getLongExtra(FollowLocation.KEY_PLACE_ID, 0L)

        if(toOpenId != 0L) {
            for(i in 0 until ContentData.placesData.size) {
                if(ContentData.placesData[i].id == toOpenId) {
                    FragmentsProvider.mainActivity.openFragment(
                        FragmentsProvider.openedPlaceFragment
                    )
                    FragmentsProvider.openedPlaceFragment.update(ContentData.placesData[i])
                }
            }
        }
    }
}