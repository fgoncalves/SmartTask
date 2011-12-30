package edu.ist.smsserializer;


public interface IHandler {	
    /**
     * Handles an SMS request after deserialization
     * @param values Values defined in the incoming message
     * @return Object to return to the sender if not null
     */
	public Object handleIncomingMessage(Object values);
}
