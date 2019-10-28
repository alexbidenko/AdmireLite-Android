package social.admire.admire.events.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_opened_event.view.*
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import social.admire.admire.data.FragmentsProvider
import social.admire.admire.R
import social.admire.admire.events.data.EventObject


class OpenedEventFragment : Fragment() {

    lateinit var data: EventObject

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_opened_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.opened_event_button_back.setOnClickListener {
            FragmentsProvider.mainActivity.openFragment(
                FragmentsProvider.mainNavigationFragment
            )
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun update(data: EventObject) {
        this.data = data

        view!!.opened_event_name.text = data.name

        Glide.with(view!!.opened_event_avatar)
            .load(data.photo_200)
            .placeholder(R.drawable.image_preview)
            .into(view!!.opened_event_avatar)
        val dateFormat = SimpleDateFormat("H:mm d.MM.yyyy")
        view!!.opened_event_time.text = if(data.start_date != null && data.finish_date != null) {
            "С ${dateFormat.format(Date(data.start_date))} по ${dateFormat.format(Date(data.finish_date))}"
        } else if(data.start_date != null) {
            dateFormat.format(Date(data.start_date))
        } else if(data.finish_date != null) {
            dateFormat.format(Date(data.finish_date))
        } else {
            "Не известно"
        }
        view!!.opened_event_group_layout.setOnClickListener {
            try {
                val siteIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/" + data.screen_name))
                startActivity(siteIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "Ссылка не действительна", Toast.LENGTH_LONG).show()
            }
        }
        if(data.site != "") {
            view!!.opened_event_site_layout.visibility = View.VISIBLE
            view!!.opened_event_site_layout.setOnClickListener {
                try {
                    val siteIntent = Intent(Intent.ACTION_VIEW, Uri.parse(data.site))
                    startActivity(siteIntent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Ссылка не действительна", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            view!!.opened_event_site_layout.visibility = View.GONE
        }
        if(data.latitude != null && data.longitude != null) {
            view!!.opened_event_location_layout.visibility = View.VISIBLE
            view!!.opened_event_location_layout.setOnClickListener {
                FragmentsProvider.mapFragment.setCenter(data)
                FragmentsProvider.mainActivity.openFragment(FragmentsProvider.mapFragment)
            }
        } else {
            view!!.opened_event_location_layout.visibility = View.GONE
        }
        view!!.opened_event_description.text = data.description
    }
}