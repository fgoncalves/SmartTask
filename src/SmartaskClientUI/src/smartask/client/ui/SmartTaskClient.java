package smartask.client.ui;

import smartask.client.core.Session;
import smartask.client.db.DatabaseSQLiteQueryEngine;
import smartask.client.ui.services.FriendSynchService;
import smartask.client.ui.services.IMService;
import smartask.client.ui.services.PositionRequestService;
import smartask.client.ui.services.SendGPSPositionService;
import smartask.client.ui.services.SynchronizationService;
import smartask.client.ui.services.UserRequestMonitoringService;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.maps.GeoPoint;

import engine.core.MySQLConfiguration;
import engine.services.UpdateUserLocalService;
import engine.views.CoordinateView;

/**
 * This class is the main activity of the mobile client. It initializes all
 * configurations and presents the user with a login screen. It also starts the
 * services.
 * 
 * @author Grupo 1
 */
public class SmartTaskClient extends Activity {
	private static final int EXIT_OPTION_ID = 0;
	private EditText UsernameTextbox;
	private Button LoginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		MySQLConfiguration.init(
				getString(R.string.mysql_database_services_log_file),
				getString(R.string.mysql_database_url),
				getString(R.string.mysql_database_user),
				getString(R.string.mysql_database_password));
		initConfiguration();
		DatabaseSQLiteQueryEngine.init(this);
		UsernameTextbox = (EditText) findViewById(R.id.UsernameTextbox);
		LoginButton = (Button) findViewById(R.id.LoginButton);

		LoginButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (canLogin(UsernameTextbox.getText().toString())) {
					Session.put("username", UsernameTextbox.getText()
							.toString());
					Intent i = new Intent("smartask.ui.menu.ROOT");
					i
							.putExtra("username", UsernameTextbox.getText()
									.toString());
					startService(new Intent(getApplicationContext(),
							IMService.class));
					startActivity(i);
				}
			}
		});

		UsernameTextbox.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					if (canLogin(UsernameTextbox.getText().toString())) {
						Location location = ((LocationManager) getSystemService(Context.LOCATION_SERVICE))
								.getLastKnownLocation("gps");
						GeoPoint geoPoint = new GeoPoint(38707708, -9136510);
						if (location != null) {
							geoPoint = new GeoPoint((int) (location
									.getLatitude() * 1E6), (int) (location
									.getLongitude() * 1E6));
						}
						int latitude = geoPoint.getLatitudeE6();
						int longitude = geoPoint.getLongitudeE6();
						Log.i(SendGPSPositionService.class + "",
								"Updating user position...");
						new UpdateUserLocalService(UsernameTextbox.getText()
								.toString(), new CoordinateView(latitude,
								longitude)).execute();
						Log.i(SendGPSPositionService.class + "", "done!");
						Session.put("username", UsernameTextbox.getText()
								.toString());
						Intent i = new Intent("smartask.ui.menu.ROOT");
						i.putExtra("username", UsernameTextbox.getText()
								.toString());
						startService(new Intent(getApplicationContext(),
								IMService.class));
						startService(new Intent(getApplicationContext(),
								FriendSynchService.class));
						startActivity(i);
					}
					return true;
				}
				return false;
			}
		});

		startService(new Intent(this, SynchronizationService.class));
		startService(new Intent(this, SendGPSPositionService.class));
		startService(new Intent(this, UserRequestMonitoringService.class));
		startService(new Intent(this, PositionRequestService.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, EXIT_OPTION_ID, 0, R.string.exit_button_label);
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case EXIT_OPTION_ID:
			finish();
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Method to check if a user can login onto the emulator.
	 * 
	 * @param username
	 *            The user's name.
	 * @return True if the user can login in the emulator. False otherwise.
	 */
	private boolean canLogin(String username) {
		if (username != null) {
			return DatabaseSQLiteQueryEngine.login(username);
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		DatabaseSQLiteQueryEngine.close();
		stopService(new Intent(this, SynchronizationService.class));
		stopService(new Intent(this, SendGPSPositionService.class));
		stopService(new Intent(this, UserRequestMonitoringService.class));
		stopService(new Intent(this, PositionRequestService.class));
		stopService(new Intent(this, IMService.class));
		stopService(new Intent(this, FriendSynchService.class));
		super.onDestroy();
	}

	/**
	 * Initialize session
	 */
	private void initConfiguration() {
		Session.put("synchronization.rate", 5000);
		Session.put("synchronization.disabled", false);
		Session.put("position.acquisition.rate", 5000);
		Session.put("position.acquisition.disabled", false);
		Session.put("position.notification.rate", 5000);
		Session.put("position.notification.disabled", false);
		Session.put("database.server.notification.rate", 5000);
		Session.put("database.server.notification.disabled", false);
		Session.put("task.radius", 5f);
		Session.put("user.radius", 5f);
		Session.put("number.tasks", 50);
	}
}
