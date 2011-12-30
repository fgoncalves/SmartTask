package smartask.monitoring.kmlparser;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class implements the SAX default handler in order to sequentially parse
 * the kml file. It also stores the parsed coordinates into an ArrayList of
 * double[]. This is supposed to be used with the class CoordinateSender.
 * 
 * @author Grupo 1
 */
public class KMLHandler extends DefaultHandler {
	private boolean insideCoordinatesElement;
	private ArrayList<double[]> parsedCoordinates;

	/**
	 * Initialize handler.
	 */
	public KMLHandler() {
		parsedCoordinates = new ArrayList<double[]>();
		insideCoordinatesElement = false;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (localName.equals("coordinates"))
			insideCoordinatesElement = true;
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equals("coordinates")) {
			String[] coords = buffer.split(",");
			double[] coordinate = new double[] { Double.parseDouble(coords[0]),
					Double.parseDouble(coords[1]) };
			parsedCoordinates.add(coordinate);
			insideCoordinatesElement = false;
			buffer = "";
		}
	}

	private String buffer = "";

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// Remember that this function may be called or may not, so we need to
		// buffer every character we receive and flush the buffer when the end
		// element is encountered.
		if (!insideCoordinatesElement)
			return;
		buffer += new String(ch, start, length);
	}

	/**
	 * Obtains the next coordinate to be sent. Since we don't need it anymore
	 * it's removed.
	 * 
	 * @return A double[] where the first position is the geographic point's
	 *         longitude and the second its latitude.
	 */
	public double[] getNextCoordinate() {
		return parsedCoordinates.remove(0);
	}

	/**
	 * Provides a way of knowing if there are more coordinates to send.
	 * 
	 * @return True if there are still coordinates to be sent. False otherwise.
	 */
	public boolean hasCoordinates() {
		return !parsedCoordinates.isEmpty();
	}
}
