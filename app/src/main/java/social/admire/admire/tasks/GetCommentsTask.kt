package social.admire.admire.tasks

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.view.View
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.message_view.view.*
import org.json.JSONArray
import social.admire.admire.R
import social.admire.admire.data.Server
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.HttpsURLConnection

internal class GetCommentsTask(
    val context: Context,
    val layout: LinearLayout
) : AsyncTask<String, String, String>() {

    override fun doInBackground(vararg params: String?): String {
        var response = ""

        try {
            val url = URL(Server.url + "/back/get_comment.php?place_id=${params[0]}")

            val conn = url.openConnection() as HttpURLConnection
            conn.readTimeout = 8000
            conn.connectTimeout = 8000
            conn.requestMethod = "GET"
            conn.doInput = true
            conn.doOutput = true

            val os = conn.outputStream
            val writer = BufferedWriter(
                OutputStreamWriter(os, "UTF-8")
            )

            writer.flush()
            writer.close()
            os.close()
            val responseCode = conn.responseCode

            response = if (responseCode == HttpsURLConnection.HTTP_OK) {
                val br = BufferedReader(InputStreamReader(conn.inputStream))
                br.readText()
            } else {
                ""
            }
        } catch (e: Exception) {}

        return response
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onPostExecute(result: String) {
        if(result != "") {
            val data = JSONArray(result)

            if(data.length() != 0) {
                layout.removeAllViews()
            }

            for(i in 0 until data.length()) {
                val message = View.inflate(context, R.layout.message_view, null)
                Glide.with(message.message_view_avatar)
                    .load("https://admire.social/avatars/" + data.getJSONObject(i).getString("avatar"))
                    .circleCrop()
                    .placeholder(R.drawable.image_preview)
                    .into(message.message_view_avatar)
                message.message_view_from_who.text = "от ${data.getJSONObject(i).getString("login")}"
                val dateFormat = SimpleDateFormat("H:mm d.MM.yyyy")
                message.message_view_time.text =
                    dateFormat.format(
                        Date(data.getJSONObject(i).getString("time_comment").toLong())
                    )
                message.message_view_text.text = data.getJSONObject(i).getString("message")
                layout.addView(message)
            }
        }
    }
}