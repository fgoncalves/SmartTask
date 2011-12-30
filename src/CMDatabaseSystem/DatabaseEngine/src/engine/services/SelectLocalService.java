package engine.services;

import java.sql.ResultSet;
import java.sql.SQLException;

import engine.core.Tables;
import engine.views.CoordinateView;
import engine.views.LocalView;

/**
 * 
 * This service is used to obtain a view of a certain local if it exists in the
 * database. Otherwise null is returned.
 * 
 * @author Grupo 1
 */
public class SelectLocalService extends Service<LocalView> {
	private CoordinateView coordinate;

	/**
	 * Initialize the service with the given arguments.
	 * 
	 * @param coordinate
	 *            The coordinates of the local to retrieve.
	 */
	public SelectLocalService(CoordinateView coordinate) {
		this.coordinate = coordinate;
	}

	/**
	 * Initialize the service with the given arguments.
	 * 
	 * @param latitude
	 *            The latitude of the local.
	 * @param longitude
	 *            The longitude of the local.
	 */
	public SelectLocalService(int latitude, int longitude) {
		this.coordinate = new CoordinateView(latitude, longitude);
	}

	@Override
	public LocalView action() throws SQLException {
		String selectLocalQuery = "SELECT * FROM " + Tables.tasks_locals
				+ " WHERE " + Tables.tasks_locals + ".latitude = "
				+ coordinate.getLatitude() + " AND " + Tables.tasks_locals
				+ ".longitude = " + coordinate.getLongitude();

		ResultSet rs = transaction.select(selectLocalQuery);
		LocalView lv = new LocalView();
		if (rs.next()) {
			lv.setCoordinate(new CoordinateView(rs.getInt("latitude"), rs
					.getInt("longitude")));
			if (rs.getInt("task_id") != 0) {
				lv.getTasks().add(rs.getInt("task_id"));
				while (rs.next()) {
					lv.getTasks().add(rs.getInt("task_id"));
				}
			}
			return lv;
		}
		return null;
	}
}
