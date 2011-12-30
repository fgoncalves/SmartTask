package engine.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import engine.core.Tables;
import engine.views.CoordinateView;
import engine.views.TaskView;
import engine.views.UserTaskView;

/**
 * 
 * This service obtains all tasks created and stored in the database.
 * 
 * @author Grupo 1
 */
public class ListAllTasksService extends Service<Collection<TaskView>> {

	@Override
	public Collection<TaskView> action() throws SQLException {
		String selectTasks = "SELECT * FROM " + Tables.tasks
				+ " LEFT OUTER JOIN " + Tables.tasks_locals + " ON "
				+ Tables.tasks + ".id = " + Tables.tasks_locals
				+ ".task_id LEFT OUTER JOIN " + Tables.tasks_users + " ON "
				+ Tables.tasks_users + ".task_id = " + Tables.tasks + ".id;";

		HashMap<Integer, TaskView> result = new HashMap<Integer, TaskView>();
		ResultSet rs = transaction.select(selectTasks);
		while (rs.next()) {
			Integer task_id = rs.getInt("id");
			if (result.containsKey(task_id)) {
				TaskView tv = result.get(task_id);
				if (rs.getString("username") != null)
					tv.getUsers().add(
							new UserTaskView(rs.getString("username"), rs
									.getInt("id"), rs
									.getTimestamp("completionDate"), rs
									.getBoolean("completed")));
				if (rs.getString("latitude") != null)
					tv.getLocals().add(
							new CoordinateView(rs.getInt("latitude"), rs
									.getInt("longitude")));
				result.put(tv.getId(), tv);
			} else {
				TaskView tv = new TaskView(rs.getInt("id"), rs
						.getString("name"), rs.getString("description"), rs
						.getInt("priority"), rs.getInt("numberOfUsersNeeded"),
						rs.getBoolean("done"), rs.getFloat("credits"),
						new HashSet<CoordinateView>(),
						new HashSet<UserTaskView>());
				if (rs.getString("username") != null)
					tv.getUsers().add(
							new UserTaskView(rs.getString("username"), rs
									.getInt("id"), rs
									.getTimestamp("completionDate"), rs
									.getBoolean("completed")));
				if (rs.getString("latitude") != null)
					tv.getLocals().add(
							new CoordinateView(rs.getInt("latitude"), rs
									.getInt("longitude")));
				result.put(tv.getId(), tv);
			}
		}
		return result.values();
	}
}
