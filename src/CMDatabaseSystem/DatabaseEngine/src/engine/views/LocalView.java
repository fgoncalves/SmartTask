package engine.views;

import java.io.Serializable;
import java.util.HashSet;

/**
 * 
 * This class represents a view of a local stored in the database. It's
 * different from CoordinateView because it has every task that is supposed to
 * be done in that local.
 * 
 * @author Grupo 1
 */
public class LocalView implements Serializable {
	private static final long serialVersionUID = 1L;
	private CoordinateView coordinate;
	private HashSet<Integer> tasks;

	/**
	 * Initialize a local view with the given parameters
	 * @param latitude The latitude of the local.
	 * @param longitude The longitude of the local.
	 * @param tasks The tasks in the local.
	 */
	public LocalView(int latitude, int longitude, HashSet<Integer> tasks) {
		super();
		this.coordinate = new CoordinateView(latitude, longitude);
		this.tasks = tasks;
	}

	/**
	 * Initializes an empty local view.
	 */
	public LocalView() {
		this.tasks = new HashSet<Integer>();
	}

	public CoordinateView getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(CoordinateView coordinate) {
		this.coordinate = coordinate;
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
		if (obj instanceof LocalView)
			return coordinate.equals(((LocalView) obj).coordinate);
		return false;
	}
}
