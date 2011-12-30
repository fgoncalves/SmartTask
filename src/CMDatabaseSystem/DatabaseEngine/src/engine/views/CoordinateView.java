package engine.views;

import java.io.Serializable;

/**
 * 
 * This class represents a view of a geographic point stored in the database.
 * 
 * @author Grupo 1
 */
public class CoordinateView implements Serializable {
	private static final long serialVersionUID = 1L;
	private int latitude;
	private int longitude;

	/**
	 * Initialize an empty view.
	 */
	public CoordinateView() {
	}

	/**
	 * Initialize a coordinate view with the given latitude and longitude.
	 * @param latitude The latitude of the geographic point
	 * @param longitude The longitude of the geographic point
	 */
	public CoordinateView(int latitude, int longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public int getLatitude() {
		return latitude;
	}

	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}

	public int getLongitude() {
		return longitude;
	}

	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}

	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CoordinateView) {
			CoordinateView c = (CoordinateView) obj;
			return (c.latitude == latitude && c.longitude == longitude);
		}
		return false;
	}

	@Override
	public String toString() {
		return latitude + ";" + longitude;
	}
}
