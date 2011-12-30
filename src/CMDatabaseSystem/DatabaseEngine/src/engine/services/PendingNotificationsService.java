package engine.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import engine.core.Tables;
import engine.views.NotificationView;

/**
 * 
 * This service obtains every monitoring request to a given user. Each request
 * in a pending state, which means it has not yet been answered.
 * 
 * @author Grupo 1
 */
public class PendingNotificationsService extends
		Service<Collection<NotificationView>> {
	private String usernameFrom;

	/**
	 * Initialize the service with the given arguments.
	 * 
	 * @param usernameFrom
	 *            The name of the user to whom requests were made.
	 */
	public PendingNotificationsService(String usernameFrom) {
		super();
		this.usernameFrom = usernameFrom;
	}

	@Override
	protected Collection<NotificationView> action() throws SQLException {
		String query = "SELECT * FROM " + Tables.notifications
				+ " WHERE usernameFrom = '" + usernameFrom
				+ "' AND approved = FALSE;";
		ResultSet rs = transaction.select(query);
		ArrayList<NotificationView> results = new ArrayList<NotificationView>();
		while (rs.next()) {
			results.add(new NotificationView(rs.getString("usernameFrom"), rs
					.getString("usernameTo"), rs.getBoolean("approved")));
		}
		return results;
	}
}
