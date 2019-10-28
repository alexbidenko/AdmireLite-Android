package social.admire.admire.places.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_opened_place_chat.view.*
import kotlinx.android.synthetic.main.message_view.view.*
import okhttp3.*
import social.admire.admire.data.FragmentsProvider.openedPlaceFragment
import social.admire.admire.R
import social.admire.admire.data.FragmentsProvider
import social.admire.admire.data.Server
import social.admire.admire.main.MainActivity
import social.admire.admire.profile.ProfileFragment
import social.admire.admire.tasks.GetCommentsTask
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class OpenedPlaceChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_opened_place_chat, container, false)
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sp = context!!.getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE)

        if(sp.contains(KEY_LOGIN)) {
            view.opened_place_input_comment_layout.visibility = View.VISIBLE
            view.opened_place_not_authorized_layout.visibility = View.GONE
        }

        view.opened_place_send_comment.setOnClickListener {
            val time = System.currentTimeMillis()

            val builder = FormBody.Builder()
            builder.add(KEY_PLACE_ID, openedPlaceFragment.data.id.toString())
            builder.add(KEY_LOGIN, sp.getString(ProfileFragment.KEY_LOGIN, "")!!)
            builder.add(KEY_TIME, time.toString())
            builder.add(KEY_MESSAGE, view.opened_place_input_comment.text.toString())

            val formBody = builder.build()
            val request = Request.Builder()
                .url(Server.url + "/back/add_comment.php")
                .post(formBody)
                .build()

            val call = OkHttpClient().newCall(request)
            call.enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {

                }
            })

            val message = View.inflate(context, R.layout.message_view, null)
            Glide.with(message.message_view_avatar)
                .load("https://admire.social/avatars/" + sp.getString(ProfileFragment.KEY_AVATAR, "")!!)
                .circleCrop()
                .placeholder(R.drawable.image_preview)
                .into(message.message_view_avatar)
            message.message_view_from_who.text = "от ${sp.getString(ProfileFragment.KEY_LOGIN, "")!!}"
            val dateFormat = SimpleDateFormat("H:mm d.MM.yyyy")
            message.message_view_time.text =
                dateFormat.format(
                    Date(time)
                )
            message.message_view_text.text = view.opened_place_input_comment.text.toString()
            view.opened_place_messages.addView(message)

            view.opened_place_input_comment.text.clear()
        }

        view.opened_place_sing_in_button.setOnClickListener {
            FragmentsProvider.mainActivity.openFragment(
                FragmentsProvider.authorizationFragment
            )
        }

        GetCommentsTask(context!!, view.opened_place_messages).execute(openedPlaceFragment.data.id.toString())
    }

    companion object {
        const val KEY_PLACE_ID = "place_id"
        const val KEY_LOGIN = "login"
        const val KEY_TIME = "time_comment"
        const val KEY_MESSAGE = "message"
    }
}