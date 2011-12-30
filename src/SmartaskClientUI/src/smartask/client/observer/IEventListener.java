package smartask.client.observer;

/**
 * This interface defines the general contract for an event listener.<br/>
 * 
 * We've created this mostly to provide a way of telling services that a certain
 * configuration has changed. Thus, this services should implement
 * IEventListener.
 * 
 * @author Grupo 1
 */
public interface IEventListener {
	/**
	 * This method should be implemented in order to receive event
	 * notifications.<br/>
	 * 
	 * The object that fired the event is passed as an argument to provide some
	 * context to the event listener
	 * 
	 * @param source
	 *            The object that fired the event.
	 */
	void handleEvent(IEventPublisher source);
}
