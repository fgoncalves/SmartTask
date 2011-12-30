package smartask.client.ui.maps.listeners;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;

/**
 * This class implements a click listener that initializes the emulator's call
 * activity.
 * 
 * @author Grupo 1
 */
public class CallClickListener implements OnClickListener {

	private String addr;
	private Context context;

	public CallClickListener(String addr, Context c) {
		super();
		this.addr = addr;
		this.context = c;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:" + addr));
		context.startActivity(callIntent);
	}

}
