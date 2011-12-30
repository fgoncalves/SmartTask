package smartask.client.ui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import smartask.client.core.Session;
import smartask.client.db.DatabaseSQLiteQueryEngine;
import smartask.client.friendsynchronization.FriendSynchThread;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import engine.services.RequestNotificationService;
import engine.views.UserView;

/**
 * This activity displays details about a certain user. The user whose details
 * should be displayed is passed in the intent that started this activity.
 * 
 * @author Grupo 1
 */
public class UserDetailsActivity extends Activity {
	private Button synch;
	private Button send;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_details);

		String username = getIntent().getStringExtra("username");
		final UserView uv = DatabaseSQLiteQueryEngine.selectUser(username);

		TextView usernameTV = (TextView) findViewById(R.id.userDetailsUsernameTextView);
		TextView phoneTV = (TextView) findViewById(R.id.userDetailsPhoneTextView);
		TextView emailTV = (TextView) findViewById(R.id.userDetailsEmailTextView);

		usernameTV.setText(usernameTV.getText().toString() + " "
				+ uv.getUsername());
		phoneTV.setText(phoneTV.getText().toString() + " "
				+ uv.getPhoneNumber());
		emailTV.setText(emailTV.getText().toString() + " " + uv.getEmail());

		synch = (Button) findViewById(R.id.userDetailsSynchButton);
		send = (Button) findViewById(R.id.userDetailsSendNotificationRequestButton);

		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new RequestNotificationService(uv.getUsername(),
						(String) Session.get("username")).execute();
				return;
			}
		});

		synch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int localPort = Integer.parseInt(DatabaseSQLiteQueryEngine
						.selectUser((String) Session.get("username"))
						.getPhoneNumber()) + 2221;
				int addr = Integer.parseInt(DatabaseSQLiteQueryEngine
						.selectUser(uv.getUsername()).getPhoneNumber()) + 2220;
				try {
					Socket clientSocket = new Socket(InetAddress
							.getByName("10.0.2.2"), addr, InetAddress
							.getByName("10.0.2.15"), localPort);
					new FriendSynchThread(clientSocket).start();
				} catch (UnknownHostException e) {
					Log.w("UserProximityAlertReceiver",
							"Unable to create socket. Stack trace:");
					e.printStackTrace();
				} catch (IOException e) {
					Log.w("UserProximityAlertReceiver",
							"Unable to create socket. Stack trace:");
					e.printStackTrace();
				}
			}
		});
	}
}
