package edu.ist.smsserializer;

import java.io.IOException;

/**
 * Defines the general contract for Serializer instances.
 * 
 * @author Grupo 1
 */
public interface ISerializer {
	/**
	 * This method should serialize the given object and return its SMS
	 * representation.
	 * 
	 * @param values
	 *            The object(s) to serialize.
	 * @return A String representation of the serialized object.
	 * @throws IOException
	 *             Should be thrown if serialization fails.
	 */
	public String serialize(Object values) throws IOException;

	/**
	 * This method should deserialized a given SMS and return its Object
	 * representation.
	 * 
	 * @param serialized_values
	 *            The SMS text received, representing the Object(s) which
	 *            was(were) previously serialized.
	 * @return An Object instance of the deserialized received SMS.
	 * @throws IOException
	 *             If deserialization gives an error.
	 * @throws ClassNotFoundException
	 *             If the class of the received object isn't found.
	 */
	public Object deserialize(String serialized_values) throws IOException,
			ClassNotFoundException;
}
