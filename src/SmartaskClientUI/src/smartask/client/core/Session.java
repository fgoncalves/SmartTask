package smartask.client.core;

import java.util.HashMap;

/**
 * This class serves as a place holder for application context related things.
 * It stores the logged in user name and every configuration of the application.<br />
 * 
 * Note that objects kept in session will be destroyed upon a closing or
 * restarting the SmarTask application.
 * 
 * @author Grupo 1
 */
public final class Session {
	private static HashMap<String, Object> session = new HashMap<String, Object>();

	/**
	 * Register an association between a name and a value. This association can
	 * be retrieved with get method.
	 * 
	 * @param key
	 *            The name which will be associated with the value.
	 * @param obj
	 *            The value to keep in the session.
	 */
	public synchronized static void put(String key, Object obj) {
		session.put(key, obj);
	}

	/**
	 * Retrieve an object previously added to the session.
	 * 
	 * @param key
	 *            The name used to store the object.
	 * @return The instance stored in the session.
	 */
	public synchronized static Object get(String key) {
		return session.get(key);
	}

	/**
	 * Remove an association from the session.
	 * 
	 * @param key
	 *            The name of the previously stored object.
	 */
	public synchronized static void remove(String key) {
		session.remove(key);
	}

	public synchronized static boolean contains(String key) {
		return session.containsKey(key);
	}
}
