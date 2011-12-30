package smartask.client.ui.maps.listeners;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;

/**
 * This class initializes a click listener that starts the emulator's sms
 * activity.
 * 
 * @author Grupo 1
 */
public class SMSClickListener implements OnClickListener {
	private String addr;
	private Context context;

	public SMSClickListener(String addr, Context c) {
		super();
		this.addr = addr;
		this.context = c;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		sendIntent.setData(Uri.parse("sms:" + addr));
		context.startActivity(sendIntent);
	}

}
