package smartask.client.ui.sms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import biz.source_code.base64Coder.Base64Coder;
import edu.ist.smsserializer.Handler;
import edu.ist.smsserializer.IHandler;
import edu.ist.smsserializer.ISerializer;
import edu.ist.smsserializer.Serializer;

public class SmsReceiver extends BroadcastReceiver {
	private static final Map<String, List<SmsPart>> msgsReceived = new HashMap<String, List<SmsPart>>();

	/* package */static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION)) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Object messages[] = (Object[]) bundle.get("pdus");
				SmsMessage smsMessages[] = new SmsMessage[messages.length];
				
				for (int i = 0; i < messages.length; i++) {
					smsMessages[i] = SmsMessage
							.createFromPdu((byte[]) messages[i]);
					SmsMessage message = smsMessages[i];
					
					String encoded_msg = message.getMessageBody();
					String encoded_header = encoded_msg.substring(0, 8);
					String encoded_body = encoded_msg.substring(8);

					System.out.println("Header: " + encoded_header);
					System.out.println("Body: " + encoded_body);

					byte[] header = Base64Coder.decode(encoded_header);

					System.out.printf("TAG: %x\n", header[0]);
					System.out.printf("ID: %x\n", header[1]);
					System.out.printf("PNUM: %x\n", header[2]);
					System.out.printf("TNUM: %x\n", header[3]);

					if (header[0] == (byte) 0xc7) {
						String msg_id = message.getDisplayOriginatingAddress()
								+ (int) header[1];
						System.out.println("Msg ID: " + msg_id);
						SmsPart sms_part = new SmsPart(header[3], header[2],
								msg_id, encoded_body, message
										.getDisplayOriginatingAddress());
						if (!SmsReceiver.msgsReceived.containsKey(msg_id)) {
							SmsReceiver.msgsReceived.put(msg_id,
									new ArrayList<SmsPart>());
						}
						List<SmsPart> msgBuffer = SmsReceiver.msgsReceived
								.get(msg_id);
						msgBuffer.add(sms_part);
						if (sms_part.total == msgBuffer.size()) {
							Collections.sort(msgBuffer,
									new Comparator<SmsPart>() {
										public int compare(SmsPart object1,
												SmsPart object2) {
											return object1.part_num
													- object2.part_num;
										}
									});
							StringBuilder sb = new StringBuilder();
							for (SmsPart sms : msgBuffer) {
								sb.append(sms.body);
							}
							SmsReceiver.msgsReceived.remove(msg_id);
							ISerializer s = Serializer.getSerializer();
							try {
								Object value = s.deserialize(sb.toString());

								IHandler h = Handler.getHandler();
								System.out.println("Full message:" + msg_id
										+ " with " + msgBuffer.get(0).total
										+ " parts from "
										+ msgBuffer.get(0).from + ".");
								
								Object ret = h.handleIncomingMessage(value);
								if (ret != null) {
									SmsSender.sendObjectBySms(
											msgBuffer.get(0).from, ret);
								}

							} catch (Throwable e) {
								System.out.println(e);
							}
						}
					}
				}
			}
		}
	}
}
