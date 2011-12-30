package engine.views;

import java.io.Serializable;

/**
 * 
 * This class represents a view of a notification request. It tells if it was
 * approved or if it still needs approval.
 * 
 * @author Grupo 1
 */
public class NotificationView implements Serializable {
	private static final long serialVersionUID = 1L;

	private String usernameFrom;
	private String usernameTo;
	private boolean approved;

	/**
	 * Initialize a view with the given arguments.
	 * @param usernameFrom User name to whom the request was sent.
	 * @param usernameTo User name that sent the request.
	 * @param sate The state of the request.
	 */
	public NotificationView(String usernameFrom, String usernameTo, boolean sate) {
		super();
		this.usernameFrom = usernameFrom;
		this.usernameTo = usernameTo;
		this.approved = sate;
	}

	/**
	 * Initialize an empty view.
	 */
	public NotificationView() {
		super();
	}

	public String getUsernameFrom() {
		return usernameFrom;
	}

	public void setUsernameFrom(String usernameFrom) {
		this.usernameFrom = usernameFrom;
	}

	public String getUsernameTo() {
		return usernameTo;
	}

	public void setUsernameTo(String usernameTo) {
		this.usernameTo = usernameTo;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean sate) {
		this.approved = sate;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NotificationView) {
			return (usernameFrom.equals(((NotificationView) obj).usernameFrom) && usernameTo
					.equals(((NotificationView) obj).usernameTo));
		}
		return false;
	}

	@Override
	public String toString() {
		return "Notify " + usernameTo + " of " + usernameFrom + " position.";
	}
}
