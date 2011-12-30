package engine.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import engine.core.Tables;
import engine.views.CoordinateView;

/**
 * 
 * This service is used to retrieve a collection of entries. Each entry has as
 * its first parameter a String corresponding to a username and a second
 * parameter as a CoordinateView, which represents the local where the user is.
 * 
 * @author Grupo 1
 */
public class SelectUserLocalsService extends
		Service<Collection<Entry<String, CoordinateView>>> {

	@Override
	protected Collection<Entry<String, CoordinateView>> action()
			throws SQLException {
		String query = "SELECT * FROM " + Tables.local_users;
		ResultSet rs = transaction.select(query);
		HashMap<String, CoordinateView> results = new HashMap<String, CoordinateView>();
		while (rs.next()) {
			results.put(rs.getString("username"), new CoordinateView(rs
					.getInt("latitude"), rs.getInt("longitude")));
		}
		return results.entrySet();
	}
}
