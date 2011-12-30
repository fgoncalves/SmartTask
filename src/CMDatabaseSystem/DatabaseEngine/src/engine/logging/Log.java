package engine.logging;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import engine.core.MySQLConfiguration;

/**
 * 
 * This class represents an instance of a log create when a log file name is
 * supplied to MySQLConfiguration.init method.
 * 
 * @author Grupo 1
 */
public class Log {
	/**
	 * Initializes the log, redirecting the stdout and stderr.
	 */
	public static void initializeLog() {

		try {
			if (MySQLConfiguration.getLogFile() != null
					&& !MySQLConfiguration.getLogFile().equals("")) {
				// initialize logging to go to rolling log file
				LogManager logManager = LogManager.getLogManager();
				logManager.reset();

				// log file max size 10K, 3 rolling files, append-on-open
				Handler fileHandler;
				try {
					fileHandler = new FileHandler(MySQLConfiguration
							.getLogFile(), 10000, 3, true);
					fileHandler.setFormatter(new StderrStdoutFormatter());
					Logger.getLogger("").addHandler(fileHandler);
					Logger logger;
					StderrStdoutLoggingOutputStream los;

					logger = Logger.getLogger("stdout");
					los = new StderrStdoutLoggingOutputStream(logger,
							StderrStdoutLevel.STDOUT);
					System.setOut(new PrintStream(los, true));

					logger = Logger.getLogger("stderr");
					los = new StderrStdoutLoggingOutputStream(logger,
							StderrStdoutLevel.STDERR);
					System.setErr(new PrintStream(los, true));
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
