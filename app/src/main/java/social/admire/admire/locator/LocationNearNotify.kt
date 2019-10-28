package social.admire.admire.locator

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import social.admire.admire.R
import social.admire.admire.main.MainActivity

class LocationNearNotify: BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        } else {
            null
        }

        val mBuilder = NotificationCompat.Builder(context)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle("Admire советы")
            .setContentText(intent!!.getStringExtra("title") + " рядом с вами!")
            /*.setStyle(NotificationCompat.BigTextStyle()
                    .bigText(textOfNotification)
                    .setBigContentTitle(titleOfNotification)
                    .setSummaryText("Напоминает"))*/
            .setChannelId("follow_location")
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1500, 1000, 500))
            .setLights(-0xb5eb74, 3000, 5000)
            .setTicker("Admire советы")
            .setWhen(System.currentTimeMillis())

        val resultIntent = Intent(context, MainActivity::class.java)
        resultIntent.putExtra(
            FollowLocation.KEY_PLACE_ID, intent.getLongExtra(
                FollowLocation.KEY_PLACE_ID, 0))

        val stackBuilder = TaskStackBuilder.create(context!!)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder.setContentIntent(resultPendingIntent)

        notificationManager!!.notify(1, mBuilder.build())
    }
}