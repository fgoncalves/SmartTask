package smartask.monitoring;

import java.util.Collection;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import engine.services.SelectAllConflicts;
import engine.views.TaskConflictView;

/**
 * @author Grupo 1
 * 
 *         This activity lists all conflicts stored in the main database. When
 *         an item is clicked, an intent is fired and ConflictDetailActivity is
 *         started. The intent carries an extra with the TaskConflictView of the
 *         clicked item.
 */
public class ConflictListActivity extends ListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Collection<TaskConflictView> c = new SelectAllConflicts().execute();
		TaskConflictView[] conflicts = new TaskConflictView[c.size()];
		c.toArray(conflicts);

		setListAdapter(new ArrayAdapter<TaskConflictView>(this,
				R.layout.conflict_list, conflicts));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ListAdapter la = (ListAdapter) arg0.getAdapter();
				TaskConflictView conflict = (TaskConflictView) la.getItem(arg2);
				Intent i = new Intent(getApplicationContext(),
						ConflictDetailActivity.class);
				i.putExtra("conflict", conflict);
				startActivity(i);
			}
		});
	}
}
