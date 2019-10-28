package social.admire.admire.profile

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.fragment_authorization.view.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject

import social.admire.admire.R
import social.admire.admire.data.FragmentsProvider
import social.admire.admire.data.Server
import social.admire.admire.main.MainActivity
import java.io.IOException
import android.widget.ArrayAdapter
import android.widget.TextView


class AuthorizationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_authorization, container, false)
    }

    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.authorization_button_back.setOnClickListener {
            FragmentsProvider.mainActivity.openFragment(
                FragmentsProvider.mainNavigationFragment
            )
        }

        view.authorization_button_sing_in.setOnClickListener {
            view.authorization_error.visibility = View.INVISIBLE

            singIn(context!!,
                view.authorization_input_login.text.toString(),
                view.authorization_input_password.text.toString(),
                view,
                true)
        }

        view.authorization_button_registration.setOnClickListener {
            view.authorization_registration_error.visibility = View.INVISIBLE
            val sex = if(view.authorization_input_new_sex_man.isChecked) {
                "male"
            } else {
                "famale"
            }
            when {
                view.authorization_input_new_login.text!!.isEmpty() ->
                    view.authorization_input_new_login_layout.error = "Не введен логин"
                view.authorization_input_new_first_name.text!!.isEmpty() ->
                    view.authorization_input_new_first_name_layout.error = "Не указано имя"
                view.authorization_input_new_last_name.text!!.isEmpty() ->
                    view.authorization_input_new_last_name_layout.error = "Не указана фамилия"
                view.authorization_input_new_city.text!!.isEmpty() ->
                    view.authorization_input_new_city_layout.error = "Не указан город"
                view.authorization_input_new_email.text!!.isEmpty() ->
                    view.authorization_input_new_email_layout.error = "Не указан email"
                view.authorization_input_new_password.text!!.isEmpty() ->
                    view.authorization_input_new_password_layout.error = "Не введен пароль"
                view.authorization_input_new_password.text.toString() != view.authorization_input_new_password_repeat.text.toString() ->
                    view.authorization_input_new_password_repeat_layout.error = "Пароли не совпадают"
                else ->
                    registration(context!!, ProfileObject(
                        view.authorization_input_new_login.text.toString(),
                        view.authorization_input_new_password.text.toString(),
                        view.authorization_input_new_first_name.text.toString(),
                        view.authorization_input_new_last_name.text.toString(),
                        sex,
                        "Россия",
                        view.authorization_input_new_city.text.toString(),
                        view.authorization_input_new_email.text.toString()
                    ))
            }
        }

        view.authorization_to_registration_button.setOnClickListener {
            view.authorization_sing_in_layout.visibility = View.GONE
            view.authorization_registration_layout.visibility = View.VISIBLE

            val request = Request.Builder()
                .url(Server.url + "/back/cities-ru.json")
                .build()

            val call = OkHttpClient().newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {}

                override fun onResponse(call: Call, response: Response) {
                    FragmentsProvider.mainActivity.runOnUiThread {
                        val citiesJson = JSONArray(response.body()?.string()!!)
                        val cities = ArrayList<String>()
                        for (i in 0 until citiesJson.length()) {
                            cities.add(citiesJson.getJSONObject(i).getString("city"))
                        }

                        val adapter = ArrayAdapter<String>(context!!, android.R.layout.select_dialog_item, cities)
                        view.authorization_input_new_city.setAdapter(adapter)
                        view.authorization_input_new_city.threshold = 1
                    }
                }
            })

            setOnTextWatcher(view.authorization_input_new_login, view.authorization_input_new_login_layout)
            setOnTextWatcher(view.authorization_input_new_password, view.authorization_input_new_password_layout)
            setOnTextWatcher(view.authorization_input_new_first_name, view.authorization_input_new_first_name_layout)
            setOnTextWatcher(view.authorization_input_new_last_name, view.authorization_input_new_last_name_layout)
            setOnTextWatcher(view.authorization_input_new_city, view.authorization_input_new_city_layout)
            setOnTextWatcher(view.authorization_input_new_email, view.authorization_input_new_email_layout)
            setOnTextWatcher(view.authorization_input_new_password_repeat, view.authorization_input_new_password_repeat_layout)
        }
        view.authorization_to_sing_in_button.setOnClickListener {
            view.authorization_registration_layout.visibility = View.GONE
            view.authorization_sing_in_layout.visibility = View.VISIBLE
        }

        registrationSubject.subscribe {
            when (it) {
                "no_internet" -> {
                    view.authorization_registration_error.text =
                        context!!.resources.getString(R.string.authorization_no_internet)
                    view.authorization_registration_error.visibility = View.VISIBLE
                }
                "no_login" -> {
                    view.authorization_registration_error.text = "Выбранный логин уже занят"
                    view.authorization_registration_error.visibility = View.VISIBLE
                }
                "no_email" -> {
                    view.authorization_registration_error.text = "email уже занят"
                    view.authorization_registration_error.visibility = View.VISIBLE
                }
                "well" -> FragmentsProvider.mainActivity.openFragment(
                    FragmentsProvider.profileFragment
                )
            }
        }
    }

    private fun setOnTextWatcher(v: View, parent: TextInputLayout) {
        (v as TextView).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                parent.error = ""
            }
        })
    }

    companion object {
        private const val KEY_LOGIN_LOGIN = "login_enter"
        private const val KEY_LOGIN_PASSWORD = "password_enter"

        private const val KEY_REGISTRATION_LOGIN = "login"
        private const val KEY_REGISTRATION_PASSWORD = "password"
        private const val KEY_REGISTRATION_PASSWORD_REPEAT = "pass_double"
        private const val KEY_REGISTRATION_FIRST_NAME = "first_name"
        private const val KEY_REGISTRATION_LAST_NAME = "last_name"
        private const val KEY_REGISTRATION_SEX = "sex"
        private const val KEY_REGISTRATION_COUNTRY = "country"
        private const val KEY_REGISTRATION_CITY = "city"
        private const val KEY_REGISTRATION_EMAIL = "email"

        lateinit var singInSubject: ReplaySubject<Any>
        lateinit var registrationSubject: ReplaySubject<String>

        fun singIn(context: Context, login: String, password: String, view: View, toOpen: Boolean = false) {
            val sp = context.getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE)

            val builder = FormBody.Builder()
            builder.add(KEY_LOGIN_LOGIN, login)
            builder.add(KEY_LOGIN_PASSWORD, password)

            val formBody = builder.build()
            val request = Request.Builder()
                .url(Server.url + "/back/login.php")
                .post(formBody)
                .build()

            val call = OkHttpClient().newCall(request)
            call.enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    if(!toOpen) {
                        FragmentsProvider.mainActivity.runOnUiThread {
                            singInSubject.onNext(ProfileObject(sp))
                        }
                    } else {
                        view.authorization_error.text = context.resources.getString(R.string.authorization_no_internet)
                        view.authorization_error.visibility = View.VISIBLE
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body()?.string()
                    FragmentsProvider.mainActivity.runOnUiThread {
                        try {
                            if(responseBody?.startsWith("well") == true) {
                                val data = JSONObject(responseBody.substring(4))

                                sp.edit()
                                    .putString(ProfileFragment.KEY_LOGIN, data.getString(ProfileFragment.KEY_LOGIN))
                                    .putString(ProfileFragment.KEY_PASSWORD, data.getString(ProfileFragment.KEY_PASSWORD))
                                    .putString(ProfileFragment.KEY_FIRST_NAME, data.getString(ProfileFragment.KEY_FIRST_NAME))
                                    .putString(ProfileFragment.KEY_LAST_NAME, data.getString(ProfileFragment.KEY_LAST_NAME))
                                    .putString(ProfileFragment.KEY_SEX, data.getString(ProfileFragment.KEY_SEX))
                                    .putString(ProfileFragment.KEY_COUNTRY, data.getString(ProfileFragment.KEY_COUNTRY))
                                    .putString(ProfileFragment.KEY_CITY, data.getString(ProfileFragment.KEY_CITY))
                                    .putString(ProfileFragment.KEY_EMAIL, data.getString(ProfileFragment.KEY_EMAIL))
                                    .putString(ProfileFragment.KEY_AVATAR, data.getString(ProfileFragment.KEY_AVATAR))
                                    .putString(ProfileFragment.KEY_SOCIAL, data.getString(ProfileFragment.KEY_SOCIAL))
                                    .apply()

                                if(toOpen) {
                                    view.authorization_input_login.text?.clear()
                                    view.authorization_input_password.text?.clear()

                                    FragmentsProvider.mainActivity.openFragment(
                                        FragmentsProvider.profileFragment
                                    )
                                }

                                singInSubject.onNext(ProfileObject(sp))
                            } else if(toOpen) {
                                view.authorization_error.text = context.resources.getString(R.string.authorization_no_login_or_password)
                                view.authorization_error.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            })
        }

        fun registration(context: Context, user: ProfileObject) {
            val builder = FormBody.Builder()
            builder.add(KEY_REGISTRATION_LOGIN, user.login)
            builder.add(KEY_REGISTRATION_PASSWORD, user.password)
            builder.add(KEY_REGISTRATION_PASSWORD_REPEAT, user.password)
            builder.add(KEY_REGISTRATION_FIRST_NAME, user.first_name)
            builder.add(KEY_REGISTRATION_LAST_NAME, user.last_name)
            builder.add(KEY_REGISTRATION_SEX, user.sex)
            builder.add(KEY_REGISTRATION_COUNTRY, user.country)
            builder.add(KEY_REGISTRATION_CITY, user.city)
            builder.add(KEY_REGISTRATION_EMAIL, user.email)

            val formBody = builder.build()
            val request = Request.Builder()
                .url(Server.url + "/back/regist.php")
                .post(formBody)
                .build()

            val call = OkHttpClient().newCall(request)
            call.enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    FragmentsProvider.mainActivity.runOnUiThread {
                        registrationSubject.onNext("no_internet")
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    FragmentsProvider.mainActivity.runOnUiThread {
                        val responseBody = response.body()?.string()!!
                        if (responseBody.startsWith("well")) {
                            val sp = context.getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE)
                            sp.edit()
                                .putString(ProfileFragment.KEY_LOGIN, user.login)
                                .putString(ProfileFragment.KEY_PASSWORD, user.password)
                                .putString(ProfileFragment.KEY_FIRST_NAME, user.first_name)
                                .putString(ProfileFragment.KEY_LAST_NAME, user.last_name)
                                .putString(ProfileFragment.KEY_SEX, user.sex)
                                .putString(ProfileFragment.KEY_COUNTRY, user.country)
                                .putString(ProfileFragment.KEY_CITY, user.city)
                                .putString(ProfileFragment.KEY_EMAIL, user.email)
                                .apply()

                            singInSubject.onNext(user)
                            registrationSubject.onNext("well")

                            FragmentsProvider.mainActivity.openFragment(
                                FragmentsProvider.profileFragment
                            )
                        } else {
                            registrationSubject.onNext(responseBody)
                        }
                    }
                }
            })
        }
    }
}