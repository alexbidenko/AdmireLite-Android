package social.admire.admire.places.support

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.place_block.view.*
import social.admire.admire.data.ContentData
import social.admire.admire.data.FragmentsProvider
import social.admire.admire.R
import social.admire.admire.places.data.PlaceObject

class PlacesListLayoutAdapter : RecyclerView.Adapter<PlacesListLayoutAdapter.PRViewHolder>() {

    var count = if(ContentData.placesData.size > 10) 10 else ContentData.placesData.size

    override fun getItemCount() = count

    override fun onBindViewHolder(viewHolder: PRViewHolder, index: Int) {
        viewHolder.bind(ContentData.placesData[index])
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): PRViewHolder {
        return PRViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.place_block, viewGroup, false)
        )
    }

    class PRViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {

        fun bind(data: PlaceObject) {
            v.place_block_title.text = data.title
            v.place_block_rating.rating = data.rating.toFloat() / 200000
            v.place_block_description.text = data.description

            Glide.with(v.place_block_avatar)
                .load(data.avatar)
                .placeholder(R.drawable.image_preview)
                .into(v.place_block_avatar)

            v.setOnClickListener {
                FragmentsProvider.mainActivity.openFragment(
                    FragmentsProvider.openedPlaceFragment
                )
                FragmentsProvider.openedPlaceFragment.update(data)
            }
        }
    }
}