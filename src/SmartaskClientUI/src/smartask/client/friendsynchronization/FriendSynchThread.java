package smartask.client.friendsynchronization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

import smartask.client.core.Session;
import smartask.client.db.DatabaseSQLiteQueryEngine;
import android.util.Log;
import engine.views.TaskView;
import engine.views.UserView;

/**
 * This thread synchronizes completed tasks with other users in the system.
 * 
 * @author Grupo 1
 */
public class FriendSynchThread extends Thread {
	private final static String TAG = "FriendSynchThread";

	private Socket clientSocket;

	/**
	 * Initialize this thread with the given socket.
	 * 
	 * @param clientSocket
	 *            The socket connected to a user, which this emulator will
	 *            synchronize its tasks.
	 */
	public FriendSynchThread(Socket clientSocket) {
		super();
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {
		String myUsername = (String) Session.get("username");
		UserView uv = DatabaseSQLiteQueryEngine.selectUser(myUsername);
		ArrayList<TaskView> tasks = new ArrayList<TaskView>(
				DatabaseSQLiteQueryEngine.selectAllCompletedTasks());
		try {
			ObjectOutputStream oos = new ObjectOutputStream(clientSocket
					.getOutputStream());
			oos.writeObject(uv);
			oos.flush();
			oos.writeObject(tasks);
			oos.flush();

			ObjectInputStream ois = new ObjectInputStream(clientSocket
					.getInputStream());
			// Synch profile
			UserView otherProfile = (UserView) ois.readObject();
			DatabaseSQLiteQueryEngine.alterUser(otherProfile);
			// Synch tasks
			Collection<?> otherTasks = (Collection<?>) ois.readObject();
			DatabaseSQLiteQueryEngine.updateUserCompletedTasks(otherTasks);

			try {
				clientSocket.close();
			} catch (IOException e) {
				// Don't do nothing.
			}
		} catch (IOException e) {
			Log.e(TAG, "Unable to synchronize with friend:" + e.getMessage()
					+ " Stack trace:");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "Unable to synchronize with friend:" + e.getMessage()
					+ " Stack trace:");
			e.printStackTrace();
		}
	}
}
