package smartask.client.ui.receivers;

import smartask.client.ui.R;
import smartask.client.ui.maps.TrackingMap;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This class defines a user proximity alert receiver. When near to a user, this
 * class will generate a notification.
 * 
 * @author Grupo 1
 */
public class UserProximityAlertReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent i = new Intent(context, TrackingMap.class);
		i.putExtra("fromnotificationuser", true);
		String username = intent.getStringExtra("usernameFrom");
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		Notification n = new Notification(R.drawable.user3dnotification,
				"User Alert!", System.currentTimeMillis());
		n.setLatestEventInfo(context, "user notifier", "User " + username
				+ " is near you", pendingIntent);
		nm.notify(R.string.User_Notification, n);
	}

}
