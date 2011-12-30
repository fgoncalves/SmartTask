package edu.ist.smsserializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import biz.source_code.base64Coder.Base64Coder;

/**
 * This class implements a default serializer for our SMSs.
 * 
 * @author Grupo 1
 */
public class Serializer implements ISerializer {
	private static ISerializer serializer;

	/**
	 * Obtain the registered serializer.
	 * @return A ISerializer implementation.
	 */
	public static ISerializer getSerializer() {
		if (serializer == null) {
			serializer = new Serializer();
		}
		return serializer;
	}

	/**
	 * Set the serializer to be used, when serializing and deserializing SMSs.
	 * @param serializer
	 */
	public static void setSerializer(ISerializer serializer) {
		Serializer.serializer = serializer;
	}

	private Serializer() {
	}

	@Override
	public Object deserialize(String serializedValues)
			throws ClassNotFoundException, IOException {
		byte[] b = Base64Coder.decode(serializedValues);
		ByteArrayInputStream ba = new ByteArrayInputStream(b);
		ObjectInputStream in = new ObjectInputStream(ba);
		Object values = in.readObject();
		return values;
	}

	@Override
	public String serialize(Object values) throws IOException {
		ByteArrayOutputStream ba = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(ba);
		out.writeObject(values);
		byte[] b = ba.toByteArray();
		char[] msg = Base64Coder.encode(b);
		return new String(msg);
	}

}
