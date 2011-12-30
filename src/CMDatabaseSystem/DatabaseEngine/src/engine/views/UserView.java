package engine.views;

import java.io.Serializable;
import java.util.HashSet;

/**
 * 
 * This class represents a view of a user in the database.
 * 
 * @author Grupo 1
 */
public class UserView implements Serializable {
	private static final long serialVersionUID = 1L;
	private String username;
	private String phoneNumber;
	private String email;
	private float acumulatedCredits;
	private HashSet<Integer> tasks;

	/**
	 * Initialize a view with the given parameters
	 * 
	 * @param username
	 *            The name of the user in the database.
	 * @param phoneNumber
	 *            The user's phone number. Note that when using android
	 *            emulators this should be the same as the emulators' port.
	 * @param email
	 *            The user's email.
	 * @param acumulatedCredits
	 *            The user's accumulated credits.
	 * @param tasks
	 *            The user's tasks. Sometimes it is useful to initialize this to
	 *            an empty Set just to retrieve user's personal data.
	 */
	public UserView(String username, String phoneNumber, String email,
			float acumulatedCredits, HashSet<Integer> tasks) {
		this.username = username;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.acumulatedCredits = acumulatedCredits;
		this.tasks = tasks;
	}

	/**
	 * Initializes an empty view.
	 */
	public UserView() {
		this.tasks = new HashSet<Integer>();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public float getAcumulatedCredits() {
		return acumulatedCredits;
	}

	public void setAcumulatedCredits(float acumulatedCredits) {
		this.acumulatedCredits = acumulatedCredits;
	}

	public HashSet<Integer> getTasks() {
		return tasks;
	}

	public void setTasks(HashSet<Integer> tasks) {
		this.tasks = tasks;
	}

	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UserView) {
			return username.equals(((UserView) obj).username);
		}
		return false;
	}

	@Override
	public String toString() {
		return username;
	}
}
