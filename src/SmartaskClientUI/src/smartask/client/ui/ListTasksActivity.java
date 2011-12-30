package smartask.client.ui;

import java.util.Collection;

import smartask.client.core.Session;
import smartask.client.db.DatabaseSQLiteQueryEngine;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import engine.views.TaskView;

/**
 * This activity lists all tasks assigned to the emulator's user. If a list item
 * is clicked, then details about the task are displayed. When viewing details,
 * one can mark that task as completed.
 * 
 * @author Grupo 1
 */
public class ListTasksActivity extends ListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Collection<TaskView> cl = DatabaseSQLiteQueryEngine
				.selectAllUncompletedTasks((String) Session.get("username"));
		TaskView[] tasks = new TaskView[cl.size()];
		cl.toArray(tasks);

		setListAdapter(new ArrayAdapter<TaskView>(this, R.layout.list_item,
				tasks));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				TaskView tv = (TaskView) ((ListAdapter) arg0.getAdapter())
						.getItem(arg2);
				Intent i = new Intent("smartask.client.ui.DETAILTASK");
				i.putExtra("taskview", tv);
				startActivity(i);
			}
		});
	}
}
