package smartask.client.ui.maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import smartask.client.core.Session;
import smartask.client.db.DatabaseSQLiteQueryEngine;
import smartask.client.ui.R;
import smartask.client.ui.maps.listeners.CallClickListener;
import smartask.client.ui.maps.listeners.IMClickListener;
import smartask.client.ui.maps.listeners.SMSClickListener;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import engine.services.UpdateUserLocalService;
import engine.views.CoordinateView;
import engine.views.TaskView;
import engine.views.UserView;

/**
 * This class provides a map view that displays all the tasks assigned to the
 * user and every monitored user.
 * 
 * @author Grupo 1
 */
public class TrackingMap extends MapActivity {
	private TaskOverlay taskOverlay;
	private UserOverlay userOverlay;
	private MyOverlay myLocationOverlay;
	private LocationManager locationManager;
	private MapController mapController;
	private RefreshThread rThread;
	private MapView mapView;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		rThread.exit();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent i = getIntent();
		if (i.getBooleanExtra("fromnotificationtask", false)) {
			((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
					.cancel(R.string.Task_Notification);
		}
		if (i.getBooleanExtra("fromnotificationuser", false)) {
			((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
					.cancel(R.string.User_Notification);
		}

		setContentView(R.layout.maps);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mapView = (MapView) findViewById(R.id.map_view);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(false);

		mapController = mapView.getController();

		Location l = locationManager.getLastKnownLocation("gps");
		GeoPoint geoPoint = new GeoPoint(38707708, -9136510);
		if (l != null) {
			geoPoint = new GeoPoint((int) (l.getLatitude() * 1E6), (int) (l
					.getLongitude() * 1E6));
		}

		new UpdateUserLocalService((String) Session.get("username"),
				new CoordinateView(geoPoint.getLatitudeE6(), geoPoint
						.getLongitudeE6())).execute();

		mapController.animateTo(geoPoint);
		mapController.setZoom(17);
		// ---Overlay
		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();

		// show tasks
		taskOverlay = new TaskOverlay(getResources().getDrawable(
				R.drawable.androidmarker), this);
		userOverlay = new UserOverlay(getResources().getDrawable(
				R.drawable.user), this);
		myLocationOverlay = new MyOverlay(getResources().getDrawable(
				R.drawable.red_user), new OverlayItem(geoPoint, "You", ""));
		listOfOverlays.add(taskOverlay);
		listOfOverlays.add(userOverlay);
		listOfOverlays.add(myLocationOverlay);

		Collection<TaskView> tasks = DatabaseSQLiteQueryEngine
				.selectAllUncompletedTasks((String) Session.get("username"));
		for (TaskView tv : tasks) {
			for (CoordinateView cv : tv.getLocals()) {
				GeoPoint point = new GeoPoint(cv.getLatitude(), cv
						.getLongitude());
				OverlayItem overlayitem = new OverlayItem(point, "Task name:\n"
						+ tv.getName(), "Description:\n" + tv.getDescription());
				taskOverlay.addOverlay(overlayitem);
			}
		}
		taskOverlay.refresh();
		setupForGPSAutoRefreshing();
		rThread = new RefreshThread();
		rThread.start();
	}

	protected final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATE = 0; // in
	// Meters
	protected final long MINIMUM_TIME_BETWEEN_UPDATE = 500; // in

	// Milliseconds

	/**
	 * This method will register a location change listener. This listener will
	 * update the map accordingly to the user's current position.
	 */
	private void setupForGPSAutoRefreshing() {// Updates map drawing
		List<String> providers = this.locationManager.getProviders(true);
		String provider = providers.get(0);

		locationManager.requestLocationUpdates(provider,
				MINIMUM_TIME_BETWEEN_UPDATE,
				MINIMUM_DISTANCE_CHANGE_FOR_UPDATE, myLocationOverlay);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/**
	 * Overlay of task items
	 * 
	 * @author Grupo 1
	 */
	private class TaskOverlay extends ItemizedOverlay<OverlayItem> {
		private ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		public Context mContext;

		public TaskOverlay(Drawable defaultMarker, Context context) {
			super(boundCenterBottom(defaultMarker));
			mContext = context;
			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return items.get(i);
		}

		@Override
		public int size() {
			return items.size();
		}

		public void addOverlay(OverlayItem overlay) {
			items.add(overlay);
		}

		public void refresh() {
			populate();
		}

		@Override
		protected boolean onTap(int index) {
			OverlayItem item;
			if (index < items.size()) {
				item = items.get(index);
				AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
				dialog.setTitle(item.getTitle());
				dialog.setMessage(item.getSnippet());
				dialog.show();
			}
			return true;
		}
	}

	/**
	 * This class implements an overlay of items, but the only item it contains
	 * is the user's current position. This overlay is update at the same time
	 * the user's position
	 * 
	 * @author Grupo 1
	 */
	private class MyOverlay extends ItemizedOverlay<OverlayItem> implements
			LocationListener {
		private ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

		public MyOverlay(Drawable marker, OverlayItem overlay) {
			super(boundCenterBottom(marker));
			items.add(overlay);
			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return items.get(i);
		}

		@Override
		public int size() {
			return items.size();
		}

		private void refresh() {
			populate();
		}

		@Override
		public void onLocationChanged(Location location) {
			int longitude = (int) (location.getLongitude() * 1E6);
			int latitude = (int) (location.getLatitude() * 1E6);
			GeoPoint geoPoint = new GeoPoint(latitude, longitude);
			OverlayItem i = new OverlayItem(geoPoint, "You", "");
			items.clear();
			items.add(i);
			refresh();

			mapController.animateTo(geoPoint);
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	/**
	 * This class is an implementation of an overlay of items, which represent
	 * the monitored users.
	 * 
	 * @author Grupo 1
	 */
	private class UserOverlay extends ItemizedOverlay<OverlayItem> {
		private ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		public Context mContext;

		public UserOverlay(Drawable defaultMarker, Context context) {
			super(boundCenterBottom(defaultMarker));
			mContext = context;
			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return items.get(i);
		}

		@Override
		public int size() {
			return items.size();
		}

		public void addOverlay(OverlayItem overlay) {
			items.add(overlay);
		}

		public void refresh() {
			populate();
		}

		public void clean() {
			items.clear();
		}

		@Override
		protected boolean onTap(int index) {
			OverlayItem item;
			if (index < items.size()) {
				item = items.get(index);
				Builder dialog = new Builder(mContext);
				dialog.setTitle(item.getTitle());
				dialog.setPositiveButton("Send an SMS", new SMSClickListener(
						item.getSnippet(), mContext));
				dialog.setNegativeButton("Call", new CallClickListener(item
						.getSnippet(), mContext));

				dialog.setNeutralButton("Instant messaging",
						new IMClickListener(item.getSnippet(), mContext));
				dialog.show();
			}
			return true;
		}
	}

	/**
	 * This thread refreshes each overlay at the same rate the user's position
	 * should be sent to the main database.
	 * 
	 * @author Grupo 1
	 */
	private class RefreshThread extends Thread {
		private boolean exit = false;

		public void exit() {
			exit = true;
		}

		@Override
		public void run() {
			int time = (Integer) Session.get("position.notification.rate");
			Log.i("RefreshThread", "Initializing...");
			while (!exit) {
				userOverlay.clean();
				HashMap<String, CoordinateView> user_positions = DatabaseSQLiteQueryEngine
						.getUsersPositions();
				for (Entry<String, CoordinateView> e : user_positions
						.entrySet()) {
					GeoPoint point = new GeoPoint(e.getValue().getLatitude(), e
							.getValue().getLongitude());
					UserView uv = DatabaseSQLiteQueryEngine.selectUser(e
							.getKey());
					OverlayItem overlayitem = new OverlayItem(point, "User: "
							+ e.getKey(), uv.getPhoneNumber());
					userOverlay.addOverlay(overlayitem);
				}
				userOverlay.refresh();
				mapView.postInvalidate();
				try {
					Thread.sleep(time);
				} catch (InterruptedException e1) {
					// Do nothing
				}
			}
			Log.i("RefreshThread", "Turning off");
		}
	}
}