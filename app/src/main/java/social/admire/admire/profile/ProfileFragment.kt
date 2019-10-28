package social.admire.admire.profile

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_profile.view.*

import social.admire.admire.R
import social.admire.admire.data.FragmentsProvider
import social.admire.admire.data.Server
import social.admire.admire.main.MainActivity

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.profile_button_back.setOnClickListener {
            FragmentsProvider.mainActivity.openFragment(
                FragmentsProvider.mainNavigationFragment
            )
        }

        view.profile_sing_out_button.setOnClickListener {
            val sp = context!!.getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE)
            sp.edit()
                .remove(KEY_LOGIN)
                .remove(KEY_PASSWORD)
                .apply()

            AuthorizationFragment.singInSubject.onNext(0)

            FragmentsProvider.mainActivity.openFragment(
                FragmentsProvider.mainNavigationFragment
            )
        }

        AuthorizationFragment.singInSubject.subscribe {
            if(it != 0) {
                update(it as ProfileObject)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun update(profile: ProfileObject) {
        Glide.with(view!!.profile_avatar)
            .load(if(profile.avatar == "" && profile.sex == "male") {
                "https://i0.wp.com/www.winhelponline.com/blog/wp-content/uploads/2017/12/user.png?fit=256%2C256&quality=100&ssl=1"
            } else if(profile.avatar == "" && profile.sex == "famale") {
                "https://iconfree.net/uploads/icon/2017/7/5/avatar-user-profile-icon-3763-512x512.png"
            } else {
                Server.url + "/avatars/" + profile.avatar
            })
            .centerCrop()
            .placeholder(R.drawable.image_preview)
            .into(view!!.profile_avatar)
        view!!.profile_name.text = profile.first_name + " " + profile.last_name
        view!!.profile_city.text = profile.city

        FragmentsProvider.settingsFragment.setSingIn(profile)
    }

    companion object {
        const val KEY_LOGIN = "login"
        const val KEY_PASSWORD = "password"
        const val KEY_FIRST_NAME = "first_name"
        const val KEY_LAST_NAME = "last_name"
        const val KEY_SEX = "sex"
        const val KEY_COUNTRY = "country"
        const val KEY_CITY = "city"
        const val KEY_EMAIL = "email"
        const val KEY_AVATAR = "avatar"
        const val KEY_SOCIAL = "social"
    }
}