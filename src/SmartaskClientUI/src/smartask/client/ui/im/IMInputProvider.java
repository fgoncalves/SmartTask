package smartask.client.ui.im;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;

import smartask.client.observer.IEventListener;
import smartask.client.observer.IEventPublisher;
import android.util.Log;

/**
 * This class provides a channel between a socket input stream and the
 * IMActivity. Whenever a message is received in the socket, an event is fired
 * to notify the IMActivity.
 * 
 * @author Grupo 1
 */
public class IMInputProvider extends Thread implements IEventPublisher {
	private InputStream is;
	private ArrayList<IEventListener> listeners;
	private boolean exit;

	private ArrayList<String> messageBuffer;

	/**
	 * Initialize this provide.
	 * 
	 * @param socket
	 *            The socket of the IM connection.
	 * @throws IOException
	 *             If the socket input stream could not be retrieved.
	 */
	public IMInputProvider(Socket socket) throws IOException {
		super();
		this.is = socket.getInputStream();
		listeners = new ArrayList<IEventListener>();
		messageBuffer = new ArrayList<String>();
		exit = false;
	}

	@Override
	public void notifyListeners() {
		for (IEventListener ie : listeners) {
			ie.handleEvent(this);
		}
	}

	@Override
	public void registerEventListener(IEventListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeEventListener(IEventListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Terminate this thread.
	 */
	public void exit() {
		exit = true;
	}

	/**
	 * Adds a message to the receiving buffer. The IMActivity can retrieve this
	 * message by calling getMessages.
	 * 
	 * @param message
	 *            The to store in the buffer.
	 */
	private void putMessage(String message) {
		synchronized (messageBuffer) {
			messageBuffer.add(message);
		}
		notifyListeners();
	}

	/**
	 * Obtain the messages stored in the buffer until this moment.
	 * 
	 * @return A collection of strings that represent the receiving messages.
	 */
	public ArrayList<String> getMessages() {
		ArrayList<String> messages;
		synchronized (messageBuffer) {
			messages = new ArrayList<String>();
			for (String string : messageBuffer) {
				messages.add(string);
			}
			messageBuffer.clear();
			return messages;
		}
	}

	@Override
	public void run() {
		while (!exit) {
			byte[] received = new byte[IMActivity.MESSAGE_SIZE];
			try {
				int actuallyRead = is.read(received);
				if (actuallyRead == -1) {
					continue;
				}
				putMessage(new String(received, 0, actuallyRead));
			} catch (IOException e) {
				Log.d("IMInputProvider", "Connection closed. Exiting...");
				exit();
			}
		}
		try {
			is.close();
		} catch (IOException e) {
			// Unable to close socket. Don't do nothing.
		}
	}
}
