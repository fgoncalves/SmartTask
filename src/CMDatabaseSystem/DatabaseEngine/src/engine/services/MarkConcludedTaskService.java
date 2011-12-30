package engine.services;

import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * 
 * This service marks a certain task as completed by a certain user. It calls
 * upon a store procedure which resolves every kind of conflict and reassigns
 * credits.
 * 
 * @author Grupo 1
 */
public class MarkConcludedTaskService extends Service<Boolean> {
	private Integer task_id;
	private Timestamp completionDate;
	private String username;

	/**
	 * Initialize the service with the given arguments.
	 * 
	 * @param taskId
	 *            The task's id to mark as completed.
	 * @param completionDate
	 *            The user's local timestamp taken at the time which he or she
	 *            completed the task.
	 * @param username
	 *            The user's name in the database.
	 */
	public MarkConcludedTaskService(Integer taskId, Timestamp completionDate,
			String username) {
		super();
		task_id = taskId;
		this.completionDate = completionDate;
		this.username = username;
	}

	@Override
	protected Boolean action() throws SQLException {
		transaction.call("complete_task(" + task_id + ",'" + username + "','"
				+ completionDate + "')");
		return true;
	}
}