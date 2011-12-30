package smartask.sms.smsmanager;

import java.io.IOException;
import java.util.ArrayList;

import android.telephony.SmsManager;
import biz.source_code.base64Coder.Base64Coder;
import edu.ist.smsserializer.Serializer;

/**
 * This class provides a way of sending any kind of object through SMS. The
 * default behavior is to serialize each object and send it in Base 64. Since
 * this makes the message to big, we've chosen to extend this functionality with
 * the StringHandler class.
 * 
 * @author Grupo 1
 */
public final class SmsSender {
	public static final int MAX_SMS_SIZE = 152;
	public static final int HEADER_SIZE = 8;

	private static int counter = 0;

	/**
	 * Sends a given object through SMS.
	 * 
	 * @param to
	 *            The port number of the destination emulator.
	 * @param o
	 *            The object to send.
	 * @throws IOException
	 *             If the object cannot be serialized with our serializer class.
	 */
	public static void sendObjectBySms(String to, Object o) throws IOException {
		SmsManager sms_m = SmsManager.getDefault();
		String encoded_msg = Serializer.getSerializer().serialize(o);

		int total_msg = encoded_msg.length() / (MAX_SMS_SIZE - HEADER_SIZE) + 1;
		ArrayList<String> msg_parts = new ArrayList<String>();

		for (int i = 0, S = 0; i < total_msg; i++) {
			int size = (encoded_msg.length() - S > (MAX_SMS_SIZE - HEADER_SIZE)) ? (MAX_SMS_SIZE - HEADER_SIZE)
					: encoded_msg.length() - S;
			String msg_part = encoded_msg.substring(S, S + size);
			S += size;
			byte[] header = new byte[4];
			header[0] = (byte) 0XC7;
			header[1] = (byte) counter;
			header[2] = (byte) i;
			header[3] = (byte) total_msg;
			char[] encoded_header = Base64Coder.encode(header);
			msg_parts.add(new String(encoded_header) + msg_part);
		}

		counter++;

		for (String string : msg_parts) {
			System.out.println("SENDING MESSAGE PART: " + string);
			sms_m.sendTextMessage(to, "5554", string, null, null);
		}
	}
}
