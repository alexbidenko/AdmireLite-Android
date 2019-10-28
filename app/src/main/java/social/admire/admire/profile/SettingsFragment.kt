package social.admire.admire.profile

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_settings.view.*
import social.admire.admire.R
import social.admire.admire.data.ContentData
import social.admire.admire.data.FragmentsProvider
import social.admire.admire.data.Server
import social.admire.admire.main.MainActivity
import social.admire.admire.tasks.GetCitiesTask

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sp = context!!.getSharedPreferences(
            MainActivity.APP_PREFERENCES,
            Context.MODE_PRIVATE
        )

        view.settings_sing_in.setOnClickListener {
            FragmentsProvider.mainActivity.openFragment(
                FragmentsProvider.authorizationFragment
            )
        }
        view.settings_in_profile_layout.setOnClickListener {
            FragmentsProvider.mainActivity.openFragment(
                FragmentsProvider.profileFragment
            )
        }

        view.settings_change_region_city.text = sp.getString("city", "")

        view.settings_change_region.setOnClickListener {
            FragmentsProvider.mainActivity.openFragment(
                FragmentsProvider.changeRegionFragment
            )

            if(ContentData.cities == null) {
                GetCitiesTask().execute()
            } else {
                FragmentsProvider.changeRegionFragment.setCities(ContentData.cities!!)
            }
        }

        view.settings_change_follow_location.isChecked =
            sp.getBoolean(MainActivity.KEY_IS_FOLLOW_LOCATION, false)
        view.settings_change_follow_location.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) FragmentsProvider.mainActivity.startFollowLocation()
            else sp.edit().putBoolean(MainActivity.KEY_IS_FOLLOW_LOCATION, false).apply()
        }

        AuthorizationFragment.singInSubject.subscribe {
            setSingIn(it)
        }
    }

    fun setCity(city: String) {
        view!!.settings_change_region_city.text = city
    }

    fun setNoPermission(isPermission: Boolean) {
        if(!isPermission) {
            view!!.settings_change_follow_location.isChecked = false
        }
    }

    @SuppressLint("SetTextI18n")
    fun setSingIn(response: Any) {
        if(response != 0) {
            val profile = response as ProfileObject
            Glide.with(view!!.settings_profile_avatar)
                .load(
                    if (profile.avatar == "" && profile.sex == "male") {
                        "https://i0.wp.com/www.winhelponline.com/blog/wp-content/uploads/2017/12/user.png?fit=256%2C256&quality=100&ssl=1"
                    } else if (profile.avatar == "" && profile.sex == "famale") {
                        "https://iconfree.net/uploads/icon/2017/7/5/avatar-user-profile-icon-3763-512x512.png"
                    } else {
                        Server.url + "/avatars/" + profile.avatar
                    }
                )
                .placeholder(R.drawable.image_preview)
                .circleCrop()
                .into(view!!.settings_profile_avatar)
            view!!.settings_profile_name.text = "${profile.first_name}  ${profile.last_name}"

            view!!.settings_sing_in.visibility = View.GONE
            view!!.settings_in_profile_layout.visibility = View.VISIBLE
        } else {
            view!!.settings_in_profile_layout.visibility = View.GONE
            view!!.settings_sing_in.visibility = View.VISIBLE
        }
    }
}