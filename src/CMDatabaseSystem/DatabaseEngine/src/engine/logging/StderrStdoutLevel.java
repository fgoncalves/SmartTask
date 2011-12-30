package engine.logging;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.util.logging.Level;

/**
 * 
 * This class defines the level for the stdout and stderr log messages.
 * 
 * @author Grupo 1
 */
public class StderrStdoutLevel extends Level {
	private static final long serialVersionUID = 1L;

	protected StderrStdoutLevel(String name, int value) {
		super(name, value);
	}

	public static Level STDOUT = new StderrStdoutLevel("STDOUT", Level.INFO
			.intValue() + 53);

	public static Level STDERR = new StderrStdoutLevel("STDERR", Level.INFO
			.intValue() + 54);

	/**
	 * Obtain the correct log level.
	 * 
	 * @return The level for stdout or stderr,
	 * @throws ObjectStreamException
	 *             This is thrown if a proper level instance cannot be found.
	 */
	protected Object readResolve() throws ObjectStreamException {
		if (this.intValue() == STDOUT.intValue())
			return STDOUT;
		if (this.intValue() == STDERR.intValue())
			return STDERR;
		throw new InvalidObjectException("Unknown instance :" + this);
	}
}
