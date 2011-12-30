package engine.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import engine.core.Tables;
import engine.views.TaskConflictView;

/**
 * 
 * This service is used only by the monitoring application. It retrieves every
 * conflict in the database.
 * 
 * @author Grupo 1
 */
public class SelectAllConflicts extends Service<Collection<TaskConflictView>> {

	@Override
	protected Collection<TaskConflictView> action() throws SQLException {
		String selectConflicts = "SELECT * FROM " + Tables.conflicts;
		ResultSet rs = transaction.select(selectConflicts);

		ArrayList<TaskConflictView> results = new ArrayList<TaskConflictView>();
		while (rs.next()) {
			TaskConflictView cv = new TaskConflictView(rs.getInt("number"), rs
					.getString("usernameToRemoveCredits"), rs
					.getString("usernameToAddCredits"), rs
					.getFloat("creditsBeforeRemove"), rs
					.getFloat("creditsAfterRemove"), rs
					.getFloat("creditsBeforeAddition"), rs
					.getFloat("creditsAfterAddition"), rs
					.getTimestamp("firstCompletionOn"), rs
					.getTimestamp("secondCompletionOn"));
			results.add(cv);
		}
		return results;
	}
}
