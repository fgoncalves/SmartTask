package smartask.monitoring.kmlparser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import android.util.Log;

/**
 * This thread sends coordinates at a given time.
 * 
 * @author Grupo 1
 */
public class CoordinateTimedSender extends Thread {
	private static final String LOG_TAG = "KMLCoordinateSender";

	private KMLTimerHandler handler;
	private Socket socket;

	private int counter = 0;

	/**
	 * @param kmlFile
	 *            The name of the kml file to parse.
	 */
	public CoordinateTimedSender(String kmlFile) {
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
			handler = new KMLTimerHandler();
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
		String nmea = "$GPGGA,%s,%03d%09.6f,%s,%03d%09.6f,%s,1,10,0.0,0.0,0,0.0,0,0.0,0000", latType, longType;

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

		String time = new SimpleDateFormat("HHmmss.SSS").format(new Date())
				.toString();

		return String.format(nmea, time, latDegrees, latMinutes_d, latType, longDegrees,
				longMinutes_d, longType);
	}

	private OutputStream os = null;
	private InputStream is = null;

	@Override
	public void run() {
		Log.i(LOG_TAG, "{Starting thread - send coordinates to port: "
				+ socket.getPort() + "}");
		System.out.println("I HAVE TO SEND " + handler.getTimestamps().size()
				+ " COORDINATES");

		handler.convertToPresent(20000);

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
		synchronized (is) {
			byte[] answer = new byte[128];
			int read = 0;
			try {
				read = is.read(answer);
				Log.d(LOG_TAG, new String(answer, 0, read));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		for (final Date date : handler.getTimestamps()) {
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					double[] coord = null;
					try {
						coord = handler.getCoordinate(date);
						String nmea = convertToNMEA(coord[0], coord[1]);
						Log.d(LOG_TAG, "[" + counter++ + "] geo nmea " + nmea
								+ " --> " + socket.getPort());
						synchronized (os) {
							os.write(("geo nmea " + nmea + "\r\n").getBytes());
							os.flush();
						}
						synchronized (is) {
							byte[] answer = new byte[128];
							int read = 0;
							read = is.read(answer);
							Log.d(LOG_TAG, new String(answer, 0, read));
						}
					} catch (IOException i) {
						Log.w(LOG_TAG,
								"There was an error sending coordinates "
										+ coord[0] + ";" + coord[1]
										+ " to socket: " + socket.getPort()
										+ ". Stak trace:");
						i.printStackTrace();
					}
				}

			}, date);
		}
		Log.i(LOG_TAG, "{Ending thread - send coordinates to port: "
				+ socket.getPort() + "}");
	}
}
