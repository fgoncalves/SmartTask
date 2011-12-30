package engine.services;

import java.sql.SQLException;

import engine.core.Tables;

/**
 * 
 * This service is used to create a monitoring request.
 * 
 * @author Grupo 1
 */
public class RequestNotificationService extends Service<Boolean> {
	private String usernameFrom;
	private String usernameTo;

	/**
	 * Initialize the service with the given arguments.
	 * 
	 * @param usernameFrom
	 *            The user name to whom the request should be sent.
	 * @param usernameTo
	 *            The user name which is requesting the monitoring.
	 */
	public RequestNotificationService(String usernameFrom, String usernameTo) {
		super();
		this.usernameFrom = usernameFrom;
		this.usernameTo = usernameTo;
	}

	@Override
	protected Boolean action() throws SQLException {
		String insertQuery = "INSERT INTO " + Tables.notifications
				+ " VALUES('" + usernameFrom + "','" + usernameTo + "',FALSE);";
		transaction.insert(insertQuery);
		return true;
	}
}
