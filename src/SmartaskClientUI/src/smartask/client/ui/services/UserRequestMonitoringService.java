package smartask.client.ui.services;

import java.util.Timer;
import java.util.TimerTask;

import smartask.client.core.Session;
import smartask.client.ui.R;
import smartask.client.ui.RequestNotificationActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import engine.services.PendingNotificationsService;
import engine.views.NotificationView;

/**
 * This service listens in a socket for monitoring requests. When a request is
 * received, this services displays a notification on the emulator. The user can
 * then click on the notification and answer the request.
 * 
 * @author Grupo 1
 */
public class UserRequestMonitoringService extends Service {
	private final static int waitTime = 5000; // 5s

	private NotificationManager notificationManager;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private Timer timer;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onCreate();
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		initializeTimer();
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		if (timer != null) {
			timer.cancel();
		}
		stopSelf();
	}

	private void initializeTimer() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if (Session.contains("username")) {
					for (NotificationView n : new PendingNotificationsService(
							(String) Session.get("username")).execute()) {
						Intent i = new Intent(getApplicationContext(),
								RequestNotificationActivity.class);
						i.putExtra("usernameTo", n.getUsernameTo());
						PendingIntent pendingIntent = PendingIntent
								.getActivity(getApplicationContext(), 0, i,
										Intent.FLAG_ACTIVITY_NEW_TASK);
						Notification notification = new Notification(
								R.drawable.question_marq,
								"Notification request", System
										.currentTimeMillis());
						notification.setLatestEventInfo(getApplication(),
								"User notification request",
								"You received a notification request",
								pendingIntent);
						notificationManager.notify(
								R.string.request_notification, notification);
					}
				}
			}
		}, 0, waitTime);
	}
}
