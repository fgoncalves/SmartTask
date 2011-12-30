package smartask.client.ui;

import java.util.ArrayList;

import smartask.client.db.DatabaseSQLiteQueryEngine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import engine.views.CoordinateView;

/**
 * This activity defines a form to create new tasks. After filling every field
 * one must select locations in the map. Thus, a map activity will be started
 * and when it is finished a collection of geopoints is retrieve in order to
 * complete the task's creation.
 * 
 * @author Grupo 1
 */
public class CreateTaskActivity extends Activity {
	private final static int SELECT_LOCAITONS_CODE = 0;

	Button createTaskButton;
	EditText taskNameEditTextBox;
	EditText descriptionEditTextBox;
	Spinner taskPrioritySpinner;
	EditText usersTextBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_task);
		Spinner spinner = (Spinner) findViewById(R.id.taskPriorityComboBox);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.task_priority,
				android.R.layout.simple_spinner_item);
		adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		createTaskButton = (Button) findViewById(R.id.createTaskButton);
		taskNameEditTextBox = (EditText) findViewById(R.id.taskNameTextbox);
		descriptionEditTextBox = (EditText) findViewById(R.id.taskDescriptionTextbox);
		taskPrioritySpinner = (Spinner) findViewById(R.id.taskPriorityComboBox);
		usersTextBox = (EditText) findViewById(R.id.nUsersTextbox);

		createTaskButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (taskNameEditTextBox.getText().toString().equals("")
						|| descriptionEditTextBox.getText().toString().equals(
								"")
						|| usersTextBox.getText().toString().equals("")
						|| Integer.parseInt(usersTextBox.getText().toString()) == 0)
					Toast.makeText(getApplicationContext(),
							"Please make sure to fill all fields.",
							Toast.LENGTH_SHORT).show();
				else
					startActivityForResult(new Intent(
							"smartask.client.ui.SELECTMAPVIEW"),
							SELECT_LOCAITONS_CODE);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SELECT_LOCAITONS_CODE) {
			if (resultCode == RESULT_OK) {
				@SuppressWarnings("unchecked")
				ArrayList<CoordinateView> points = (ArrayList<CoordinateView>) data
						.getExtras().get("geopoints");

				System.out.println(taskPrioritySpinner
						.getSelectedItemPosition());

				DatabaseSQLiteQueryEngine.createPseudoTask(taskNameEditTextBox
						.getText().toString(), descriptionEditTextBox.getText()
						.toString(), taskPrioritySpinner
						.getSelectedItemPosition() + 1, Integer
						.parseInt(usersTextBox.getText().toString()), points);

				Toast.makeText(this, "Task successfuly created",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}
}
