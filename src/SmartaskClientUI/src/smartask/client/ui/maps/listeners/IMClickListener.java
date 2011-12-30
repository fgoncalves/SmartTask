package smartask.client.ui.maps.listeners;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import smartask.client.core.Session;
import smartask.client.db.DatabaseSQLiteQueryEngine;
import smartask.client.ui.im.IMActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;

/**
 * This class implements a click listener that initializes the IMActivity.
 * 
 * @author Grupo 1
 */
public class IMClickListener implements OnClickListener {
	private int addr;
	private Context context;

	private static HashMap<Integer, Socket> connectedClients = new HashMap<Integer, Socket>();

	public IMClickListener(String addr, Context c) {
		super();
		this.addr = Integer.parseInt(addr) + 1110;
		this.context = c;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		try {
			int localPort = Integer.parseInt(DatabaseSQLiteQueryEngine
					.selectUser((String) Session.get("username"))
					.getPhoneNumber()) + 1111;
			Socket clientSocket = new Socket(InetAddress.getByName("10.0.2.2"),
					addr, InetAddress.getByName("10.0.2.15"), localPort);
			Integer port = clientSocket.getPort();

			connectedClients.put(port, clientSocket);
			Intent i = new Intent(context, IMActivity.class);
			i.putExtra("from", "IMClickListener");
			i.putExtra("port", port);
			context.startActivity(i);
		} catch (UnknownHostException e) {
			Log.e("IMClickListener", "Unable to create socket. Stack trace:");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("IMClickListener", "Unable to create socket. Stack trace:");
			e.printStackTrace();
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
			Log.d("IMClickListener", "Could find socket to close.");
	}
}
