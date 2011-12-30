package smartask.client.ui.im;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import android.util.Log;

/**
 * This class provides an interaction between IMActivity and the socket output
 * stream. The IMActivity stores messages in a buffer kept by this class. These
 * messages will then be sent in a FIFO policy to the other end of the socket.
 * 
 * @author Grupo 1
 */
public class IMOutputProvider extends Thread {

	private OutputStream os;
	private boolean exit;

	private ArrayList<String> messageBuffer;

	/**
	 * Initialize this output provider.
	 * 
	 * @param socket
	 *            The connection socket.
	 * @throws IOException
	 *             If the socket output stream could not be thrown.
	 */
	public IMOutputProvider(Socket socket) throws IOException {
		super();
		this.os = socket.getOutputStream();
		messageBuffer = new ArrayList<String>();
		exit = false;
	}

	/**
	 * Terminate this thread
	 */
	public void exit() {
		exit = true;
	}

	/**
	 * Store a message in the buffer so it can be sent through the socket's
	 * output stream.
	 * 
	 * @param message
	 *            The message to send.
	 */
	public void putMessage(String message) {
		synchronized (messageBuffer) {
			messageBuffer.add(message);
			messageBuffer.notify();
		}
	}

	/**
	 * @return The next message to be sent.
	 */
	private String getMessage() {
		synchronized (messageBuffer) {
			while (messageBuffer.size() == 0)
				try {
					messageBuffer.wait();
				} catch (InterruptedException ie) {
					// Don't do nothing.
				}
			return messageBuffer.remove(0);
		}
	}

	@Override
	public void run() {
		while (!exit) {
			try {
				String message = getMessage();
				os.write(message.getBytes());
				os.flush();
			} catch (IOException e) {
				Log.d("IMOutputProvider", "Connection closed. Exiting...");
				exit();
			}
		}
		try {
			os.close();
		} catch (IOException e) {
			// Unable to close socket. Don't do nothing.
		}
	}
}
