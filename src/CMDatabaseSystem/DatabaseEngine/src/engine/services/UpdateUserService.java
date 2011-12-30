package engine.services;

import java.sql.ResultSet;
import java.sql.SQLException;

import engine.core.Tables;
import engine.views.UserView;

/**
 * 
 * This service is used to update a user's characteristics, such as phone
 * number, email, etc..<br />
 * 
 * The action method returns the update profile or null if an error has ocurred.
 * 
 * @author Grupo 1
 */
public class UpdateUserService extends Service<UserView> {
	private String username;
	private String phoneNumber;
	private String email;

	/**
	 * Initialize the service with the given arguments.
	 * 
	 * @param username
	 *            The user name of the user to update the profile.
	 * @param phoneNumber
	 *            The phone number of the user.
	 * @param email
	 *            The user's updated email.
	 */
	public UpdateUserService(String username, String phoneNumber, String email) {
		this.username = username;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}

	/**
	 * Initialize the service with the given arguments.
	 * 
	 * @param uv
	 *            A view containing all the updated data of a given user.
	 */
	public UpdateUserService(UserView uv) {
		username = uv.getUsername();
		phoneNumber = uv.getPhoneNumber();
		email = uv.getEmail();
	}

	@Override
	protected UserView action() throws SQLException {
		String updateQuery = "UPDATE " + Tables.users + " SET email = '"
				+ email + "', telephone = '" + phoneNumber + "' WHERE name = '"
				+ username + "';";
		transaction.update(updateQuery);

		String selectUserQuery = "SELECT * FROM " + Tables.users
				+ " WHERE name = '" + username + "';";
		ResultSet rs = transaction.select(selectUserQuery);

		UserView uv = new UserView();
		if (rs.next()) {
			uv.setAcumulatedCredits(rs.getFloat("credits"));
			uv.setEmail(rs.getString("email"));
			uv.setPhoneNumber(rs.getString("telephone"));
			uv.setUsername(rs.getString("name"));

			return uv;
		}
		return null;
	}
}
