package engine.logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * 
 * This class represents a formatter for the stderr and stdout log messages.
 * 
 * @author Grupo 1
 */
public class StderrStdoutFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
		return record.getMessage() + "\n";
	}

}
