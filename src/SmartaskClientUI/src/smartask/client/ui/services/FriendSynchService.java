package smartask.client.ui.services;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import smartask.client.core.Session;
import smartask.client.db.DatabaseSQLiteQueryEngine;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import engine.views.TaskView;
import engine.views.UserView;

/**
 * This service is used to synchronize the database of two users.
 * 
 * @author Grupo 1
 */
public class FriendSynchService extends Service implements Runnable {
	private final static String TAG = "FriendSynchService";

	private ServerSocket serverSocket;
	private boolean exit = false;

	private static HashMap<Integer, Socket> connectedFriends = new HashMap<Integer, Socket>();

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		String username = (String) Session.get("username");

		if (username == null) {
			Log.e(TAG,
					"Unable to get user from db. Stopping FriendSynchService");
			stopSelf();
			return;
		}

		UserView uv = DatabaseSQLiteQueryEngine.selectUser(username);
		try {
			serverSocket = new ServerSocket(Integer.parseInt(uv
					.getPhoneNumber()) + 2220);
		} catch (NumberFormatException e) {
			Log
					.e(TAG,
							"Unable parse server port. Stopping FriendSynchService. Stack trace:");
			e.printStackTrace();
			stopSelf();
			return;
		} catch (IOException e) {
			Log
					.e(TAG,
							"Unable to initialize server socket. Stopping FriendSynchService. Stack trace:");
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

				connectedFriends.put(port, clientSocket);

				ObjectInputStream ois = new ObjectInputStream(clientSocket
						.getInputStream());
				try {
					// Synch profile
					UserView otherProfile = (UserView) ois.readObject();
					DatabaseSQLiteQueryEngine.alterUser(otherProfile);
					// Synch tasks
					Collection<?> tasks = (Collection<?>) ois.readObject();
					DatabaseSQLiteQueryEngine.updateUserCompletedTasks(tasks);

					String myUsername = (String) Session.get("username");
					UserView uv = DatabaseSQLiteQueryEngine
							.selectUser(myUsername);
					ArrayList<TaskView> myTasks = new ArrayList<TaskView>(
							DatabaseSQLiteQueryEngine.selectAllCompletedTasks());
					ObjectOutputStream oos = new ObjectOutputStream(
							clientSocket.getOutputStream());
					oos.writeObject(uv);
					oos.flush();
					oos.writeObject(myTasks);
					oos.flush();
				} catch (ClassNotFoundException e) {
					Log.e(TAG, "Couldn't create userview.");
					e.printStackTrace();
				}
			} catch (IOException e) {
				Log.e(TAG, "Error getting client data. Stack trace:");
				e.printStackTrace();
			}
		}
	}

	public static Socket getClientSocket(Integer port) {
		return connectedFriends.get(port);
	}

	public static void closeClientSocket(Integer port) {
		if (connectedFriends.containsKey(port)) {
			try {
				connectedFriends.get(port).close();
			} catch (IOException e) {
				// Don't do nothing
			}
			connectedFriends.remove(port);
		} else
			Log.d(TAG, "Could find socket to close.");
	}
}
