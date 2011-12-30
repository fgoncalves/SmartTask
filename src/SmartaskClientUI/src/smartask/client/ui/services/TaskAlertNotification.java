package smartask.client.ui.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import smartask.client.core.Session;
import smartask.client.db.DatabaseSQLiteQueryEngine;
import smartask.client.ui.receivers.TaskProximityAlertReceiver;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import engine.views.CoordinateView;
import engine.views.TaskView;

/**
 * This class stores proximity alerts for each task assigned to the user.
 * 
 * @author Grupo 1
 */
public class TaskAlertNotification {
	private final static String proximityIntentAction = "smartask.ui.maps.PROXIMITY_ALERT";
	private static TaskAlertNotification instance = null;

	private HashMap<Integer, ArrayList<PendingIntent>> pendingIntents;

	private TaskProximityAlertReceiver receiver;
	private LocationManager locationManager;

	private TaskAlertNotification() {
		pendingIntents = new HashMap<Integer, ArrayList<PendingIntent>>();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public synchronized static TaskAlertNotification getInstance() {
		if (instance == null) {
			instance = new TaskAlertNotification();
		}
		return instance;
	}

	public void setup(Context c) {
		locationManager = (LocationManager) c
				.getSystemService(Context.LOCATION_SERVICE);
		IntentFilter intentFilter = new IntentFilter(proximityIntentAction);
		receiver = new TaskProximityAlertReceiver();
		c.registerReceiver(receiver, intentFilter);
	}

	public void tearDown(Context c) {
		c.unregisterReceiver(receiver);
	}

	public void reconfigure(Context c) {
		teardownTaskAlerts(c);
		setupTaskAlerts(c);
	}

	public void setupTaskAlerts(Context c) {
		Collection<TaskView> tasks = DatabaseSQLiteQueryEngine
				.selectAllUncompletedTasks((String) Session.get("username"));
		for (TaskView tv : tasks) {
			if (!pendingIntents.containsKey(tv.getId())) {
				for (CoordinateView cv : tv.getLocals()) {
					setTaskProximityAlert(c, tv.getId(), ((double) cv
							.getLatitude()) / 1E6,
							((double) cv.getLongitude()) / 1E6, 1);
				}
			}
		}
	}

	private void teardownTaskAlerts(Context c) {
		for (ArrayList<PendingIntent> list : pendingIntents.values()) {
			for (PendingIntent i : list) {
				locationManager.removeProximityAlert(i);
			}
		}
		pendingIntents.clear();
	}

	private void setTaskProximityAlert(Context c, Integer task_id, double lat,
			double lon, int requestCode) {
		float radius = (Float) Session.get("task.radius");

		Intent intent = new Intent(proximityIntentAction);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(c
				.getApplicationContext(), requestCode, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		putPendingIntent(task_id, pendingIntent);
		locationManager.addProximityAlert(lat, lon, radius, -1, pendingIntent);
	}

	private void putPendingIntent(Integer task_id, PendingIntent p) {
		if (pendingIntents.containsKey(task_id)) {
			pendingIntents.get(task_id).add(p);
			return;
		}
		ArrayList<PendingIntent> list = new ArrayList<PendingIntent>();
		list.add(p);
		pendingIntents.put(task_id, list);
	}
}
