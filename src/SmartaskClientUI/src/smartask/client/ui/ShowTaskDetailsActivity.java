package smartask.client.ui;

import smartask.client.core.Session;
import smartask.client.db.DatabaseSQLiteQueryEngine;
import smartask.client.ui.services.TaskAlertNotification;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import engine.views.TaskView;

/**
 * This activity displays details about a certain task. The task to display
 * details about is passed on the intent that started this activity.
 * 
 * @author Grupo 1
 */
public class ShowTaskDetailsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task);

		final TaskView tv = (TaskView) getIntent().getExtras().get("taskview");

		TextView tName = (TextView) findViewById(R.id.detailTaskNameLabel);
		TextView tDescription = (TextView) findViewById(R.id.detailTaskDescriptionLabel);
		TextView tPriority = (TextView) findViewById(R.id.detailTaskPriorityLabel);
		TextView tCredits = (TextView) findViewById(R.id.detailTaskCreditsLabel);

		tName.setText(tv.getName());
		tDescription.setText(tv.getDescription());
		switch (tv.getPriority()) {
		case 0:
			tPriority.setText("Normal");
			break;
		case 1:
			tPriority.setText("Urgent");
			break;
		case 2:
			tPriority.setText("Critical");
			break;
		default:
			tPriority.setText("Unknown");
		}

		tCredits.setText(""
				+ (tv.getCredits() / tv.getNumberOfUsersNeededForCompletion()));

		((Button) findViewById(R.id.markDoneTaskButton))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						tv.setDone(true);
						DatabaseSQLiteQueryEngine.concludeTask(tv,
								(String) Session.get("username"));
						TaskAlertNotification.getInstance().reconfigure(
								getApplicationContext());
						Toast.makeText(getApplicationContext(),
								"Task completed", Toast.LENGTH_SHORT).show();
						finish();
					}
				});
	}
}
