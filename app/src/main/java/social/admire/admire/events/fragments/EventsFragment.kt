package social.admire.admire.events.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_events.view.*
import social.admire.admire.data.ContentData
import social.admire.admire.data.FragmentsProvider
import social.admire.admire.R
import social.admire.admire.events.support.EventsListLayoutAdapter
import social.admire.admire.tasks.GetEventsTask

class EventsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.events_list_layout.layoutManager = LinearLayoutManager(context)
        view.events_list_layout.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if((recyclerView.layoutManager as LinearLayoutManager).itemCount <=
                    (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() + 3) {
                    val count = (recyclerView.adapter as EventsListLayoutAdapter).count
                    (recyclerView.adapter as EventsListLayoutAdapter).count =
                        if(count != ContentData.eventsData.size && count + 5 < ContentData.eventsData.size) {
                            count + 5
                        } else {
                            ContentData.eventsData.size
                        }
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
            }
        })

        view.events_swipe_refresh.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorSilver
        )
        view.events_swipe_refresh.setOnRefreshListener {
            view.events_swipe_refresh.isRefreshing = true

            GetEventsTask(FragmentsProvider.mainActivity).execute()
        }
    }

    fun update() {
        if(view!!.events_list_layout.adapter == null) view!!.events_list_layout.adapter =
            EventsListLayoutAdapter()
        view!!.events_list_layout.adapter!!.notifyDataSetChanged()

        view!!.events_progress_load.visibility = View.GONE
        view!!.events_swipe_refresh.isRefreshing = false
    }
}