package smartask.monitoring;

import smartask.monitoring.kmlparser.CoordinateSender;
import smartask.monitoring.kmlparser.CoordinateTimedSender;
import smartask.monitoring.kmlparser.KMLDirectoryList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import engine.core.MySQLConfiguration;

/**
 * This is the main activity of the monitoring application.
 * @author Grupo 1 
 */
public class MainActivity extends Activity {
	private final static String DIRECTORY_NAME = "data/data/smartask.monitoring";

	private Button conflicts;
	private Button map;
	private KMLDirectoryList kmlFiles;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MySQLConfiguration.init(
				getString(R.string.mysql_database_services_log_file),
				getString(R.string.mysql_database_url),
				getString(R.string.mysql_database_user),
				getString(R.string.mysql_database_password));

		setContentView(R.layout.main);

		conflicts = (Button) findViewById(R.id.conflictsButton);
		conflicts.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						ConflictListActivity.class));
			}
		});

		map = (Button) findViewById(R.id.mapsButton);
		map.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						TrackingMap.class));
			}
		});

		kmlFiles = new KMLDirectoryList(DIRECTORY_NAME);

		((Button) findViewById(R.id.sendKMLCoordinatesButton))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						CheckBox timed = (CheckBox) findViewById(R.id.timedKMLCoordinatesCheckbox);
						if (timed.isChecked()) {
							for (String file : kmlFiles.getFiles()) {
								new CoordinateTimedSender(DIRECTORY_NAME + "/"
										+ file).start();
							}
						} else {
							for (String file : kmlFiles.getFiles()) {
								new CoordinateSender(DIRECTORY_NAME + "/"
										+ file).start();
							}
						}
					}
				});

		Spinner spinner = (Spinner) findViewById(R.id.sendKMLCoordinatesSpinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.velocity, android.R.layout.simple_spinner_item);
		adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				CoordinateSender.setSleepTime(arg2 + 1);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				CoordinateSender.setSleepTime(1);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}