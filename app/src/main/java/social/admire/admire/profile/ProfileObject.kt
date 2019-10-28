package social.admire.admire.profile

import android.content.SharedPreferences

class ProfileObject {

    val login: String
    val password: String
    val first_name: String
    val last_name: String
    val sex: String
    val country: String
    val city: String
    val email: String
    val avatar: String
    val social: String?

    constructor(sp: SharedPreferences) {
        login = sp.getString(ProfileFragment.KEY_LOGIN, null) ?: ""
        password = sp.getString(ProfileFragment.KEY_PASSWORD, null) ?: ""
        first_name = sp.getString(ProfileFragment.KEY_FIRST_NAME, null) ?: ""
        last_name = sp.getString(ProfileFragment.KEY_LAST_NAME, null) ?: ""
        sex = sp.getString(ProfileFragment.KEY_SEX, null) ?: ""
        country = sp.getString(ProfileFragment.KEY_COUNTRY, null) ?: ""
        city = sp.getString(ProfileFragment.KEY_CITY, null) ?: ""
        email = sp.getString(ProfileFragment.KEY_EMAIL, null) ?: ""
        avatar = sp.getString(ProfileFragment.KEY_AVATAR, null) ?: ""
        social = sp.getString(ProfileFragment.KEY_SOCIAL, null) ?: ""
    }

    constructor(
        login: String,
        password: String,
        first_name: String,
        last_name: String,
        sex: String,
        country: String,
        city: String,
        email: String
    ) {
        this.login = login
        this.password = password
        this.first_name = first_name
        this.last_name = last_name
        this.sex = sex
        this.country = country
        this.city = city
        this.email = email
        this.avatar = ""
        this.social = null
    }
}