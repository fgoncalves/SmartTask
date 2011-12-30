package engine.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * This class defines an output stream for the stderr and stdout messages.
 * 
 * @author Grupo 1
 */
public class StderrStdoutLoggingOutputStream extends ByteArrayOutputStream {
	private String lineSeparator;

	private Logger logger;
	private Level level;

	/**
	 * Initialize stream with the given log instance and level.
	 * @param logger A stderr or stdout logger.
	 * @param level A stderr or stdout level.
	 */
	public StderrStdoutLoggingOutputStream(Logger logger, Level level) {
		super();
		this.logger = logger;
		this.level = level;
		lineSeparator = System.getProperty("line.separator");
	}

	@Override
	public void flush() throws IOException {
		String record = "";
		synchronized (this) {
			super.flush();
			record = this.toString();
			super.reset();

			if (record.length() > 0 && !record.equals(lineSeparator))
				logger.logp(level, "", "", record);
		}
	}
}
