package smartask.client.ui.menu;

import smartask.client.ui.R;
import smartask.client.ui.menu.command.Command;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * This class implements a base class for list like menus. A menu is a list of
 * commands. Commands contain actions, which in turn will be fired when the menu
 * item is clicked. The idea is to have other menus or activities registered for
 * those actions.
 * 
 * @see smartask.client.ui.menu.command.Command
 * @author Grupo 1
 */
public class Menu extends ListActivity {
	protected Command[] commands;

	public Menu(Command[] commands) {
		super();
		this.commands = commands;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<Command>(this, R.layout.list_item,
				commands));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListAdapter la = (ListAdapter) parent.getAdapter();
				Command c = (Command) la.getItem(position);
				startActivity(new Intent(c.getAction()));
			}
		});
	}
}
