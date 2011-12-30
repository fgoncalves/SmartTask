package engine.services;

import java.sql.SQLException;

import engine.core.Tables;
import engine.views.CoordinateView;

/**
 * 
 * This service is used to update the coordinates in the database of the user's
 * current position. <br/>
 * 
 * The action method return true if the update was successfully done. Null
 * otherwise.
 * 
 * @author Grupo 1
 */
public class UpdateUserLocalService extends Service<Boolean> {
	private String username;
	private CoordinateView coordinate;

	/**
	 * Initialize the service with the given arguments.
	 * 
	 * @param username
	 *            The name of the user to update the position.
	 * @param coordinate
	 *            The user's current position.
	 */
	public UpdateUserLocalService(String username, CoordinateView coordinate) {
		super();
		this.username = username;
		this.coordinate = coordinate;
	}

	/**
	 * Initialize the service with the given arguments.
	 * 
	 * @param username
	 *            The name of the user to update the position.
	 * @param latitude
	 *            The user's current latitude.
	 * @param longitude
	 *            The user's current longitude.
	 */
	public UpdateUserLocalService(String username, int latitude, int longitude) {
		super();
		this.username = username;
		this.coordinate = new CoordinateView(latitude, longitude);
	}

	/**
	 * Initialize the service with the given arguments.<br/>
	 * This method should be used with Java Reflection API.
	 * 
	 * @param username
	 *            The name of the user to update the position.
	 * @param latitude
	 *            The user's current latitude.
	 * @param longitude
	 *            The user's current longitude.
	 */
	public UpdateUserLocalService(String username, Integer latitude,
			Integer longitude) {
		super();
		this.username = username;
		this.coordinate = new CoordinateView(latitude, longitude);
	}

	@Override
	protected Boolean action() throws SQLException {
		String updateQuery = "REPLACE INTO " + Tables.local_users + " VALUES('"
				+ username + "'," + coordinate.getLatitude() + ","
				+ coordinate.getLongitude() + ");";
		transaction.update(updateQuery);
		return true;
	}
}
