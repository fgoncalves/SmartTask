package engine.core;

/**
 * 
 * This inteface contains all table names used in queries executed by services.
 * 
 * @author Grupo 1
 */
public interface Tables {
	/**
	 * Table which contains information about each task.
	 */
	public final static String tasks = "`Task`";
	/**
	 * Table which contains information about each user.
	 */
	public final static String users = "`User`";
	/**
	 * Table which relates locals to tasks.
	 */
	public final static String tasks_locals = "`Task_Local`";
	/**
	 * Table which relates tasks to users.
	 */
	public final static String tasks_users = "`User_Task`";

	/**
	 * Table which contains information about each user's position.
	 */
	public final static String local_users = "`User_Local`";

	/**
	 * Table which contains information about each conflicted.
	 */
	public final static String conflicts = "`Conflicts`";

	/**
	 * Table which contains information about each notification state.
	 */
	public final static String notifications = "`Allowed_Notifications`";
}
