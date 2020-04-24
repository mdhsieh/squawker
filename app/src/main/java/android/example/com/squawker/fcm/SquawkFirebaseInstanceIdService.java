package android.example.com.squawker.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.example.com.squawker.MainActivity;
import android.example.com.squawker.R;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * Listens for changes in the InstanceID
 */
public class SquawkFirebaseInstanceIdService extends FirebaseMessagingService {

    private static final String TAG = SquawkFirebaseInstanceIdService.class.getSimpleName();

    private static final String CHANNEL_ID = "squawker-notification-channel";

    // the unique int for a Squawker notification
    private static final int SQUAWKER_NOTIFICATION_ID = 1;

    // The onNewToken callback fires whenever a new token is generated.
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        // Get updated InstanceID token.
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token)
    {
        // This method is blank, but if you were to build a server that stores users token
        // information, this is where you'd send the token to the server.
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            String author = data.get("author");
            Log.d(TAG, "author: " + author);
            String authorKey = data.get("authorKey");
            Log.d(TAG, "author key: " + authorKey);
            String message = data.get("message");
            Log.d(TAG, "message: " + message);
            String date = data.get("date");
            Log.d(TAG, "date: " + date);

            // display a notification with the message
            //Context context = getApplicationContext();

            createNotificationChannel();

            // Create an explicit intent for an Activity in your app
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            // Android 8.0 = O and up require all notifications to be assigned to a channel or they will not appear
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_person_black_48dp)
                    .setContentTitle(author)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            // for backwards compatibility, make sure older device notification makes sound and will appear
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                    && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                builder.setDefaults(Notification.DEFAULT_SOUND);
            }

            // show the notification
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(SQUAWKER_NOTIFICATION_ID, builder.build());

            // use content provider to insert message into local database
            ContentValues values = new ContentValues();
            values.put(SquawkContract.COLUMN_DATE, date);
            values.put(SquawkContract.COLUMN_AUTHOR_KEY, SquawkContract.TEST_ACCOUNT_KEY);
            values.put(SquawkContract.COLUMN_AUTHOR, author);
            values.put(SquawkContract.COLUMN_MESSAGE, message);
            getContentResolver().insert(SquawkProvider.SquawkMessages.CONTENT_URI, values);
        }
        else
        {
            Log.d(TAG, "No data payload.");
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
