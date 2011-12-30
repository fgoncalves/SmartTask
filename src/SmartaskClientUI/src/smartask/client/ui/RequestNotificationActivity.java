package smartask.client.ui;

import smartask.client.core.Session;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import engine.services.AcceptNotificationService;
import engine.services.RejectNotificationService;

/**
 * This activity serves as a confirmation dialog box. It is started when the
 * user clicks on a monitoring request notification that appears on the top bar
 * of the emulator.<br/>
 * 
 * We did this with an activity instead of an alert dialog, because android
 * couldn't display such dialog when a notification was clicked.
 * 
 * @author Grupo 1
 */
public class RequestNotificationActivity extends Activity {

	private NotificationManager notificationManager;

	/**
	 * This method builds and displays the actually alert dialog box.
	 * 
	 * @param userRequesting
	 *            The name of the user requesting the monitoring.
	 */
	private void buildAndDisplayMessageBox(final String userRequesting) {
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle("Notification Request");
		dialog.setMessage("User " + userRequesting
				+ " requests to monitor your position.");
		dialog.setButton("Allow", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				new AcceptNotificationService((String) Session.get("username"),
						userRequesting).execute();
				notificationManager.cancel(R.string.request_notification);
				finish();
			}
		});
		dialog.setButton2("Disallow", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				new RejectNotificationService((String) Session.get("username"),
						userRequesting).execute();
				notificationManager.cancel(R.string.request_notification);
				finish();
			}
		});
		dialog.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_item);
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		String usernameTo = getIntent().getExtras().getString("usernameTo");
		buildAndDisplayMessageBox(usernameTo);
	}
}
