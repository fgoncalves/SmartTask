package smartask.monitoring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import engine.services.ListAllTasksService;
import engine.services.SelectUserLocalsService;
import engine.views.CoordinateView;
import engine.views.TaskView;

/**
 * This activity shows all users and tasks in a map. Each overlay is updated
 * every second.
 * 
 * @author Grupo 1
 */
public class TrackingMap extends MapActivity {
	private static final long SLEEP_TIME = 1000; // 1s

	private TaskOverlay taskOverlay;
	private UserOverlay userOverlay;

	private RefreshThread refreshThread;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);
		MapView mapView = (MapView) findViewById(R.id.map_view);
		mapView.setBuiltInZoomControls(true);

		MapController mc = mapView.getController();
		// TagusPark micro-coordenates
		GeoPoint geoPoint = new GeoPoint(38737880, -9302290);
		mc.animateTo(geoPoint);
		mc.setZoom(17);
		// ---Overlay
		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();

		// show tasks
		taskOverlay = new TaskOverlay(getResources().getDrawable(
				R.drawable.androidmarker), this);
		listOfOverlays.add(taskOverlay);

		userOverlay = new UserOverlay(getResources().getDrawable(
				R.drawable.user), this);
		listOfOverlays.add(userOverlay);

		Collection<TaskView> tasks = new ListAllTasksService().execute();
		for (TaskView tv : tasks) {
			for (CoordinateView cv : tv.getLocals()) {
				GeoPoint point = new GeoPoint(cv.getLatitude(), cv
						.getLongitude());
				OverlayItem overlayitem = new OverlayItem(point, "Task name:\n"
						+ tv.getName(), "Description:\n" + tv.getDescription());
				taskOverlay.addOverlay(overlayitem);
			}
		}
		Collection<Entry<String, CoordinateView>> users = new SelectUserLocalsService()
				.execute();
		for (Entry<String, CoordinateView> u : users) {
			GeoPoint point = new GeoPoint(u.getValue().getLatitude(), u
					.getValue().getLongitude());
			OverlayItem overlayitem = new OverlayItem(point, u.getKey(), "");
			userOverlay.addOverlay(overlayitem);
		}

		refreshThread = new RefreshThread();
		new Thread(refreshThread).start();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onDestroy() {
		refreshThread.exit();
		super.onDestroy();
	}

	/**
	 * This class implements an item overlay which renders every task in the
	 * mapview.
	 * 
	 * @author Grupo 1
	 */
	private class TaskOverlay extends ItemizedOverlay<OverlayItem> {
		private ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		public Context mContext;

		/**
		 * @param defaultMarker
		 *            The default icon to use in every item of the overlay.
		 * @param context
		 *            The application context.
		 */
		public TaskOverlay(Drawable defaultMarker, Context context) {
			super(boundCenterBottom(defaultMarker));
			mContext = context;
			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return items.get(i);
		}

		/**
		 * Cleans all methods in the overlay.
		 */
		public void clearItems() {
			items.clear();
		}

		@Override
		public int size() {
			return items.size();
		}

		/**
		 * Adds an overlay item to this overlay.
		 * 
		 * @param overlay
		 *            The overlay item to add to this overlay.
		 */
		public void addOverlay(OverlayItem overlay) {
			items.add(overlay);
			populate();
		}

		@Override
		protected boolean onTap(int index) {
			OverlayItem item;
			if (items.get(index) == null)
				System.out.println("overlay null");
			else {
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
	 * This class implements an item overlay which renders every user in the
	 * mapview.
	 * 
	 * @author Grupo 1
	 */
	private class UserOverlay extends ItemizedOverlay<OverlayItem> {
		private ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		public Context mContext;

		/**
		 * @param defaultMarker
		 *            The default icon to use in every item of this overlay.
		 * @param context
		 *            The application context.
		 */
		public UserOverlay(Drawable defaultMarker, Context context) {
			super(boundCenterBottom(defaultMarker));
			mContext = context;
			populate();
		}

		/**
		 * Clear all items in this overlay.
		 */
		public void clearItems() {
			items.clear();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return items.get(i);
		}

		@Override
		public int size() {
			return items.size();
		}

		/**
		 * Add an item to this overlay.
		 * 
		 * @param overlay
		 *            The overlay item to add to this overlay.
		 */
		public void addOverlay(OverlayItem overlay) {
			items.add(overlay);
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
	 * This thread is used to refresh each overlay.
	 * 
	 * @author Grupo 1
	 */
	private class RefreshThread extends Thread {
		private boolean exit = false;

		/**
		 * Method used to terminate this thread.
		 */
		public void exit() {
			exit = true;
		}

		@Override
		public void run() {
			while (!exit) {
				try {
					Thread.sleep(SLEEP_TIME);
					taskOverlay.clearItems();
					userOverlay.clearItems();
					Collection<TaskView> tasks = new ListAllTasksService()
							.execute();
					for (TaskView tv : tasks) {
						for (CoordinateView cv : tv.getLocals()) {
							GeoPoint point = new GeoPoint(cv.getLatitude(), cv
									.getLongitude());
							OverlayItem overlayitem = new OverlayItem(point,
									"Task name:" + tv.getName(), "Description:"
											+ tv.getDescription());
							taskOverlay.addOverlay(overlayitem);
						}
					}
					Collection<Entry<String, CoordinateView>> users = new SelectUserLocalsService()
							.execute();
					userOverlay.clearItems();
					for (Entry<String, CoordinateView> u : users) {
						GeoPoint point = new GeoPoint(u.getValue()
								.getLatitude(), u.getValue().getLongitude());
						OverlayItem overlayitem = new OverlayItem(point, u
								.getKey(), "");
						userOverlay.addOverlay(overlayitem);
					}
				} catch (InterruptedException e) {
					// don't do anything
				}
			}
		}
	}
}