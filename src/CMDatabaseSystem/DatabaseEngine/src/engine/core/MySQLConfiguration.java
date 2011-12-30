package engine.core;

import java.sql.SQLException;

/** 
 * This class initializes MySQL configurations.<br/>
 * 
 * Every message can be written to a circular log with maximum size 10K.
 * 
 * @author Grupo 1
 */
public final class MySQLConfiguration {
	private static String logFile;
	private static String username;
	private static String password;
	private static String url;

	private static boolean isInitialized;

	/**
	 * This methods does all the work necessary to initialize MySQL database
	 * configuration.
	 * 
	 * @param logFile
	 *            The name of the log file where errors and messages will be
	 *            logged. If null or an empty string is supplied, than stdout
	 *            will be used instead.
	 * @param url
	 *            The remote database url. This should be supplied with the
	 *            database name:<br/>
	 *            jdbc:mysql://host/databasename
	 * @param username
	 *            The database user name used to access the schema.
	 * @param pass
	 *            The user's password.
	 */
	public synchronized static void init(String logFile, String url,
			String username, String pass) {
		MySQLConfiguration.logFile = logFile;
		MySQLConfiguration.password = pass;
		MySQLConfiguration.url = url;
		MySQLConfiguration.username = username;
		isInitialized = true;
	}

	/**
	 * Obtain the name of the log file.
	 * 
	 * @return A String representing the log file name.
	 * @throws SQLException
	 *             This is thrown if init was never called.
	 */
	public static synchronized String getLogFile() throws SQLException {
		if (!isInitialized)
			throw new SQLException("MySQLConfiguration are not initialized.");
		return logFile;
	}

	/**
	 * Obtain the user name that accesses the database.
	 * 
	 * @return A String representing the user name
	 * @throws SQLException
	 *             This is thrown if init was never called.
	 */
	public static synchronized String getUsername() throws SQLException {
		if (!isInitialized)
			throw new SQLException("MySQLConfiguration are not initialized.");
		return username;
	}

	/**
	 * Obtain the password of the user that accesses the database
	 * 
	 * @return A String representing the password
	 * @throws SQLException
	 *             This is thrown if init was never called.
	 */
	public static synchronized String getPassword() throws SQLException {
		if (!isInitialized)
			throw new SQLException("MySQLConfiguration are not initialized.");
		return password;
	}

	/**
	 * Obtain the remote database url
	 * 
	 * @return A String representing the database's url.
	 * @throws SQLException
	 *             This is thrown if init was never called.
	 */
	public static synchronized String getUrl() throws SQLException {
		if (!isInitialized)
			throw new SQLException("MySQLConfiguration are not initialized.");
		return url;
	}
}
