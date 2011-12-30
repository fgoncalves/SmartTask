package smartask.client.ui.services;

import java.io.IOException;
import java.util.List;

import smartask.client.core.ApplicationConfiguration;
import smartask.client.core.Session;
import smartask.client.observer.IEventListener;
import smartask.client.observer.IEventPublisher;
import smartask.client.ui.sms.SmsSender;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import engine.services.UpdateUserLocalService;
import engine.views.CoordinateView;

/**
 *This service sends the user's current position to the main database, where
 * other users can get it.
 * 
 * @author Grupo 1
 */
public class SendGPSPositionService extends Service implements IEventListener {
	private class ServiceLocationListener implements LocationListener {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {
			tearDownForGPSAutoRefreshing();
		}

		@Override
		public void onLocationChanged(Location location) {
			String username = (String) Session.get("username");
			if (username != null) {
				int latitude = (int) (location.getLatitude() * 1E6);
				int longitude = (int) (location.getLongitude() * 1E6);
				Log.i(SendGPSPositionService.class + "",
						"Updating user position...");
				Boolean res = new UpdateUserLocalService(username,
						new CoordinateView(latitude, longitude)).execute();
				if (res == null) {
					try {
						SmsSender.sendObjectBySms("5560",
								"UpdateUserLocalService;string:" + username
										+ ";int:" + latitude + ";int:"
										+ longitude);
					} catch (IOException e) {
						Log.e("SendGPSPositionService",
								"Could not send position through sms.");
						e.printStackTrace();
					}
				}
				Log.i(SendGPSPositionService.class + "", "done!");
			}
		}
	}

	private LocationManager locationManager;
	private ServiceLocationListener listener;
	private static final int MIN_DISTANCE = 0; // meters

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onCreate();
		listener = new ServiceLocationListener();
		ApplicationConfiguration.getInstance().registerEventListener(this);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (!((Boolean) Session.get("position.notification.disabled")))
			setupForGPSAutoRefreshing();
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		tearDownForGPSAutoRefreshing();
		super.onDestroy();
	}

	private void setupForGPSAutoRefreshing() {
		List<String> providers = this.locationManager.getProviders(true);
		String provider = providers.get(0);

		long minTimeBetweenUp = (Integer) Session
				.get("position.notification.rate") * 1000;

		locationManager.requestLocationUpdates(provider, minTimeBetweenUp,
				MIN_DISTANCE, listener);
	}

	private void tearDownForGPSAutoRefreshing() {
		locationManager.removeUpdates(listener);
	}

	@Override
	public void handleEvent(IEventPublisher source) {
		if (source instanceof ApplicationConfiguration) {
			tearDownForGPSAutoRefreshing();
			if (!((Boolean) Session.get("position.notification.disabled")))
				setupForGPSAutoRefreshing();
		}
	}
}
