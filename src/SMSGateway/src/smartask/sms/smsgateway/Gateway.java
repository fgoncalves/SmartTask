package smartask.sms.smsgateway;

import edu.ist.smsserializer.Handler;
import engine.core.MySQLConfiguration;
import smartask.sms.R;
import smartask.sms.smsmanager.StringHandler;
import android.app.Activity;
import android.os.Bundle;

/**
 * This class is the main activity of the gateway. It initializes the database
 * configuration and sets the SMS handler to an instance of a StringHandler.
 * 
 * @author Grupo 1
 */
public class Gateway extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		MySQLConfiguration.init(
				getString(R.string.mysql_database_services_log_file),
				getString(R.string.mysql_database_url),
				getString(R.string.mysql_database_user),
				getString(R.string.mysql_database_password));
		Handler.setHandler(new StringHandler());
	}
}
