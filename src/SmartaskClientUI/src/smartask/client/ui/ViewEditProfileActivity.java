package smartask.client.ui;

import smartask.client.core.Session;
import smartask.client.db.DatabaseSQLiteQueryEngine;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import engine.views.UserView;

/**
 * This activity displays the user's current profile in an editable interface.
 * Thus, profile can be altered and saved.
 * 
 * @author Grupo 1
 */
public class ViewEditProfileActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_profile);
		String username = (String) Session.get("username");

		UserView uv = DatabaseSQLiteQueryEngine.selectUser(username);
		if (uv == null)
			finish();

		((TextView) findViewById(R.id.usernameLabelEditProfile)).setText(uv
				.getUsername());
		((EditText) findViewById(R.id.emailTextBox)).setText(uv.getEmail());
		((EditText) findViewById(R.id.phoneNumberTextBox)).setText(uv
				.getPhoneNumber());
		((TextView) findViewById(R.id.awardedCreditsLabel)).setText(""
				+ uv.getAcumulatedCredits());

		Button b = (Button) findViewById(R.id.saveProfileButton);
		b.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String u = ((TextView) findViewById(R.id.usernameLabelEditProfile))
						.getText().toString();
				String e = ((EditText) findViewById(R.id.emailTextBox))
						.getText().toString();
				String p = ((EditText) findViewById(R.id.phoneNumberTextBox))
						.getText().toString();
				float c = Float
						.parseFloat(((TextView) findViewById(R.id.awardedCreditsLabel))
								.getText().toString());
				DatabaseSQLiteQueryEngine.alterUser(u, e, p, c);
				Toast.makeText(getApplicationContext(), "Profile saved",
						Toast.LENGTH_SHORT).show();
			}
		});
	}
}
