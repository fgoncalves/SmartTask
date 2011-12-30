package engine.services;

import java.sql.SQLException;

import engine.core.Tables;

/**
 * 
 * This services should be used to reject requests made to a given user.
 * 
 * @author Grupo 1
 */
public class RejectNotificationService extends Service<Boolean> {
	private String usernameFrom;
	private String usernameTo;

	/**
	 * Initialize the service with the given arguments.
	 * 
	 * @param usernameFrom
	 *            The user name of the user to whom the request was made.
	 * @param usernameTo
	 *            The user name of the user that request the monitoring.
	 */
	public RejectNotificationService(String usernameFrom, String usernameTo) {
		super();
		this.usernameFrom = usernameFrom;
		this.usernameTo = usernameTo;
	}

	@Override
	protected Boolean action() throws SQLException {
		String deleteQuery = "DELETE FROM " + Tables.notifications
				+ " WHERE usernameFrom = '" + usernameFrom
				+ "' AND usernameTo = '" + usernameTo + "';";
		transaction.delete(deleteQuery);
		return true;
	}
}
