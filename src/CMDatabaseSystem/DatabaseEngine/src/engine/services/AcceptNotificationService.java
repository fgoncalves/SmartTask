package engine.services;

import java.sql.SQLException;

import engine.core.Tables;

/**
 * 
 * This class implements a service which goal is to accept a pending
 * notification request.<br/>
 * 
 * An update is made to the allowed notifications table. This will provide users
 * the authorization required to access other users' positions.<br/>
 * 
 * The action method of this class return true if the service was executed
 * without any erros and Null otherwise.
 * 
 * @author Grupo 1
 */
public class AcceptNotificationService extends Service<Boolean> {
	private String usernameFrom;
	private String usernameTo;

	/**
	 * Initialize the service with the given parameters.
	 * 
	 * @param usernameFrom
	 *            User name of the user that is accepting the request.
	 * @param usernameTo
	 *            Use name that requested the notification.
	 */
	public AcceptNotificationService(String usernameFrom, String usernameTo) {
		super();
		this.usernameFrom = usernameFrom;
		this.usernameTo = usernameTo;
	}

	@Override
	protected Boolean action() throws SQLException {
		String updateQuery = "UPDATE " + Tables.notifications
				+ " SET approved = TRUE WHERE usernameFrom = '" + usernameFrom
				+ "' AND usernameTo = '" + usernameTo + "';";
		transaction.delete(updateQuery);
		return true;
	}
}
