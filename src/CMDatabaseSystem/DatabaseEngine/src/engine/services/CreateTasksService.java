package engine.services;

import java.sql.SQLException;
import java.util.Collection;

import engine.views.TaskView;

/**
 * 
 * This service is used as a convenience. It iterates through a list of tasks'
 * views and calls CreateTaskService.execute to create each task.
 * 
 * @author Grupo 1
 */
public class CreateTasksService extends Service<Boolean> {
	private Collection<TaskView> tasks;

	/**
	 * Initialize this service with the given arguments.
	 * @param tasks The tasks to be created.
	 */
	public CreateTasksService(Collection<TaskView> tasks) {
		super();
		this.tasks = tasks;
	}

	@Override
	protected Boolean action() throws SQLException {
		for (TaskView tv : tasks) {
			new CreateTaskService(tv.getName(), tv.getDescription(), tv
					.getPriority(), tv.getNumberOfUsersNeededForCompletion(),
					tv.getLocals()).execute();
		}
		return true;
	}
}
