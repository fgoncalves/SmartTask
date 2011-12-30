package smartask.client.db;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import smartask.client.core.Session;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import engine.views.CoordinateView;
import engine.views.TaskView;
import engine.views.UserTaskView;
import engine.views.UserView;

/**
 * @author Grupo 1
 * 
 *         This class implements all methods necessary to retrieve data from the
 *         SQLite database.<br/>
 *         Every method is static in order to be called from any class without
 *         creating a new instance of this class.
 */
public class DatabaseSQLiteQueryEngine {
	private final static String db_name = "smartask";
	private final static int db_version = 1;
	private final static String user_table_name = "users";
	private final static String user_local_table_name = "users_locals";
	private final static String tasks_table_name = "tasks";
	private final static String locals_table_name = "locals";
	private final static String tasks_locals_table_name = "tasks_locals";
	private final static String tasks_users_table_name = "tasks_users";
	private final static String pseudo_tasks_table_name = "pseudo_tasks";
	private final static String pseudo_tasks_locals_table_name = "pseudo_locals_tasks";

	/**
	 * Database helper
	 * 
	 * @author Grupo 1
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		/**
		 * Initialize database helper.
		 * 
		 * @param context
		 *            The application context.
		 * @param name
		 *            The name of the database.
		 * @param version
		 *            The version the the database.
		 */
		public DatabaseHelper(Context context, String name, int version) {
			super(context, name, null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db
					.execSQL("CREATE TABLE "
							+ user_table_name
							+ " ( name TEXT PRIMARY KEY, email TEXT, credits REAL NOT NULL, telephone TEXT UNIQUE NOT NULL);");
			db
					.execSQL("CREATE TABLE "
							+ tasks_table_name
							+ " (id INT PRIMARY KEY, name TEXT NOT NULL, description TEXT,	priority INT NOT NULL, done INT NOT NULL, credits REAL NOT NULL, numberOfUsersNeeded INT NOT NULL);");
			db
					.execSQL("CREATE TABLE "
							+ locals_table_name
							+ " (latitude INT, longitude INT, PRIMARY KEY(latitude,longitude));");

			db
					.execSQL("CREATE TABLE "
							+ tasks_locals_table_name
							+ " (latitude INT, longitude INT, task_id INT, PRIMARY KEY(latitude,longitude,task_id),  FOREIGN KEY(task_id) REFERENCES "
							+ tasks_table_name
							+ "(id) ON DELETE CASCADE ON UPDATE CASCADE,"
							+ "FOREIGN KEY(latitude,longitude) REFERENCES "
							+ locals_table_name
							+ "(latitude,longitude) ON DELETE CASCADE ON UPDATE CASCADE);");
			db
					.execSQL("CREATE TABLE "
							+ tasks_users_table_name
							+ " (username TEXT, task_id INT, completionDate LONG, completed INT NOT NULL, PRIMARY KEY(username,task_id), FOREIGN KEY(task_id) REFERENCES "
							+ tasks_table_name
							+ "(id) ON DELETE CASCADE ON UPDATE CASCADE,FOREIGN KEY(username) REFERENCES "
							+ user_table_name
							+ "(name) ON DELETE CASCADE ON UPDATE CASCADE);");
			db
					.execSQL("CREATE TABLE "
							+ pseudo_tasks_table_name
							+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, description TEXT, priority INT NOT NULL, numberOfUsersNeeded INT NOT NULL);");
			db
					.execSQL("CREATE TABLE "
							+ pseudo_tasks_locals_table_name
							+ "(latitude INT, longitude INT, task_id, PRIMARY KEY(latitude,longitude) FOREIGN KEY(task_id) REFERENCES "
							+ pseudo_tasks_table_name + "(id));");
			db
					.execSQL("CREATE TABLE "
							+ user_local_table_name
							+ "(username TEXT PRIMARY KEY, latitude INT, longitude INT, FOREIGN KEY(username) REFERENCES "
							+ user_table_name + "(name));");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + user_table_name);
			db.execSQL("DROP TABLE IF EXISTS " + user_local_table_name);
			db.execSQL("DROP TABLE IF EXISTS " + locals_table_name);
			db.execSQL("DROP TABLE IF EXISTS " + tasks_table_name);
			db.execSQL("DROP TABLE IF EXISTS " + tasks_locals_table_name);
			db.execSQL("DROP TABLE IF EXISTS " + tasks_users_table_name);
			db.execSQL("DROP TABLE IF EXISTS " + pseudo_tasks_table_name);
			db
					.execSQL("DROP TABLE IF EXISTS "
							+ pseudo_tasks_locals_table_name);
			onCreate(db);
		}
	}

	private static SQLiteDatabase db;
	private static Context context;

	/**
	 * Initialize the database. This method should be called only once.
	 * 
	 * @param ctxt
	 *            The application context.
	 */
	public static void init(Context ctxt) {
		context = ctxt;
		DatabaseHelper dh = new DatabaseHelper(context, db_name, db_version);
		db = dh.getWritableDatabase();
	}

	private DatabaseSQLiteQueryEngine() {
	}

	/**
	 * Obtain all users in the local database.
	 * 
	 * @return A Collection of user views, representing every contact in the
	 *         database.
	 */
	public static Collection<UserView> getContacts() {
		Cursor cursor = db.query(user_table_name, null, "name <> '"
				+ (String) Session.get("username") + "'", null, null, null,
				null);
		ArrayList<UserView> result = new ArrayList<UserView>();
		while (cursor.moveToNext()) {
			UserView uv = new UserView(cursor.getString(0),
					cursor.getString(3), cursor.getString(1), cursor
							.getFloat(2), new HashSet<Integer>());
			result.add(uv);
		}
		if (cursor != null && !cursor.isClosed())
			cursor.close();
		return result;
	}

	/**
	 * Adds a user position to the local database. This is used to store
	 * monitored user's positions.
	 * 
	 * @param username
	 *            The monitored user name.
	 * @param cv
	 *            The geographic point where the monitored user is.
	 */
	public static void addUserPosition(String username, CoordinateView cv) {
		ContentValues initialValues = new ContentValues();
		initialValues.put("username", username);
		initialValues.put("latitude", cv.getLatitude());
		initialValues.put("longitude", cv.getLongitude());

		try {
			db.insertOrThrow(user_table_name, null, initialValues);
		} catch (SQLException e) {
			db.replace(user_local_table_name, null, initialValues);
		}
	}

	/**
	 * This method creates a user contact on the database.
	 * 
	 * @param username
	 *            The user's name.
	 * @param email
	 *            The user's email.
	 * @param telephone
	 *            The user's phone number, which must be the emulator port.
	 * @param credits
	 *            The user's total credits.
	 * @return The number of users in the database.
	 */
	public static long createUser(String username, String email,
			String telephone, float credits) {
		ContentValues initialValues = new ContentValues();
		initialValues.put("name", username);
		initialValues.put("email", email);
		initialValues.put("credits", credits);
		initialValues.put("telephone", telephone);

		return db.replace(user_table_name, null, initialValues);
	}

	/**
	 * Obtain the monitored users' positions.
	 * 
	 * @return A hashmap containing the users in its keys and the positions in
	 *         its values.
	 */
	public static HashMap<String, CoordinateView> getUsersPositions() {
		Cursor cursor = db.query(user_local_table_name, null, null, null, null,
				null, null);
		HashMap<String, CoordinateView> usersPos = new HashMap<String, CoordinateView>();
		while (cursor.moveToNext()) {
			usersPos.put(cursor.getString(0), new CoordinateView(cursor
					.getInt(1), cursor.getInt(2)));
		}
		if (cursor != null && !cursor.isClosed())
			cursor.close();
		return usersPos;
	}

	/**
	 * Verifies if the user exists in the database.
	 * 
	 * @param username
	 *            The name of the user.
	 * @return True if the user exists. False otherwise.
	 */
	public static boolean login(String username) {
		Cursor cursor = db.query(user_table_name, new String[] { "name" },
				"name = '" + username + "'", null, null, null, null);
		boolean res = cursor.moveToFirst();
		if (cursor != null && !cursor.isClosed())
			cursor.close();
		return res;
	}

	/**
	 * Obtain a user view of a specific user.
	 * 
	 * @param username
	 *            The user's name.
	 * @return A view representing the user in the local database.
	 */
	public static UserView selectUser(String username) {
		Cursor cursor = db.query(user_table_name, null, "name = '" + username
				+ "'", null, null, null, null);
		if (cursor.moveToNext()) {
			UserView uv = new UserView();
			uv.setAcumulatedCredits(cursor.getFloat(2));
			uv.setEmail(cursor.getString(1));
			uv.setPhoneNumber(cursor.getString(3));
			uv.setUsername(cursor.getString(0));
			if (cursor != null && !cursor.isClosed())
				cursor.close();
			return uv;
		}
		if (cursor != null && !cursor.isClosed())
			cursor.close();
		return null;
	}

	/**
	 * Change a user contact in the database.
	 * 
	 * @param username
	 *            The user's name
	 * @param email
	 *            The user's email
	 * @param phone
	 *            The user's phone, which should be the same as the emulator's
	 *            port number.
	 * @param credits
	 *            The user's total credits.
	 */
	public static void alterUser(String username, String email, String phone,
			float credits) {
		ContentValues cv = new ContentValues();
		cv.put("email", email);
		cv.put("telephone", phone);
		cv.put("credits", credits);
		db.update(user_table_name, cv, "name = '" + username + "'", null);
	}

	/**
	 * See alterUser(String username, String email, String phone, float credits)
	 * 
	 * @param uv
	 *            A user view containing the user's data.
	 */
	public static void alterUser(UserView uv) {
		ContentValues cv = new ContentValues();
		cv.put("email", uv.getEmail());
		cv.put("telephone", uv.getPhoneNumber());
		cv.put("credits", uv.getAcumulatedCredits());
		db.update(user_table_name, cv, "name = '" + uv.getUsername() + "'",
				null);
	}

	/**
	 * Obtain all uncompleted tasks assigned to a certain user.
	 * 
	 * @param username
	 *            The user's name.
	 * @return Return a collection of every task in the local database, assigned
	 *         to the given user.
	 */
	public static Collection<TaskView> selectAllUncompletedTasks(String username) {
		Cursor cursor = db.query(tasks_table_name + " LEFT OUTER JOIN "
				+ tasks_locals_table_name + " ON " + tasks_table_name
				+ ".id = " + tasks_locals_table_name
				+ ".task_id LEFT OUTER JOIN " + tasks_users_table_name + " ON "
				+ tasks_table_name + ".id = " + tasks_users_table_name
				+ ".task_id", null, "completed = 0 AND username = '" + username
				+ "'", null, null, null, null);
		HashMap<Integer, TaskView> result = new HashMap<Integer, TaskView>();
		while (cursor.moveToNext()) {
			if (!result.containsKey(new Integer(cursor.getInt(0)))) {
				TaskView t = new TaskView();
				t.setId(cursor.getInt(0));
				t.setName(cursor.getString(1));
				t.setDescription(cursor.getString(2));
				t.setPriority(cursor.getInt(3));
				t.setNumberOfUsersNeededForCompletion(cursor.getInt(6));
				t.setDone(false);
				t.setCredits(cursor.getFloat(5));
				result.put(t.getId(), t);
			}
			if (!cursor.isNull(7)) {
				result.get(new Integer(cursor.getInt(0))).getLocals().add(
						new CoordinateView(cursor.getInt(7), cursor.getInt(8)));
			}
			if (!cursor.isNull(10)) {
				boolean bool;
				if (cursor.getInt(13) == 0)
					bool = false;
				else
					bool = true;
				result.get(new Integer(cursor.getInt(0))).getUsers().add(
						new UserTaskView(cursor.getString(10), cursor
								.getInt(11), new Timestamp(cursor.getLong(12)),
								bool));
			}
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return result.values();
	}

	/**
	 * Select every completed task.
	 * 
	 * @return A collection of all completed tasks in the local database.
	 */
	public static Collection<TaskView> selectAllCompletedTasks() {
		Cursor cursor = db.query(tasks_table_name + " LEFT OUTER JOIN "
				+ tasks_locals_table_name + " ON " + tasks_table_name
				+ ".id = " + tasks_locals_table_name
				+ ".task_id LEFT OUTER JOIN " + tasks_users_table_name + " ON "
				+ tasks_table_name + ".id = " + tasks_users_table_name
				+ ".task_id", null, "completed = 1", null, null, null, null);
		HashMap<Integer, TaskView> result = new HashMap<Integer, TaskView>();
		while (cursor.moveToNext()) {
			if (!result.containsKey(new Integer(cursor.getInt(0)))) {
				TaskView t = new TaskView();
				t.setId(cursor.getInt(0));
				t.setName(cursor.getString(1));
				t.setDescription(cursor.getString(2));
				t.setPriority(cursor.getInt(3));
				t.setNumberOfUsersNeededForCompletion(cursor.getInt(6));
				t.setDone(true);
				t.setCredits(cursor.getFloat(5));
				result.put(t.getId(), t);
			}
			if (!cursor.isNull(7)) {
				result.get(new Integer(cursor.getInt(0))).getLocals().add(
						new CoordinateView(cursor.getInt(7), cursor.getInt(8)));
			}
			if (!cursor.isNull(10)) {
				boolean bool;
				if (cursor.getInt(13) == 0)
					bool = false;
				else
					bool = true;
				result.get(new Integer(cursor.getInt(0))).getUsers().add(
						new UserTaskView(cursor.getString(10), cursor
								.getInt(11), new Timestamp(cursor.getLong(12)),
								bool));
			}
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return result.values();
	}

	/**
	 * Create an pseudo-task that represents a task without a global id.<br/>
	 * 
	 * Later it must be promoted to a full task. To do this one must send it to
	 * the main database where an id will be generated and the promotion will be
	 * performed.
	 * 
	 * @see smartask.client.ui.services.SynchronizationService
	 * 
	 * @param name
	 *            The task's name.
	 * @param description
	 *            The task's description.
	 * @param priority
	 *            The task's priority.
	 * @param numberOfUsersNeeded
	 *            The number of users needed to complete the task.
	 * @param points
	 *            A list of the coordinates of each local where the task must be
	 *            done.
	 */
	public static void createPseudoTask(String name, String description,
			int priority, int numberOfUsersNeeded,
			ArrayList<CoordinateView> points) {
		ContentValues initialValues = new ContentValues();
		initialValues.put("name", name);
		initialValues.put("description", description);
		initialValues.put("priority", priority);
		initialValues.put("numberOfUsersNeeded", numberOfUsersNeeded);

		db.insert(pseudo_tasks_table_name, null, initialValues);

		Cursor c = db.rawQuery("SELECT last_insert_rowid()", null);
		c.moveToNext();
		int taskId = c.getInt(0);
		for (CoordinateView coordinateView : points) {
			relateLocalToPseudoTask(taskId, coordinateView);
		}
		if (c != null && !c.isClosed()) {
			c.close();
		}
	}

	/**
	 * Create a local in the emulators database.
	 * 
	 * @param coord
	 *            The geographic point of the local.
	 */
	public static void insertLocal(CoordinateView coord) {
		ContentValues initialValues = new ContentValues();
		initialValues.put("latitude", coord.getLatitude());
		initialValues.put("longitude", coord.getLongitude());

		db.replace(locals_table_name, null, initialValues);
	}

	/**
	 * This method creates a relationship between a local and a pseudo task
	 * 
	 * @param id
	 *            The local id of the pseudo_task
	 * @param coord
	 *            The coordinate of a local.
	 */
	public static void relateLocalToPseudoTask(Integer id, CoordinateView coord) {
		ContentValues initialValues = new ContentValues();
		initialValues.put("task_id", id);
		initialValues.put("latitude", coord.getLatitude());
		initialValues.put("longitude", coord.getLongitude());

		db.insert(pseudo_tasks_locals_table_name, null, initialValues);
	}

	/**
	 * This method creates a relationship between a local and a task
	 * 
	 * @param id
	 *            The task's global id.
	 * @param coord
	 *            The local's coordinate.
	 */
	public static void relateLocalToTask(Integer id, CoordinateView coord) {
		ContentValues localTaskValues = new ContentValues();
		localTaskValues.put("task_id", id);
		localTaskValues.put("latitude", coord.getLatitude());
		localTaskValues.put("longitude", coord.getLongitude());

		db.replace(tasks_locals_table_name, null, localTaskValues);
	}

	/**
	 * Update the contacts in the local database.
	 * 
	 * @param users
	 *            The users stored in the main database.
	 */
	public static void updateUsers(Collection<UserView> users) {
		for (UserView uv : users) {
			ContentValues initialValues = new ContentValues();
			initialValues.put("name", uv.getUsername());
			initialValues.put("email", uv.getEmail());
			initialValues.put("credits", uv.getAcumulatedCredits());
			initialValues.put("telephone", uv.getPhoneNumber());
			db.replace(user_table_name, null, initialValues);
		}
	}

	/**
	 * Update the tasks in the local database.
	 * 
	 * @param tasks
	 *            The tasks stored in the main database.
	 */
	public static void updateTasks(Collection<TaskView> tasks) {
		for (TaskView tv : tasks) {
			ContentValues initialValues = new ContentValues();
			initialValues.put("id", tv.getId());
			initialValues.put("name", tv.getName());
			initialValues.put("description", tv.getDescription());
			initialValues.put("priority", tv.getPriority());
			initialValues.put("done", 0);
			initialValues.put("credits", tv.getCredits());
			initialValues.put("numberOfUsersNeeded", tv
					.getNumberOfUsersNeededForCompletion());

			db.replace(tasks_table_name, null, initialValues);

			for (CoordinateView c : tv.getLocals()) {
				insertLocal(c);
				relateLocalToTask(tv.getId(), c);
			}

			for (UserTaskView u : tv.getUsers()) {
				relateUserToTask(u);
			}
		}
	}

	/**
	 * Mark a task as completed by the given user.
	 * 
	 * @param task
	 *            The task to complete.
	 * @param username
	 *            The user that completed the task.
	 */
	public static void concludeTask(TaskView task, String username) {
		ContentValues initialValues = new ContentValues();
		initialValues.put("completed", 1);
		initialValues.put("completionDate", System.currentTimeMillis());

		db.update(tasks_users_table_name, initialValues, "task_id = "
				+ task.getId() + " AND username = '" + username + "'", null);
	}

	/**
	 * Create a relationship between a user and a task.
	 * 
	 * @param u
	 *            The user and task to create the relationship.
	 */
	private static void relateUserToTask(UserTaskView u) {
		ContentValues localTaskValues = new ContentValues();
		localTaskValues.put("task_id", u.getTaskID());
		localTaskValues.put("username", u.getUsername());
		localTaskValues.put("completionDate", u.getCompletionDate().getTime());
		int value;
		if (u.isCompleted())
			value = 1;
		else
			value = 0;
		localTaskValues.put("completed", value);

		db.replace(tasks_users_table_name, null, localTaskValues);
	}

	/**
	 * Get the user's name associated with a phone number.
	 * 
	 * @param phoneNumber
	 *            The user's phone number.
	 * @return The user's name associated with the given phone number, or null
	 *         if it doesn't exist.
	 */
	public static String getUsername(String phoneNumber) {
		Cursor cursor = db.query(user_table_name, new String[] { "name" },
				"telephone = '" + phoneNumber + "'", null, null, null, null);
		cursor.moveToFirst();
		String result = cursor.getString(0);

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return result;
	}

	/**
	 * Obtain all pseudo tasks in the emulator's database.
	 * 
	 * @return A collection of the pseudo tasks stored in the emulator's
	 *         database.
	 */
	public static Collection<TaskView> selectAllPseudoTasks() {
		Cursor cursor = db.query(pseudo_tasks_table_name + " LEFT OUTER JOIN "
				+ pseudo_tasks_locals_table_name + " ON "
				+ pseudo_tasks_table_name + ".id = "
				+ pseudo_tasks_locals_table_name + ".task_id", null, null,
				null, null, null, null);
		HashMap<Integer, TaskView> result = new HashMap<Integer, TaskView>();
		while (cursor.moveToNext()) {
			if (!result.containsKey(new Integer(cursor.getInt(0)))) {
				TaskView t = new TaskView();
				t.setId(cursor.getInt(0));
				t.setName(cursor.getString(1));
				t.setDescription(cursor.getString(2));
				t.setPriority(cursor.getInt(3));
				t.setNumberOfUsersNeededForCompletion(cursor.getInt(4));
				result.put(t.getId(), t);
			}
			if (!cursor.isNull(5)) {
				result.get(new Integer(cursor.getInt(0))).getLocals().add(
						new CoordinateView(cursor.getInt(5), cursor.getInt(6)));
			}
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return result.values();
	}

	/**
	 * @return The number of tasks in the emulator's database.
	 */
	public static int countTasks() {
		Cursor c = db
				.rawQuery("SELECT COUNT(*) FROM " + tasks_table_name, null);
		c.moveToNext();
		int res = c.getInt(0);
		if (c != null && !c.isClosed()) {
			c.close();
		}
		return res;
	}

	/**
	 * Mark tasks as completed so they can be cleaned.
	 * 
	 * @param tasks
	 *            A collections with the completed tasks.
	 */
	public static void updateUserCompletedTasks(Collection<?> tasks) {
		for (Object o : tasks) {
			if (o instanceof TaskView) {
				TaskView tv = (TaskView) o;
				for (UserTaskView user : tv.getUsers()) {
					ContentValues initialValues = new ContentValues();
					initialValues.put("username", user.getUsername());
					initialValues.put("task_id", tv.getId());
					initialValues.put("completionDate", user
							.getCompletionDate().getTime());
					initialValues.put("completed", user.isCompleted());
					db.replace(tasks_users_table_name, null, initialValues);
				}
			} else
				Log.w("SQLite database",
						"Could not update task: Not an instance of TaskView");
		}
	}
	
	public static void cleanCompletedTasks() {
		db.delete(tasks_table_name, "done = 1", null);
		db.delete(tasks_users_table_name, "completed = 1", null);
	}

	public static void cleanPseudoEntities() {
		db.delete(pseudo_tasks_locals_table_name, null, null);
		db.delete(pseudo_tasks_table_name, null, null);
	}

	public static void close() {
		db.close();
	}
}
