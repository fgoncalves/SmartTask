package smartask.client.core;

import java.util.ArrayList;

import smartask.client.observer.IEventListener;
import smartask.client.observer.IEventPublisher;

/**
 * This class announces the change of the application's configuration through an
 * event. Listeners may register to this event in order to know when the
 * application configuration has changed.
 * 
 * @author Grupo 1
 */
public final class ApplicationConfiguration implements IEventPublisher {
	private static ApplicationConfiguration instance = null;
	private ArrayList<IEventListener> listeners;

	private ApplicationConfiguration() {
		listeners = new ArrayList<IEventListener>();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException(ApplicationConfiguration.class
				+ " is a singleton and cannot be cloned.");
	}

	/**
	 * Obtain the unique instance of this class
	 * 
	 * @return An instance of this class representing the only instance
	 *         available in the application.
	 */
	public synchronized static ApplicationConfiguration getInstance() {
		if (instance == null)
			instance = new ApplicationConfiguration();
		return instance;
	}

	@Override
	public void notifyListeners() {
		for (IEventListener l : listeners) {
			l.handleEvent(this);
		}
	}

	@Override
	public void registerEventListener(IEventListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeEventListener(IEventListener listener) {
		listeners.remove(listener);
	}
}
