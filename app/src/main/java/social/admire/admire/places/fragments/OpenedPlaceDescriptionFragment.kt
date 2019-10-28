package social.admire.admire.places.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_opened_place_description.view.*
import kotlinx.android.synthetic.main.tag_view.view.*
import social.admire.admire.data.FragmentsProvider
import social.admire.admire.data.FragmentsProvider.openedPlaceFragment
import social.admire.admire.R

class OpenedPlaceDescriptionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_opened_place_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        openedPlaceFragment.data.tags.forEach {
            val tag = View.inflate(context, R.layout.tag_view, null)
            tag.tag_view_text.text = it
            view.opened_place_tags.addView(tag)
        }
        view.opened_place_rating.rating = openedPlaceFragment.data.rating.toFloat() / 200000
        view.opened_place_location_layout.setOnClickListener {
            FragmentsProvider.mapFragment.setCenter(openedPlaceFragment.data)
            FragmentsProvider.mainActivity.openFragment(FragmentsProvider.mapFragment)
        }
        view.opened_place_share_layout.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_TEXT, "https://admire.social/link.php?p=${openedPlaceFragment.data.id}&s=${openedPlaceFragment.data.status}")
            shareIntent.type = "text/plain"
            startActivity(shareIntent)
        }
        view.opened_place_description.text = openedPlaceFragment.data.description

        for(i in 0 until openedPlaceFragment.data.images.size) {
            val image = ImageView(context)
            val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600)
            lp.setMargins(16, 16, 16, 16)
            image.layoutParams = lp
            image.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(image)
                .load(openedPlaceFragment.data.images[i])
                .centerCrop()
                .placeholder(R.drawable.image_preview)
                .into(image)

            image.setOnClickListener {
                FragmentsProvider.imagesSliderFragment.update(i, openedPlaceFragment.data.images)
                FragmentsProvider.mainActivity.openFragment(FragmentsProvider.imagesSliderFragment)
            }

            view.opened_place_images.addView(image)
        }
    }
}