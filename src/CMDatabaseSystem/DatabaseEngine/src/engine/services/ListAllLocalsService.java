package engine.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import engine.core.Tables;
import engine.views.CoordinateView;
import engine.views.LocalView;

/**
 * 
 * This service gives a Collection of all locals in the database. This is needed
 * to obtain each task in each local.
 * 
 * @author Grupo 1
 */
public class ListAllLocalsService extends Service<Collection<LocalView>> {

	@Override
	public Collection<LocalView> action() throws SQLException {
		String selectLocals = "SELECT * FROM " + Tables.tasks_locals + ";";
		ResultSet rs = transaction.select(selectLocals);
		HashMap<CoordinateView, LocalView> result = new HashMap<CoordinateView, LocalView>();
		while (rs.next()) {
			CoordinateView c = new CoordinateView(rs.getInt("latitude"), rs
					.getInt("longitude"));
			if (result.containsKey(c)) {
				LocalView lv = result.get(c);
				if (rs.getInt("task_id") != 0)
					lv.getTasks().add(rs.getInt("task_id"));
				result.put(lv.getCoordinate(), lv);
			} else {
				LocalView lv = new LocalView(rs.getInt("latitude"), rs
						.getInt("longitude"), new HashSet<Integer>());
				if (rs.getInt("task_id") != 0)
					lv.getTasks().add(rs.getInt("task_id"));
				result.put(lv.getCoordinate(), lv);
			}
		}
		return result.values();
	}
}
