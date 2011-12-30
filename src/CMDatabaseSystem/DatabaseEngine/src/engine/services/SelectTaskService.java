package engine.services;

import java.sql.ResultSet;
import java.sql.SQLException;

import engine.core.Tables;
import engine.views.CoordinateView;
import engine.views.TaskView;
import engine.views.UserTaskView;

/**
 * 
 * This service is used to obtain a certain task stored in the database. Null is
 * returned if the task does not exist.
 * 
 * @author Grupo 1
 */
public class SelectTaskService extends Service<TaskView> {
	private Integer id;

	/**
	 * Initialize the service with the given arguments.
	 * @param id The id of the task to retrieve.
	 */
	public SelectTaskService(Integer id) {
		super();
		this.id = id;
	}

	@Override
	protected TaskView action() throws SQLException {
		String selectTask = "SELECT * FROM " + Tables.tasks
				+ " LEFT OUTER JOIN " + Tables.tasks_locals + " ON "
				+ Tables.tasks + ".id = " + Tables.tasks_locals
				+ ".task_id LEFT OUTER JOIN " + Tables.tasks_users + " ON "
				+ Tables.tasks_users + ".task_id = " + Tables.tasks
				+ ".id WHERE " + Tables.tasks + ".id = " + id + ";";

		TaskView result = new TaskView();
		ResultSet rs = transaction.select(selectTask);
		if (rs.next()) {
			result.setCredits(rs.getFloat("credits"));
			result.setDescription(rs.getString("description"));
			result.setDone(rs.getBoolean("done"));
			result.setId(rs.getInt("id"));
			result.setName(rs.getString("name"));
			result.setPriority(rs.getInt("priority"));
			result.setNumberOfUsersNeededForCompletion(rs
					.getInt("numberOfUsersNeeded"));

			if (rs.getString("username") != null)
				result.getUsers().add(
						new UserTaskView(rs.getString("username"), rs
								.getInt("id"), rs
								.getTimestamp("completionDate"), rs
								.getBoolean("completed")));

			int latitude = rs.getInt("latitude");
			if (!rs.wasNull())
				result.getLocals().add(
						new CoordinateView(latitude, rs.getInt("longitude")));

			while (rs.next()) {
				if (rs.getString("username") != null)
					result.getUsers().add(
							new UserTaskView(rs.getString("username"), rs
									.getInt("id"), rs
									.getTimestamp("completionDate"), rs
									.getBoolean("completed")));

				latitude = rs.getInt("latitude");
				if (!rs.wasNull())
					result.getLocals()
							.add(
									new CoordinateView(latitude, rs
											.getInt("longitude")));

			}
			return result;
		}

		return null;
	}
}
