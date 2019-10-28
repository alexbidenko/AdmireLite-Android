package social.admire.admire.data

object Server {
    const val url = "https://admire.social"

    var city: String? = null
    lateinit var latitude: String
    lateinit var longitude: String

    const val KEY_SELECTED_CITY = "selected_city"
    const val KEY_SELECTED_LATITUDE = "selected_latitude"
    const val KEY_SELECTED_LONGITUDE = "selected_longitude"
}