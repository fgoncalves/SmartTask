package smartask.client.ui;

import smartask.client.core.ApplicationConfiguration;
import smartask.client.core.Session;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity implements the configuration menu.<br/>
 * 
 * Each time the configuration is saved an event is triggered and all event
 * listeners will be notified. This means that services requiring values defined
 * here, will be restart in order to read the new configuration.
 * 
 * @author Grupo 1
 */
public class ConfigurationActivity extends Activity {
	private TextView configDBServerConfigurationTextBox;
	private TextView configPosAcquisitionTextBox;
	private TextView configPosNotificationSendTextBox;
	private TextView configDBNotificationTextBox;
	private TextView configTaskRadiusTextBox;
	private TextView configUserRadiusTextBox;
	private TextView configNumberOfTasksTextBox;
	private CheckBox configDBServerConfigurationCheckBox;
	private CheckBox configPosAcquisitionCheckBox;
	private CheckBox configPosNotificationSendCheckBox;
	private CheckBox configDBNotificationCheckBox;
	private Button saveChanges;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_configuration);

		configDBServerConfigurationTextBox = (TextView) findViewById(R.id.configDBServerConfigurationTextBox);
		configDBServerConfigurationTextBox.setText(""
				+ ((Integer) Session.get("synchronization.rate") / 1000));
		configPosAcquisitionTextBox = (TextView) findViewById(R.id.configPosAcquisitionTextBox);
		configPosAcquisitionTextBox.setText(""
				+ ((Integer) Session.get("position.acquisition.rate") / 1000));
		configPosNotificationSendTextBox = (TextView) findViewById(R.id.configPosNotificationSendTextBox);
		configPosNotificationSendTextBox.setText(""
				+ ((Integer) Session.get("position.notification.rate") / 1000));
		configDBNotificationTextBox = (TextView) findViewById(R.id.configDBNotificationTextBox);
		configDBNotificationTextBox
				.setText(""
						+ ((Integer) Session
								.get("database.server.notification.rate") / 1000));
		configTaskRadiusTextBox = (TextView) findViewById(R.id.configTaskRadiusTextBox);
		configTaskRadiusTextBox
				.setText("" + (Float) Session.get("task.radius"));
		configUserRadiusTextBox = (TextView) findViewById(R.id.configUserRadiusTextBox);
		configUserRadiusTextBox
				.setText("" + (Float) Session.get("user.radius"));
		configNumberOfTasksTextBox = (TextView) findViewById(R.id.configNumberOfTasksTextBox);
		configNumberOfTasksTextBox.setText(""
				+ (Integer) Session.get("number.tasks"));

		configDBServerConfigurationCheckBox = (CheckBox) findViewById(R.id.configDBServerConfigurationCheckBox);
		configPosAcquisitionCheckBox = (CheckBox) findViewById(R.id.configPosAcquisitionCheckBox);
		configPosNotificationSendCheckBox = (CheckBox) findViewById(R.id.configPosNotificationSendCheckBox);
		configDBNotificationCheckBox = (CheckBox) findViewById(R.id.configDBNotificationCheckBox);

		configDBServerConfigurationCheckBox.setChecked((Boolean) Session
				.get("synchronization.disabled"));
		configPosAcquisitionCheckBox.setChecked((Boolean) Session
				.get("position.acquisition.disabled"));
		configPosNotificationSendCheckBox.setChecked((Boolean) Session
				.get("position.notification.disabled"));
		configDBNotificationCheckBox.setChecked((Boolean) Session
				.get("database.server.notification.disabled"));

		saveChanges = (Button) findViewById(R.id.saveConfigurationButton);
		saveChanges.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (saveState()) {
					//Notify event listeners
					ApplicationConfiguration.getInstance().notifyListeners();
					Toast.makeText(getApplicationContext(),
							"Configuration saved", Toast.LENGTH_SHORT).show();
					finish();
				}
			}
		});
	}

	/**
	 * Save the new values in the Session of the emulator.
	 * 
	 * @return True if the values were correctly saved. False otherwise
	 */
	private boolean saveState() {
		try {
			Session.put("synchronization.rate", Integer
					.parseInt(configDBServerConfigurationTextBox.getText()
							.toString()) * 1000);
			Session.put("synchronization.disabled",
					configDBServerConfigurationCheckBox.isChecked());
			Session.put("position.acquisition.rate",
					Integer.parseInt(configPosAcquisitionTextBox.getText()
							.toString()) * 1000);
			Session.put("position.acquisition.disabled",
					configPosAcquisitionCheckBox.isChecked());
			Session.put("position.notification.rate", Integer
					.parseInt(configPosNotificationSendTextBox.getText()
							.toString()) * 1000);
			Session.put("position.notification.disabled",
					configPosNotificationSendCheckBox.isChecked());
			Session.put("database.server.notification.rate",
					Integer.parseInt(configDBNotificationTextBox.getText()
							.toString()) * 1000);
			Session.put("database.server.notification.disabled",
					configDBNotificationCheckBox.isChecked());
			Session.put("task.radius", Float.parseFloat(configTaskRadiusTextBox
					.getText().toString()));
			Session.put("user.radius", Float.parseFloat(configUserRadiusTextBox
					.getText().toString()));
			Session.put("number.tasks", Integer
					.parseInt(configNumberOfTasksTextBox.getText().toString()));
		} catch (NumberFormatException e) {
			Toast.makeText(this, "Please make shure to fill every text box",
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
}
