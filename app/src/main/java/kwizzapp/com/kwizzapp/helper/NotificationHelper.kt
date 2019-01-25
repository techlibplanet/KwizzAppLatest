package kwizzapp.com.kwizzapp.helper

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import kwizzapp.com.kwizzapp.MainActivity
import kwizzapp.com.kwizzapp.R
import java.util.ArrayList


object NotificationHelper {

    val ERROR = Notification.CATEGORY_ERROR
    val STATUS = Notification.CATEGORY_STATUS
    private val NOTIFICATION_CHANNEL_NAME = "KwizzApp"
    val NOTIFICATION_CHANNEL_ID = "com.kwizzapp"
    private var notifyId = 0
    private lateinit var notificationManager : NotificationManager

//    @Synchronized
//    fun notify(context: Context, subText: String, notificationCategory: String) {
//        val builder = Notification.Builder(context)
//        builder.setSmallIcon(R.mipmap.ic_launcher_round)
//                .setContentTitle(context.getString(R.string.app_name))
//                .setContentText(subText)
//                .setVisibility(Notification.VISIBILITY_PUBLIC)
//                .setCategory(notificationCategory)
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                .setPriority(Notification.PRIORITY_MAX)
//
//        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.notify(++notifyId, builder.build())
//    }
//
//    @Synchronized
//    fun notifyGroupedError(context: Context, subText: String, summaryText: String, messages: ArrayList<String>) {
//        val builder = Notification.Builder(context)
//        builder.setSmallIcon(R.mipmap.ic_launcher_round)
//                .setContentTitle(context.getString(R.string.app_name))
//                .setContentText(subText)
//                .setVisibility(Notification.VISIBILITY_PUBLIC)
//                .setCategory(Notification.CATEGORY_ERROR)
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                .setPriority(Notification.PRIORITY_MAX)
//
//
//        val inboxStyle = Notification.InboxStyle()
//        inboxStyle.setBigContentTitle(subText)
//                .setSummaryText(summaryText)
//
//        for (message in messages) {
//            inboxStyle.addLine(message)
//        }
//
//        builder.setStyle(inboxStyle)
//                .setGroupSummary(true)
//                .setGroup(Constants.ACCOUNT_NAME)
//        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.notify(++notifyId, builder.build())
//    }


//==============================================================================================================================
//==============================================================================================================================
    // New Notification Api level 26 and above
    // Notice that the NotificationCompat.Builder constructor requires that you provide a channel ID.
    // This is required for compatibility with Android 8.0 (API level 26) and higher, but is ignored by older versions.

    // Create notification channel method
    private fun createNotificationChannel(context: Context){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = NOTIFICATION_CHANNEL_NAME
            // val description = "Description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            // channel.description = description
            channel.lightColor = Color.CYAN;
            channel.canShowBadge();
            channel.setShowBadge(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }


    @Synchronized
    fun notify(context: Context, subText: String, notificationCategory: String) {
        createNotificationChannel(context)
        // Create an explicit intent for an Activity in your app
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val mBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(context.getString(R.string.app_name))
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setContentText(subText)
                .setBadgeIconType(R.mipmap.ic_launcher)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setCategory(notificationCategory)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

                // Set the intent that will fire when the user taps the notification
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_round))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
        notificationManager.notify(++notifyId, mBuilder);
    }


    @Synchronized
    fun notifyGroupedError(context: Context, subText: String, summaryText: String, messages: ArrayList<String>){
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(context.getString(R.string.app_name))
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setContentText(subText)
                .setBadgeIconType(R.mipmap.ic_launcher)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setCategory(Notification.CATEGORY_ERROR)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

                // Set the intent that will fire when the user taps the notification
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_round))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        val inboxStyle = NotificationCompat.InboxStyle()
        inboxStyle.setBigContentTitle(subText)
                .setSummaryText(summaryText)

        for (message in messages) {
            inboxStyle.addLine(message)
        }

        builder.setStyle(inboxStyle)
                .setGroupSummary(true)
                .setGroup(NOTIFICATION_CHANNEL_NAME)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(++notifyId, builder.build())
    }

    @Synchronized
    fun notifyGroupedError(context: Context, subText: String, summaryText: String, messages: String){
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(context.getString(R.string.app_name))
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setContentText(subText)
                .setBadgeIconType(R.mipmap.ic_launcher)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setCategory(Notification.CATEGORY_ERROR)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

                // Set the intent that will fire when the user taps the notification
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_round))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        val inboxStyle = NotificationCompat.InboxStyle()
        inboxStyle.setBigContentTitle(subText)
                .setSummaryText(summaryText)

        inboxStyle.addLine(messages)

//        for (message in messages) {
//            inboxStyle.addLine(message)
//        }

        builder.setStyle(inboxStyle)
                .setGroupSummary(true)
                .setGroup(NOTIFICATION_CHANNEL_NAME)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(++notifyId, builder.build())
    }

}