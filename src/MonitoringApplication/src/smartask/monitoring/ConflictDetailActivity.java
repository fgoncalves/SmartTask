package smartask.monitoring;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import engine.views.TaskConflictView;

/**
 * @author Grupo 1
 * 
 *         This activity is used to show the details of a given conflict. A
 *         TaskConflictView should be passed in the intent that fired this
 *         activity in order to show the conflict's details.
 */
public class ConflictDetailActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detailed_conflict);

		TextView tv = (TextView) findViewById(R.id.detailsAboutConflictLabel);
		TaskConflictView c = (TaskConflictView) getIntent().getExtras().get(
				"conflict");

		String details = "Username that received credits before conflict: %s\n"
				+ "\tCredits before conflict: %f\n"
				+ "\tCredits after conflict: %f\n"
				+ "\tTask was completed in %s\n\n"
				+ "Username that received credits after conflict: %s\n"
				+ "\tCredits before conflict: %f\n"
				+ "\tCredits after conflict: %f\n"
				+ "\tTask was completed in %s\n\n";

		tv.setText(String.format(details, c.getUsernameToRemoveCredits(), c
				.getCreditsBeforeRemove(), c.getCreditsAfterRemove(), c
				.getFirstCompletionOn(), c.getUsernameToAddCredits(), c
				.getCreditsBeforeAddition(), c.getCreditsAfterAddition(), c
				.getSecondCompletionOn()));
	}
}
