package smartask.client.ui.services;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import smartask.client.core.Session;
import smartask.client.db.DatabaseSQLiteQueryEngine;
import smartask.client.ui.R;
import smartask.client.ui.im.IMActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import engine.views.UserView;

/**
 * This service initializes a socket and constantly listens on it for incoming
 * connections. When a connection arrives, the service creates a thread to
 * handle the it.
 * 
 * @author Grupo 1
 */
public class IMService extends Service implements Runnable {
	private ServerSocket serverSocket;
	private boolean exit = false;

	private static HashMap<Integer, Socket> connectedClients = new HashMap<Integer, Socket>();

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		String username = (String) Session.get("username");

		if (username == null) {
			Log.e("IMService", "Unable to get user from db. Stopping service");
			stopSelf();
			return;
		}

		UserView uv = DatabaseSQLiteQueryEngine.selectUser(username);
		try {
			serverSocket = new ServerSocket(Integer.parseInt(uv
					.getPhoneNumber()) + 1110);
		} catch (NumberFormatException e) {
			Log.e("IMService",
					"Unable parse server port. Stopping service. Stack trace:");
			e.printStackTrace();
			stopSelf();
			return;
		} catch (IOException e) {
			Log
					.e("IMService",
							"Unable to initialize server socket. Stopping service. Stack trace:");
			e.printStackTrace();
			stopSelf();
			return;
		}

		new Thread(this).start();
	}

	@Override
	public void onDestroy() {
		exit = true;
		stopSelf();
	}

	@Override
	public void run() {
		while (!exit) {
			try {
				Socket clientSocket = serverSocket.accept();

				Integer port = clientSocket.getPort();

				connectedClients.put(port, clientSocket);

				NotificationManager nm = (NotificationManager) this
						.getSystemService(Context.NOTIFICATION_SERVICE);
				Intent i = new Intent(this, IMActivity.class);

				i.putExtra("port", port);
				i.putExtra("from", "IMService");

				PendingIntent pendingIntent = PendingIntent.getActivity(this,
						0, i, Intent.FLAG_ACTIVITY_NEW_TASK);
				Notification n = new Notification(
						R.drawable.user3dnotification, "Incoming message",
						System.currentTimeMillis());
				n.setLatestEventInfo(this, "Instant messaging",
						"Incoming message from " + port, pendingIntent);
				nm.notify(R.string.im_messaging, n);
			} catch (IOException e) {
				Log.e("IMService", "Error getting client data. Stack trace:");
				e.printStackTrace();
			}
		}
	}

	public static Socket getClientSocket(Integer port) {
		return connectedClients.get(port);
	}

	public static void closeClientSocket(Integer port) {
		if (connectedClients.containsKey(port)) {
			try {
				connectedClients.get(port).close();
			} catch (IOException e) {
				// Don't do nothing
			}
			connectedClients.remove(port);
		} else
			Log.d("IMService", "Could find socket to close.");
	}
}
