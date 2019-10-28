package social.admire.admire.support.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_images_slider.view.*
import social.admire.admire.R
import social.admire.admire.data.FragmentsProvider

class ImagesSliderFragment : Fragment() {

    lateinit var images: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_images_slider, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.images_slider_button_back.setOnClickListener {
            FragmentsProvider.mainActivity.openFragment(
                FragmentsProvider.openedPlaceFragment
            )
        }
    }

    fun update(index: Int, images: Array<String>) {
        this.images = images
        view!!.images_slider_view_pager.adapter = ThisAdapter(childFragmentManager)
        view!!.images_slider_view_pager.currentItem = index
    }

    private inner class ThisAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getCount() = images.size

        override fun getItem(position: Int): Fragment {
            val image = ImageFragment()
            image.index = position
            return image
        }
    }
}