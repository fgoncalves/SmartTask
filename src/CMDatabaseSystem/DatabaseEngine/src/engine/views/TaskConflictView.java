package engine.views;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 
 * This class represents a view of a conflict.
 * 
 * @author Grupo 1
 */
public class TaskConflictView implements Serializable {
	private static final long serialVersionUID = 1L;

	private int number;
	private String usernameToRemoveCredits;
	private String usernameToAddCredits;
	private float creditsBeforeRemove;
	private float creditsAfterRemove;
	private float creditsBeforeAddition;
	private float creditsAfterAddition;
	private Timestamp firstCompletionOn;
	private Timestamp secondCompletionOn;

	/**
	 * Initializes a empty view.
	 */
	public TaskConflictView() {
	}

	/**
	 * Initializes a view with the given parameters
	 * 
	 * @param number
	 *            Auto generated conflict id.
	 * @param usernameToRemoveCredits
	 *            User from whom the credits will be removed.
	 * @param usernameToAddCredits
	 *            User to whom credits will be given.
	 * @param creditsBeforeRemove
	 *            Total number of credits of the user before removal.
	 * @param creditsAfterRemove
	 *            Total number of credits of the user after removal.
	 * @param creditsBeforeAddition
	 *            Total number of credits of the user before awarding credits.
	 * @param creditsAfterAddition
	 *            Total number of credits of the user after awarding credits.
	 * @param firstCompletionOn
	 *            The date corresponding to the first time the task was
	 *            completed.
	 * @param secondCompletionOn
	 *            The date corresponding to the second time the task was
	 *            completed.
	 */
	public TaskConflictView(int number, String usernameToRemoveCredits,
			String usernameToAddCredits, float creditsBeforeRemove,
			float creditsAfterRemove, float creditsBeforeAddition,
			float creditsAfterAddition, Timestamp firstCompletionOn,
			Timestamp secondCompletionOn) {
		super();
		this.number = number;
		this.usernameToRemoveCredits = usernameToRemoveCredits;
		this.usernameToAddCredits = usernameToAddCredits;
		this.creditsBeforeRemove = creditsBeforeRemove;
		this.creditsAfterRemove = creditsAfterRemove;
		this.creditsBeforeAddition = creditsBeforeAddition;
		this.creditsAfterAddition = creditsAfterAddition;
		this.firstCompletionOn = firstCompletionOn;
		this.secondCompletionOn = secondCompletionOn;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getUsernameToRemoveCredits() {
		return usernameToRemoveCredits;
	}

	public void setUsernameToRemoveCredits(String usernameToRemoveCredits) {
		this.usernameToRemoveCredits = usernameToRemoveCredits;
	}

	public String getUsernameToAddCredits() {
		return usernameToAddCredits;
	}

	public void setUsernameToAddCredits(String usernameToAddCredits) {
		this.usernameToAddCredits = usernameToAddCredits;
	}

	public float getCreditsBeforeRemove() {
		return creditsBeforeRemove;
	}

	public void setCreditsBeforeRemove(float creditsBeforeRemove) {
		this.creditsBeforeRemove = creditsBeforeRemove;
	}

	public float getCreditsAfterRemove() {
		return creditsAfterRemove;
	}

	public void setCreditsAfterRemove(float creditsAfterRemove) {
		this.creditsAfterRemove = creditsAfterRemove;
	}

	public float getCreditsBeforeAddition() {
		return creditsBeforeAddition;
	}

	public void setCreditsBeforeAddition(float creditsBeforeAddition) {
		this.creditsBeforeAddition = creditsBeforeAddition;
	}

	public float getCreditsAfterAddition() {
		return creditsAfterAddition;
	}

	public void setCreditsAfterAddition(float creditsAfterAddition) {
		this.creditsAfterAddition = creditsAfterAddition;
	}

	public Timestamp getFirstCompletionOn() {
		return firstCompletionOn;
	}

	public void setFirstCompletionOn(Timestamp firstCompletionOn) {
		this.firstCompletionOn = firstCompletionOn;
	}

	public Timestamp getSecondCompletionOn() {
		return secondCompletionOn;
	}

	public void setSecondCompletionOn(Timestamp secondCompletionOn) {
		this.secondCompletionOn = secondCompletionOn;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TaskConflictView)
			return number == ((TaskConflictView) obj).number;
		return false;
	}

	@Override
	public String toString() {
		return number + "- " + usernameToRemoveCredits + " ~ "
				+ usernameToAddCredits;
	}
}
