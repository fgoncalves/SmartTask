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
 * This service selects all uncompleted tasks assigned to a certain user. It
 * retrieves the X most recent tasks, where X is a numbered supplied in the
 * class's constructor. This is used to provide a way of knowing how many tasks
 * an emulator supports.
 * 
 * @author Grupo 1
 */
public class SelectAllUncompletedTasksService extends
		Service<Collection<TaskView>> {
	private int limit;
	private String username;

	/**
	 * Initialize the service with the given arguments.
	 * 
	 * @param limit
	 *            The number of tasks to retrieve from the database.
	 */
	public SelectAllUncompletedTasksService(String user, int limit) {
		super();
		this.limit = limit;
		username = user;
	}

	@Override
	protected Collection<TaskView> action() throws SQLException {
		String selectTasks = "SELECT * FROM "
				+ Tables.tasks
				+ ","
				+ Tables.tasks_locals
				+ ","
				+ Tables.tasks_users
				+ " WHERE id = "
				+ Tables.tasks_locals
				+ ".task_id AND id = "
				+ Tables.tasks_users
				+ ".task_id AND completed = FALSE AND done = FALSE AND username = '"
				+ username + "' ORDER BY id DESC LIMIT " + limit + ";";

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
