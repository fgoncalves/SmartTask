package engine.views;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 
 * This class represents a view of a user and their tasks in the database.
 * 
 * @author Grupo 1
 */
public class UserTaskView implements Serializable {
	private static final long serialVersionUID = 1L;

	private String username;
	private Integer taskID;
	private Timestamp completionDate;
	private boolean completed;

	/**
	 * Initializes a view with the given arguments.
	 * 
	 * @param username
	 *            The user's name in the database.
	 * @param taskID
	 *            The id of the task.
	 * @param completionDate
	 *            Completion date of the task. Null if it has not yet been
	 *            completed.
	 * @param completed
	 *            True if the task has been completed. False otherwise. This
	 *            parameter is different from the task's done parameter because
	 *            this applies to each user.
	 */
	public UserTaskView(String username, Integer taskID,
			Timestamp completionDate, boolean completed) {
		super();
		this.username = username;
		this.taskID = taskID;
		this.completionDate = completionDate;
		this.completed = completed;
	}

	/**
	 * Initializes an empty view.
	 */
	public UserTaskView() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getTaskID() {
		return taskID;
	}

	public void setTaskID(Integer taskID) {
		this.taskID = taskID;
	}

	public Timestamp getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(Timestamp completionDate) {
		this.completionDate = completionDate;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UserTaskView) {
			UserTaskView casted = (UserTaskView) obj;
			return username.equals(casted.username)
					&& taskID.equals(casted.taskID);
		}
		return false;
	}
}
