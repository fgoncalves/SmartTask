package smartask.client.ui;

import java.util.Collection;

import smartask.client.db.DatabaseSQLiteQueryEngine;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import engine.views.UserView;

/**
 * This activity merely presents a list of all the users in the local database.
 * If a list item is clicked, then details about the user are displayed. When
 * viewing user details, one can synchronize with him or her and send a
 * monitoring request.
 * 
 * @author Grupo 1
 */
public class ListContactsActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Collection<UserView> cl = DatabaseSQLiteQueryEngine.getContacts();

		UserView[] users = new UserView[cl.size()];
		cl.toArray(users);

		setListAdapter(new ArrayAdapter<UserView>(this, R.layout.list_item,
				users));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				UserView uv = (UserView) ((ListAdapter) arg0.getAdapter())
						.getItem(arg2);
				Intent i = new Intent(getApplicationContext(),
						UserDetailsActivity.class);
				i.putExtra("username", uv.getUsername());
				startActivity(i);
			}
		});

		Toast.makeText(this, "Click on an user to view his details",
				Toast.LENGTH_LONG).show();
	}
}
