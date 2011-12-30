package smartask.client.ui.maps;

import java.util.ArrayList;
import java.util.List;

import smartask.client.ui.R;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import engine.views.CoordinateView;

/**
 * This activity displays a clickable map view. Each click on a local will store
 * that local's coordinate in an ArrayList. This ArrayList is returned to the
 * activity CreateTaskActivity.
 * 
 * @see smartask.client.ui.CreateTaskActivity
 * @author Grupo 1
 */
public class SelectPlaceMap extends MapActivity {
	private final static int SAVE_OPTION_ID = 0;

	private ArrayList<CoordinateView> points = new ArrayList<CoordinateView>();
	private PinOverlay pinOverlay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);
		MapView mapView = (MapView) findViewById(R.id.map_view);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(false);
		Toast.makeText(getBaseContext(),
				R.string.create_task_map_overlay_prompt, Toast.LENGTH_LONG)
				.show();
		MapController mc = mapView.getController();
		Location l = ((LocationManager) getSystemService(LOCATION_SERVICE))
				.getLastKnownLocation("gps");
		GeoPoint geoPoint = new GeoPoint(38707708, -9136510);
		if (l != null) {
			geoPoint = new GeoPoint((int) (l.getLatitude() * 1E6), (int) (l
					.getLongitude() * 1E6));
		}
		mc.animateTo(geoPoint);
		mc.setZoom(17);
		// ---Overlay
		MapOverlay mapOverlay = new MapOverlay();
		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);
		pinOverlay = new PinOverlay(getResources().getDrawable(
				R.drawable.androidmarker));
		listOfOverlays.add(pinOverlay);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, SAVE_OPTION_ID, 0, R.string.create_task_menu_button_label);
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case SAVE_OPTION_ID:
			if (points.size() == 0) {
				Toast.makeText(this, "Please select at least one location.",
						Toast.LENGTH_SHORT).show();
				return true;
			}
			Intent intent = new Intent();
			intent.putExtra("geopoints", points);
			setResult(RESULT_OK, intent);
			finish();
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/**
	 * This is an overlay that adds items to the PinOverlay.
	 * 
	 * @see smartask.client.ui.maps.SelectPlaceMap.PinOverlay
	 * @author Grupo 1
	 */
	private class MapOverlay extends Overlay {

		@Override
		public boolean onTap(GeoPoint p, MapView mapView) {
			points
					.add(new CoordinateView(p.getLatitudeE6(), p
							.getLongitudeE6()));
			pinOverlay.addOverlay(new OverlayItem(p, "", ""));
			return true;
		}
	}

	/**
	 * This class implements an overlay of items. Each time a click event is
	 * sent to the MapOverlay, an item is added to this overlay.
	 * 
	 * @author Grupo 1
	 */
	private class PinOverlay extends ItemizedOverlay<OverlayItem> {
		private ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

		/**
		 * @param defaultMarker
		 *            The icon image to use on the overlay.
		 */
		public PinOverlay(Drawable defaultMarker) {
			super(boundCenterBottom(defaultMarker));
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
			populate();
		}

		@Override
		protected boolean onTap(int index) {
			if (index < items.size()) {
				items.remove(index);
				return true;
			}
			return false;
		}
	}
}