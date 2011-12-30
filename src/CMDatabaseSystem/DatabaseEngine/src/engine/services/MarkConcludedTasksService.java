package engine.services;

import java.sql.SQLException;
import java.util.Collection;

import engine.views.TaskView;
import engine.views.UserTaskView;

/**
 * 
 * This service is used as a convenience. It calls upon
 * MarkConcludedTaskService.execute and marks every task as concluded for each
 * user.
 * 
 * @author Grupo 1
 */
public class MarkConcludedTasksService extends Service<Boolean> {
	private Collection<TaskView> tasks;

	/**
	 * Initialize the service with the given arguments. 
	 * @param tasks The Collection of tasks to mark as completed.
	 */
	public MarkConcludedTasksService(Collection<TaskView> tasks) {
		super();
		this.tasks = tasks;
	}

	@Override
	protected Boolean action() throws SQLException {
		for (TaskView tv : tasks) {
			for (UserTaskView utv : tv.getUsers()) {
				new MarkConcludedTaskService(tv.getId(), utv
						.getCompletionDate(), utv.getUsername()).execute();
			}
		}
		return true;
	}

}
