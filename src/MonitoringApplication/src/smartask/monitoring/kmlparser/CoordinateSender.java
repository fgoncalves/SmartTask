package smartask.monitoring.kmlparser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import android.util.Log;

/**
 * This thread is used to parse a given kml file and send each coordinate at a
 * fixed rate. If coordinates must be sent at a given time, then
 * CoordinateTimedSender should be used.
 * 
 * @author Grupo 1
 */
public class CoordinateSender extends Thread {
	private static final String LOG_TAG = "KMLCoordinateSender";
	private static volatile int SLEEP_TIME = 1000; // 5s default value

	private KMLHandler handler;
	private Socket socket;

	/**
	 * @param kmlFile
	 *            The name of the kml file to be parsed.
	 */
	public CoordinateSender(String kmlFile) {
		super();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			String[] specs = kmlFile.split("/");
			socket = new Socket("10.0.2.2", Integer
					.parseInt(specs[specs.length - 1].split("\\.")[0]));
		} catch (NumberFormatException e1) {
			Log.w(LOG_TAG, "There was an error opening the socket to: "
					+ kmlFile + ". Stak trace:");
			e1.printStackTrace();
		} catch (UnknownHostException e1) {
			Log.w(LOG_TAG, "There was an error opening the socket to: "
					+ kmlFile + ". Stak trace:");
			e1.printStackTrace();
		} catch (IOException e1) {
			Log.w(LOG_TAG, "There was an error opening the socket to: "
					+ kmlFile + ". Stak trace:");
			e1.printStackTrace();
		}
		try {
			SAXParser saxParser = factory.newSAXParser();
			handler = new KMLHandler();
			saxParser.parse(new File(kmlFile), handler);
		} catch (ParserConfigurationException e) {
			Log.w(LOG_TAG, "There was an error parsing the kml file: "
					+ kmlFile + ". Stak trace:");
			e.printStackTrace();
		} catch (SAXException e) {
			Log.w(LOG_TAG, "There was an error parsing the kml file: "
					+ kmlFile + ". Stak trace:");
			e.printStackTrace();
		} catch (IOException e) {
			Log.w(LOG_TAG, "There was an error parsing the kml file: "
					+ kmlFile + ". Stak trace:");
			e.printStackTrace();
		}
	}

	/**
	 * This method is used to convert decimal coordinates to military
	 * coordinates.
	 * 
	 * @param longitude
	 *            Decimal longitude to convert.
	 * @param latitude
	 *            Decimal latitude to convert.
	 * @return A string representing the nmea command to use in the telnet
	 *         command.
	 */
	private String convertToNMEA(double longitude, double latitude) {
		String nmea = "$GPGGA,%s,%03d%02d.%06d,%s,%03d%02d.%06d,%s,1,10,0.0,0.0,0,0.0,0,0.0,0000", latType, longType;

		if (latitude < 0)
			latType = "S";
		else
			latType = "N";

		if (longitude < 0)
			longType = "W";
		else
			longType = "E";

		latitude = Math.abs(latitude);
		longitude = Math.abs(longitude);

		int latDegrees = (int) latitude, longDegrees = (int) longitude;

		double latMinutes_d = (latitude - latDegrees) * 60.0;
		double longMinutes_d = (longitude - longDegrees) * 60.0;
		int latMinutes_i = (int) latMinutes_d;
		int longMinutes_i = (int) longMinutes_d;
		double latSeconds_d = (latMinutes_d - latMinutes_i) * 60.0;
		double longSeconds_d = (longMinutes_d - longMinutes_i) * 60.0;

		String time = new SimpleDateFormat("HHmmss.SSS").format(new Date())
				.toString();

		return String.format(nmea, time, latDegrees, latMinutes_i,
				(int) (latSeconds_d * 10000), latType, longDegrees,
				longMinutes_i, (int) (longSeconds_d * 10000), longType);
	}

	private int counter = 0;

	@Override
	public void run() {
		OutputStream os = null;
		InputStream is = null;
		Log.i(LOG_TAG, "{Starting thread - send coordinates to port: "
				+ socket.getPort() + "}");
		try {
			os = socket.getOutputStream();
			is = socket.getInputStream();
		} catch (IOException e) {
			Log.w(LOG_TAG,
					"There was an error getting the socket output stream to: "
							+ socket.getPort() + ". Stak trace:");
			e.printStackTrace();
			return;
		}
		byte[] answer = new byte[128];
		int read = 0;
		try {
			read = is.read(answer);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Log.d(LOG_TAG, new String(answer, 0, read));
		double[] coord = null;
		while (handler.hasCoordinates()) {
			try {
				coord = handler.getNextCoordinate();
				String nmea = convertToNMEA(coord[0], coord[1]);
				Log.d(LOG_TAG, "[" + counter++ + "] geo nmea " + nmea + " --> "
						+ socket.getPort());
				os.write(("geo nmea " + nmea + "\r\n").getBytes());
				os.flush();
				read = is.read(answer);
				Log.d(LOG_TAG, new String(answer, 0, read));
			} catch (IOException e) {
				Log.w(LOG_TAG, "There was an error sending coordinates "
						+ coord[0] + ";" + coord[1] + " to socket: "
						+ socket.getPort() + ". Stak trace:");
				e.printStackTrace();
			}
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				// Do nothing
			}
		}
		try {
			socket.close();
		} catch (IOException e) {
			Log.w(LOG_TAG, "Error closing socket.");
		}
		Log.i(LOG_TAG, "{Ending thread - send coordinates to port: "
				+ socket.getPort() + "}");
	}

	/**
	 * Method to set the sending rate of the coordinates. A base value is used,
	 * this value is 1 second. If a factor of two is applied, then each
	 * coordinate is sent with a half second rate.
	 * 
	 * @param factor
	 *            Factor to divide the base rate value.
	 */
	public static void setSleepTime(int factor) {
		SLEEP_TIME = (int) (1000 / factor);
		Log
				.d(LOG_TAG, "Sending coordinates at " + SLEEP_TIME
						+ " miliseconds.");
	}
}
