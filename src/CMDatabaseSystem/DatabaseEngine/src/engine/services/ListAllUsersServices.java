package engine.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import engine.core.Tables;
import engine.views.UserView;

/**
 * 
 * This service obtains every user in the database.
 * 
 * @author Grupo 1
 */
public class ListAllUsersServices extends Service<Collection<UserView>> {

	@Override
	public Collection<UserView> action() throws SQLException {
		String selectUsers = "SELECT * FROM " + Tables.users
				+ " LEFT OUTER JOIN " + Tables.tasks_users + " ON "
				+ Tables.users + ".name = " + Tables.tasks_users + ".username";
		ResultSet rs = transaction.select(selectUsers);
		HashMap<String, UserView> result = new HashMap<String, UserView>();
		while (rs.next()) {
			if (result.containsKey(rs.getString("name"))) {
				UserView uv = result.get(rs.getString("name"));
				if (rs.getInt("task_id") != 0)
					uv.getTasks().add(rs.getInt("task_id"));
				result.put(uv.getUsername(), uv);
			} else {
				UserView uv = new UserView(rs.getString("name"), rs
						.getString("telephone"), rs.getString("email"), rs
						.getFloat("credits"), new HashSet<Integer>());
				if (rs.getInt("task_id") != 0)
					uv.getTasks().add(rs.getInt("task_id"));
				result.put(uv.getUsername(), uv);
			}
		}
		return result.values();
	}
}
