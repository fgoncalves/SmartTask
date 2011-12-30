package smartask.client.observer;

/**
 * This interface defines the general contract for event publishers.<br/>
 * 
 * Objects that fire events should implement this interface.
 * 
 * @author Grupo 1
 */
public interface IEventPublisher {

	/**
	 * Method to register an event listener.<br/>
	 * 
	 * The way event listeners are managed is delegated to the subclass.
	 * 
	 * @param listener
	 *            The listener that wishes to listen for events fired by the
	 *            subclass.
	 */
	void registerEventListener(IEventListener listener);

	/**
	 * Removes an event listener from this event publisher.<br/>
	 * 
	 * @param listener
	 *            The listener to be removed from this event publisher.
	 */
	void removeEventListener(IEventListener listener);

	/**
	 * This method is called when the event is fired. At this point, all
	 * registered listeners will be notified of such event.
	 */
	void notifyListeners();
}
