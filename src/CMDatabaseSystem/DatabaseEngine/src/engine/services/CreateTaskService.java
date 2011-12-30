package engine.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import engine.core.Tables;
import engine.views.CoordinateView;
import engine.views.TaskView;
import engine.views.UserTaskView;

/**
 * 
 * This class is an implementation of a service that creates a task in the
 * database.<br/>
 * 
 * The action method of this class returns the created task or null if an error
 * ocurred.
 * 
 * @author Grupo 1
 */
public class CreateTaskService extends Service<TaskView> {
	private String name;
	private String description;
	private int priority;
	private int numberOfUsersNeededForCompletion;
	private HashSet<CoordinateView> locals;

	private final static int creditFactor = 5;

	/**
	 * Initialize the service with the given attributes
	 * 
	 * @param name
	 *            The task's name.
	 * @param description
	 *            The task's description.
	 * @param priority
	 *            The task's priority.
	 * @param numberOfUsersNeededForCompletion
	 *            The number of users needed to complete the task.
	 * @param locals
	 *            The locals where this task should be done.
	 */
	public CreateTaskService(String name, String description, int priority,
			int numberOfUsersNeededForCompletion, HashSet<CoordinateView> locals) {
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.numberOfUsersNeededForCompletion = numberOfUsersNeededForCompletion;
		this.locals = locals;
	}

	@Override
	public TaskView action() throws SQLException {
		if (locals == null || locals.isEmpty())
			throw new SQLException(
					"You must provide at least one local to create this task: "
							+ name);

		String insertTaskQuery = "REPLACE INTO "
				+ Tables.tasks
				+ "(name,description,priority,done,credits,numberOfUsersNeeded)"
				+ " VALUES('" + name + "','" + description + "'," + priority
				+ ",FALSE," + priority * creditFactor + ","
				+ numberOfUsersNeededForCompletion + ");";

		int t_id = transaction.insertAndReturnLastInsertedID(insertTaskQuery);

		String insertLocalTaskQuery = "INSERT INTO " + Tables.tasks_locals
				+ " VALUES(";
		for (CoordinateView c : locals) {
			transaction.insert(insertLocalTaskQuery + c.getLatitude() + ","
					+ c.getLongitude() + "," + t_id + ");");
		}

		String selectTask = "SELECT * FROM " + Tables.tasks
				+ " LEFT OUTER JOIN " + Tables.tasks_locals + " ON "
				+ Tables.tasks + ".id = " + Tables.tasks_locals
				+ ".task_id LEFT OUTER JOIN " + Tables.tasks_users + " ON "
				+ Tables.tasks_users + ".task_id = " + Tables.tasks
				+ ".id WHERE " + Tables.tasks + ".id = " + t_id + ";";

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
						new UserTaskView(rs.getString("username"), t_id, rs
								.getTimestamp("completionDate"), false));

			int latitude = rs.getInt("latitude");
			if (!rs.wasNull())
				result.getLocals().add(
						new CoordinateView(latitude, rs.getInt("longitude")));

			while (rs.next()) {
				if (rs.getString("username") != null)
					result.getUsers().add(
							new UserTaskView(rs.getString("username"), t_id, rs
									.getTimestamp("completionDate"), false));

				latitude = rs.getInt("latitude");
				if (!rs.wasNull())
					result.getLocals()
							.add(
									new CoordinateView(latitude, rs
											.getInt("longitude")));

			}

			transaction.call("assign_tasks(" + t_id + ")");
			return result;
		}

		return null;
	}
}
