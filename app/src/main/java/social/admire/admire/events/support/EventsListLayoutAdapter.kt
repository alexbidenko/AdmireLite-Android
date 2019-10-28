package social.admire.admire.events.support

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.event_block.view.*
import social.admire.admire.data.ContentData
import social.admire.admire.data.FragmentsProvider
import social.admire.admire.R
import social.admire.admire.events.data.EventObject


class EventsListLayoutAdapter : RecyclerView.Adapter<EventsListLayoutAdapter.ERViewHolder>() {

    var count = if(ContentData.eventsData.size > 10) 10 else ContentData.eventsData.size

    override fun getItemCount() = count

    override fun onBindViewHolder(viewHolder: ERViewHolder, index: Int) {
        viewHolder.bind(ContentData.eventsData[index])
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ERViewHolder {
        return ERViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.event_block, viewGroup, false)
        )
    }

    class ERViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {

        fun bind(data: EventObject) {
            v.event_block_name.text = data.name
            v.event_block_description.text = data.description

            Glide.with(v.event_block_photo)
                .load(data.photo_200)
                .placeholder(R.drawable.image_preview)
                .into(v.event_block_photo)

            v.setOnClickListener {
                FragmentsProvider.mainActivity.openFragment(
                    FragmentsProvider.openedEventFragment
                )
                FragmentsProvider.openedEventFragment.update(data)
            }
        }
    }
}