package smartask.monitoring.kmlparser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.TreeMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
import android.util.Pair;

/**
 * This class is another implementation of the SAX default handler for kml
 * parsing. This should be used with CoordinateTimeSender to send timed
 * coordinates.
 * 
 * @author Grupo1
 */
public class KMLTimerHandler extends DefaultHandler {
	private boolean insideInterestingElement;
	private double[] currentCoordinate;
	private TreeMap<Date, double[]> parsedCoordinates;

	private String buffer = "";

	/**
	 * Initialize the handler.
	 */
	public KMLTimerHandler() {
		insideInterestingElement = false;
		parsedCoordinates = new TreeMap<Date, double[]>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (localName.equals("coordinates") || localName.equals("when"))
			insideInterestingElement = true;
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equals("coordinates")) {
			String[] coords = buffer.split(",");
			currentCoordinate = new double[] { Double.parseDouble(coords[0]),
					Double.parseDouble(coords[1]) };
			insideInterestingElement = false;
			buffer = "";
			return;
		}
		if (localName.equals("when")) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			Date date = null;
			try {
				date = df.parse(buffer);
			} catch (ParseException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			parsedCoordinates.put(date, currentCoordinate);
			insideInterestingElement = false;
			buffer = "";
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (!insideInterestingElement)
			return;
		buffer += new String(ch, start, length);
	}

	/**
	 * Obtain every timestamp in the kml file.
	 * 
	 * @return An ordered collection with every timestamp in the kml file.
	 */
	public Collection<Date> getTimestamps() {
		return parsedCoordinates.keySet();
	}

	/**
	 * This function is used to convert every timestamp to a present time. The
	 * idea is to use already generated kml files, which coordinates should have
	 * been sent in the past.
	 * 
	 * @param plus_mili_seconds
	 *            This is used to cause a small delay in each coordinate. Each
	 *            coordinate will have a timestamp equal to present time +
	 *            plus_mili_seconds.
	 */
	public void convertToPresent(long plus_mili_seconds) {

		TreeMap<Date, double[]> newParsed = new TreeMap<Date, double[]>();
		Date fd = parsedCoordinates.firstKey();

		if (fd.getTime() > new Date().getTime()) {
			Log.w("KMLTimerHandler.convertToPresent",
					"First date is in the future: " + fd.toLocaleString());
		}

		plus_mili_seconds += new Date().getTime() - fd.getTime();

		for (Date d : parsedCoordinates.keySet()) {
			long ms = d.getTime();
			ms += plus_mili_seconds;
			Date newd = new Date(ms);
			newParsed.put(newd, parsedCoordinates.get(d));
		}

		parsedCoordinates = newParsed;
	}

	/**
	 * Obtain the coordinate to send at a given timestamp.
	 * 
	 * @param timestamp
	 *            The timestamp associated with the coordinate.
	 * @return A double[] where the first position is the geographic point's
	 *         longitude and the second its latitude.
	 */
	public double[] getCoordinate(Date timestamp) {
		return parsedCoordinates.get(timestamp);
	}

	/**
	 * Provides a way of knowing if there are coordinates still to be sent.
	 * 
	 * @return True if there are coordinates still to be sent. False otherwise.
	 */
	public boolean hasNext() {
		return parsedCoordinates.size() > 0;
	}

	/**
	 * Obtain the next coordinate to be sent. Since we don't need the coordinate
	 * anymore it's removed.
	 * 
	 * @return A pair which first element is the timestamp of the coordinate and
	 *         a double[] where the first position is the geographic point's
	 *         longitude and the second its latitude.
	 */
	public Pair<Date, double[]> getNextCoordinate() {
		synchronized (parsedCoordinates) {
			Date fd = parsedCoordinates.firstKey();
			Pair<Date, double[]> pair = new Pair<Date, double[]>(fd,
					parsedCoordinates.get(fd));
			parsedCoordinates.remove(fd);
			return pair;
		}
	}
}
