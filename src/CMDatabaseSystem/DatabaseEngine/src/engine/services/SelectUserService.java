package engine.services;

import java.sql.ResultSet;
import java.sql.SQLException;

import engine.core.Tables;
import engine.views.UserView;

/**
 * 
 * This service is used to retrieve a UserView of a certain user in the
 * database. Null is returned if the user doesn't exist.
 * 
 * @author Grupo 1
 */
public class SelectUserService extends Service<UserView> {

	private String userName;

	/**
	 * Initialize the service with the given arguments.
	 * @param userName The name of the user to be retrieved.
	 */
	public SelectUserService(String userName) {
		super();
		this.userName = userName;
	}

	@Override
	protected UserView action() throws SQLException {
		String selectUserQuery = "SELECT * FROM " + Tables.users
				+ " LEFT OUTER JOIN " + Tables.tasks_users
				+ " ON name = username WHERE name = '" + userName + "';";
		ResultSet rs = transaction.select(selectUserQuery);

		UserView uv = new UserView();
		if (rs.next()) {
			uv.setAcumulatedCredits(rs.getFloat("credits"));
			uv.setEmail(rs.getString("email"));
			uv.setPhoneNumber(rs.getString("telephone"));
			uv.setUsername(rs.getString("name"));

			if (rs.getInt("task_id") != 0) {
				uv.getTasks().add(rs.getInt("task_id"));
				while (rs.next()) {
					uv.getTasks().add(rs.getInt("task_id"));
				}
			}
			return uv;
		}
		return null;
	}
}