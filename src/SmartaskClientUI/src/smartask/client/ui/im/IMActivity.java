package smartask.client.ui.im;

import java.io.IOException;
import java.net.Socket;

import smartask.client.core.Session;
import smartask.client.observer.IEventListener;
import smartask.client.observer.IEventPublisher;
import smartask.client.ui.R;
import smartask.client.ui.maps.listeners.IMClickListener;
import smartask.client.ui.services.IMService;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * This activity displays an interface used by the Instant Messaging protocol.
 * 
 * @author Grupo 1
 */
public class IMActivity extends Activity implements IEventListener {
	public final static int MESSAGE_SIZE = 128;

	private IMOutputProvider imOutProv;
	private IMInputProvider imInProv;
	private TextView chattingWith;
	private EditText chatWindow;
	private Button sendButton;
	private EditText sendBox;
	private String user;

	private String eventThatTriggeredThisActivity;

	private Integer endPort;

	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_layout);

		endPort = getIntent().getExtras().getInt("port");

		user = (String) Session.get("username");

		if (endPort == null) {
			Log.e("IMActivity",
					"Unable to get username from intent. Finishing activity.");
			finish();
			return;
		}

		eventThatTriggeredThisActivity = getIntent().getStringExtra("from");

		if (eventThatTriggeredThisActivity == null) {
			Log
					.e("IMActivity",
							"Unable to determine from where intent was trigger. Finishing activity.");
			finish();
			return;
		}

		Socket socket = null;
		if (eventThatTriggeredThisActivity.equals("IMService")) {
			socket = IMService.getClientSocket(endPort);
			NotificationManager nm = (NotificationManager) this
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.cancel(R.string.im_messaging);
			if (socket == null) {
				Log
						.e("IMActivity",
								"Unable to get socket from IMService. Finishing activity.");
				finish();
				return;
			}
		} else {
			if (eventThatTriggeredThisActivity.equals("IMClickListener")) {
				socket = IMClickListener.getClientSocket(endPort);
				if (socket == null) {
					Log
							.e("IMActivity",
									"Unable to get socket from IMClickListener. Finishing activity.");
					finish();
					return;
				}
			}
		}

		chattingWith = (TextView) findViewById(R.id.imChatWith);
		chatWindow = (EditText) findViewById(R.id.imChatWindow);
		sendButton = (Button) findViewById(R.id.imSendButton);
		sendBox = (EditText) findViewById(R.id.imSendBox);

		chattingWith.setText(chattingWith.getText() + ("" + endPort));

		// Initialize peer
		try {
			imOutProv = new IMOutputProvider(socket);
			imInProv = new IMInputProvider(socket);

			imInProv.registerEventListener(this);

			imOutProv.start();
			imInProv.start();
		} catch (IOException e) {
			Log.e("IMActivity", "Unable to initialize peer. Stack trace:");
			e.printStackTrace();
			finish();
			return;
		}

		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMessage();
			}
		});
		sendButton.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					sendMessage();
					return true;
				}
				return false;
			}
		});
		sendBox.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					sendMessage();
					return true;
				}
				return false;
			}
		});

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				printMessage(msg.getData().getString("message"));
			}
		};
	}

	@Override
	public void handleEvent(IEventPublisher source) {
		if (source instanceof IMInputProvider) {
			for (String message : imInProv.getMessages()) {
				Bundle b = new Bundle();
				b.putString("message", message);
				Message msg = handler.obtainMessage();
				msg.setData(b);
				handler.sendMessage(msg);
			}
		}
	}

	@Override
	protected void onDestroy() {
		imOutProv.exit();
		imInProv.exit();
		if (eventThatTriggeredThisActivity.equals("IMService"))
			IMService.closeClientSocket(endPort);
		else
			IMClickListener.closeClientSocket(endPort);
		super.onDestroy();
	}

	/**
	 * Print a message in the chat window.
	 * 
	 * @param message
	 *            The message to be displayed.
	 */
	private void printMessage(String message) {
		synchronized (chatWindow) {
			chatWindow.setText(chatWindow.getText() + message);
			sendBox.setText("");
			chatWindow.invalidate();
		}
	}

	/**
	 * Send the message in the message text box to the other user.
	 */
	public void sendMessage() {
		String text = sendBox.getText().toString();
		if (text != null && !text.equals("")) {
			imOutProv.putMessage(user + ": " + text);
			printMessage("me: " + text);
		}
	}
}
