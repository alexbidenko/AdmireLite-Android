package social.admire.admire.places.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_opened_place.view.*
import android.support.v4.app.FragmentPagerAdapter
import android.view.inputmethod.InputMethodManager
import social.admire.admire.data.FragmentsProvider
import social.admire.admire.R
import social.admire.admire.places.data.PlaceObject


class OpenedPlaceFragment : Fragment() {

    val openedPlaceDescriptionFragment = OpenedPlaceDescriptionFragment()
    val openedPlaceChatFragment = OpenedPlaceChatFragment()

    lateinit var data: PlaceObject

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_opened_place, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.opened_place_button_back.setOnClickListener {
            hideKeyboard()

            FragmentsProvider.mainActivity.openFragment(
                FragmentsProvider.mainNavigationFragment
            )
        }
    }

    fun update(data: PlaceObject) {
        this.data = data

        view!!.opened_place_title.text = data.title

        Glide.with(view!!.opened_place_avatar)
            .load(data.avatar)
            .placeholder(R.drawable.image_preview)
            .into(view!!.opened_place_avatar)

        view!!.opened_place_view_pager.adapter = ThisAdapter(childFragmentManager)

        view!!.opened_place_view_pager_tab.setupWithViewPager(
            view!!.opened_place_view_pager
        )

        view!!.opened_place_view_pager_tab.setOnClickListener {
            hideKeyboard()
        }

        /*view!!.opened_place_view_pager.layoutParams =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, view!!.opened_place_scroll_view.height)*/
    }

    private inner class ThisAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getCount() = 2

        override fun getItem(position: Int): Fragment {
            return when(position) {
                0 -> openedPlaceDescriptionFragment
                1 -> openedPlaceChatFragment
                else -> openedPlaceDescriptionFragment
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when(position) {
                0 -> "Описание"
                1 -> "Комментарии"
                else -> ""
            }
        }
    }

    fun hideKeyboard() {
        val inputManager = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            view!!.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}