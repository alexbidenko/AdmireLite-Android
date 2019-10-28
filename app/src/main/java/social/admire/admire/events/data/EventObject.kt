package social.admire.admire.events.data

import org.json.JSONObject

class EventObject(
    val id: Long,
    val name: String,
    val description: String,
    val screen_name: String,
    val site: String,
    val start_date: Long?,
    val finish_date: Long?,
    val latitude: Double?,
    val longitude: Double?,
    val photo_50: String,
    val photo_200: String
) {

    companion object {

        fun fromJSON(json: JSONObject): EventObject {
            return EventObject(
                json.getLong(EventsDBHelper.KEY_ID),
                json.getString(EventsDBHelper.KEY_NAME),
                json.getString(EventsDBHelper.KEY_DESCRIPTION),
                json.getString(EventsDBHelper.KEY_SCREEN_NAME),
                when {
                    json.getString(EventsDBHelper.KEY_SITE).toLowerCase().startsWith(
                        "http"
                    ) ->
                        json.getString(EventsDBHelper.KEY_SITE).toLowerCase()
                    json.getString(EventsDBHelper.KEY_SITE) != "" ->
                        "http://" + json.getString(EventsDBHelper.KEY_SITE).toLowerCase()
                    else -> ""
                },
                if (json.has(EventsDBHelper.KEY_START_DATE)) json.getLong(EventsDBHelper.KEY_START_DATE) * 1000 else null,
                if (json.has(EventsDBHelper.KEY_FINISH_DATE)) json.getLong(EventsDBHelper.KEY_FINISH_DATE) * 1000 else null,
                if (json.has(EventsDBHelper.KEY_PLACE))
                    json.getJSONObject(EventsDBHelper.KEY_PLACE).getDouble(EventsDBHelper.KEY_LATITUDE)
                else null,
                if (json.has(EventsDBHelper.KEY_PLACE))
                    json.getJSONObject(EventsDBHelper.KEY_PLACE).getDouble(EventsDBHelper.KEY_LONGITUDE)
                else null,
                json.getString(EventsDBHelper.KEY_PHOTO_50).replace("\\", ""),
                json.getString(EventsDBHelper.KEY_PHOTO_200).replace("\\", "")
            )
        }
    }
}