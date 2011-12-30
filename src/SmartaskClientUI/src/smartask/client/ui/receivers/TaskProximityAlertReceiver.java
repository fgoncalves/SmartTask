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
 * This class defines a proximity alert receiver for tasks. When close to a
 * task, this receiver will generate a notification.
 * 
 * @author Grupo 1
 */
public class TaskProximityAlertReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent i = new Intent(context, TrackingMap.class);
		i.putExtra("fromnotificationtask", true);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		Notification n = new Notification(R.drawable.task_icon, "Task Alert!",
				System.currentTimeMillis());
		n.setLatestEventInfo(context, "Task notifier",
				"You are near a task assigned to you", pendingIntent);
		nm.notify(R.string.Task_Notification, n);
	}

}
