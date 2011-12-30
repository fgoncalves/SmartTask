package smartask.client.ui.services;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import smartask.client.core.ApplicationConfiguration;
import smartask.client.core.Session;
import smartask.client.db.DatabaseSQLiteQueryEngine;
import smartask.client.observer.IEventListener;
import smartask.client.observer.IEventPublisher;
import smartask.client.ui.receivers.UserProximityAlertReceiver;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.IBinder;
import engine.views.CoordinateView;

/**
 * This service communicates with the main database to get each position of
 * monitored users, which have at least one task in common with this user.
 * 
 * @author Grupo 1
 */
public class PositionRequestService extends Service implements IEventListener {
	private HashMap<String, PendingIntent> pendingIntents;
	private LocationManager locationManager;
	private final static String proximityIntentAction = "smartask.ui.maps.USER_PROXIMITY_ALERT";

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private Timer timer;
	private UserProximityAlertReceiver receiver;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onCreate();
		pendingIntents = new HashMap<String, PendingIntent>();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		IntentFilter intentFilter = new IntentFilter(proximityIntentAction);
		receiver = new UserProximityAlertReceiver();
		registerReceiver(receiver, intentFilter);
		ApplicationConfiguration.getInstance().registerEventListener(this);
		initializeTimer();
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		if (timer != null) {
			timer.cancel();
		}
		unregisterReceiver(receiver);
		stopSelf();
	}

	@Override
	public void handleEvent(IEventPublisher source) {
		if (source instanceof ApplicationConfiguration) {
			timer.cancel();
			initializeTimer();
		}
	}

	private void teardownUserAlerts() {
		for (PendingIntent p : pendingIntents.values()) {
			locationManager.removeProximityAlert(p);
		}
		pendingIntents.clear();
	}

	private void putPendingIntent(String username, PendingIntent p) {
		pendingIntents.put(username, p);
	}

	private void setUserProximityAlert(String username, double lat, double lon,
			int requestCode) {
		float radius = (Float) Session.get("user.radius");

		Intent intent = new Intent(proximityIntentAction);
		intent.putExtra("usernameFrom", username);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), requestCode, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		putPendingIntent(username, pendingIntent);
		locationManager.addProximityAlert(lat, lon, radius, -1, pendingIntent);
	}

	private void initializeTimer() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if (Session.contains("username")) {
					teardownUserAlerts();
					HashMap<String, CoordinateView> user_positions = DatabaseSQLiteQueryEngine
							.getUsersPositions();
					for (Entry<String, CoordinateView> e : user_positions
							.entrySet()) {
						setUserProximityAlert(e.getKey(), ((double) e
								.getValue().getLatitude()) / 1E6, ((double) e
								.getValue().getLongitude()) / 1E6, 1);
					}
				}
			}
		}, 0, 5000);
	}
}
