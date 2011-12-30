package engine.views;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

/**
 * 
 * This class represents a view of a task in the database.
 * 
 * @author Grupo 1
 */
public class TaskView implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * Interface which maps tasks' priorities into numbers.
	 * 
	 * @author Grupo 1
	 */
	public static interface Priority extends Serializable {
		final int NORMAL = 0;
		final int URGENT = 1;
		final int CRITICAL = 2;
	}

	private Integer id;
	private String name;
	private String description;
	private int priority;
	private int numberOfUsersNeededForCompletion;
	private float credits;
	private boolean done;
	private HashSet<CoordinateView> locals;
	private HashSet<UserTaskView> users;

	/**
	 * Initializes a view of a task with the given parameters
	 * 
	 * @param id
	 *            Auto generated id.
	 * @param name
	 *            Task's name
	 * @param description
	 *            Task's description
	 * @param priority
	 *            Task's priority
	 * @param numberOfUsersNeededForCompletion
	 *            Number of users needed to complete the task.
	 * @param done
	 *            True if the task was completed by all users to whom it was
	 *            assigned. Otherwise it should be false.
	 * @param credits
	 *            Number of total credits this task will give. Note that this
	 *            still needs to be divided among each user who completes the
	 *            task.
	 * @param locals
	 *            Locals where this task must be done.
	 * @param users
	 *            Users to whom this task is assigned.
	 */
	public TaskView(Integer id, String name, String description,
			Integer priority, Integer numberOfUsersNeededForCompletion,
			boolean done, float credits, HashSet<CoordinateView> locals,
			HashSet<UserTaskView> users) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.numberOfUsersNeededForCompletion = numberOfUsersNeededForCompletion;
		this.locals = locals;
		this.users = users;
		this.done = done;
		this.credits = credits;
	}

	/**
	 * Initializes an empty view.
	 */
	public TaskView() {
		locals = new HashSet<CoordinateView>();
		users = new HashSet<UserTaskView>();
	}

	public float getCredits() {
		return credits;
	}

	public void setCredits(float credits) {
		this.credits = credits;
	}

	public Collection<UserTaskView> getUsers() {
		return users;
	}

	public void setUsers(HashSet<UserTaskView> users) {
		this.users = users;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getNumberOfUsersNeededForCompletion() {
		return numberOfUsersNeededForCompletion;
	}

	public void setNumberOfUsersNeededForCompletion(
			int numberOfUsersNeededForCompletion) {
		this.numberOfUsersNeededForCompletion = numberOfUsersNeededForCompletion;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public HashSet<CoordinateView> getLocals() {
		return locals;
	}

	public void setLocals(HashSet<CoordinateView> locals) {
		this.locals = locals;
	}

	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TaskView)
			return id.equals(((TaskView) obj).id);
		return false;
	}

	@Override
	public String toString() {
		return name;
	}
}
