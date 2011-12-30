package engine.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import engine.core.Tables;
import engine.views.CoordinateView;

/**
 * 
 * This service obtains the positions of other users monitored by a given user.
 * Note that positions are returned only if the user has authorization and has
 * tasks in common with those users.
 * 
 * @author Grupo 1
 */
public class ObtainFriendsPositionsService extends
		Service<HashMap<String, CoordinateView>> {
	private String usernameTo;

	/**
	 * Initialize the service with the given arguments
	 * 
	 * @param usernameTo
	 *            The name of the user that requested the notifications.
	 */
	public ObtainFriendsPositionsService(String usernameTo) {
		super();
		this.usernameTo = usernameTo;
	}

	@Override
	protected HashMap<String, CoordinateView> action() throws SQLException {
		String selectQuery = "SELECT " + Tables.notifications
				+ ".usernameFrom, " + Tables.local_users + ".latitude, "
				+ Tables.local_users + ".longitude" + " FROM "
				+ Tables.tasks_users + " JOIN " + Tables.tasks + " ON "
				+ Tables.tasks_users + ".task_id = " + Tables.tasks
				+ ".id JOIN " + Tables.local_users + " ON "
				+ Tables.local_users + ".username = " + Tables.tasks_users
				+ ".username JOIN " + Tables.notifications + " ON "
				+ Tables.local_users + ".username = " + Tables.notifications
				+ ".usernameFrom WHERE " + Tables.notifications
				+ ".usernameTo = '" + usernameTo + "' AND "
				+ Tables.notifications
				+ ".approved = TRUE AND id IN (SELECT id FROM "
				+ Tables.tasks_users + " JOIN " + Tables.tasks + " ON "
				+ Tables.tasks_users + ".task_id = " + Tables.tasks
				+ ".id WHERE done <> TRUE AND username = '" + usernameTo
				+ "');";
		ResultSet rs = transaction.select(selectQuery);
		HashMap<String, CoordinateView> hm = new HashMap<String, CoordinateView>();
		while (rs.next()) {
			hm.put(rs.getString("usernameFrom"), new CoordinateView(rs
					.getInt("latitude"), rs.getInt("longitude")));
		}
		return hm;
	}
}
