package smartask.sms.smsmanager;

import android.util.Log;
import edu.ist.smsserializer.IHandler;

/**
 * This class implements a handler, which treats SMS messages with the pattern:<br/>
 * <br/>
 * service_class;parameter_type1:parameter_value1;parameter_type2:
 * parameter_value2;.......;parameter_typeN:parameter_valueN<br/>
 * <br/>
 * 
 * An example of such message can be:<br/>
 * <br/>
 * UpdateUserLocalService:string:foo;int:2;int6<br/>
 * <br/>
 * 
 * Which will update foo's position to latitude 2 and longitude 6.<br/>
 * 
 * If the received message is not an instance of String it is ignored.
 * @see TreatSMSThread
 * 
 * @author Grupo 1
 */
public class StringHandler implements IHandler {

	public Object handleIncomingMessage(Object arg0) {
		if (arg0 instanceof String) {
			Log.d("StringHandler", "Handling incoming message: "
					+ (String) arg0);
			new TreatSMSThread((String) arg0).start();
		}
		return null;
	}

}
