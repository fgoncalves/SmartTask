package smartask.sms.smsmanager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;
import android.util.Pair;
import engine.services.Service;

/**
 * This class implements a handles SMS messages with the pattern:<br/>
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
 * Upon receiving this message, an instance of
 * engine.services.UpdateUserLocalService is created, with the given parameters,
 * and its execute method is called.
 * 
 * Note that if the service returns any value it is ignore.
 * 
 * @author Grupo 1
 */
public class TreatSMSThread extends Thread {
	/**
	 * This class serves as an internal place holder for the parsed SMS.
	 * 
	 * @author Grupo 1
	 */
	private class ClassTree {
		private String className;
		private ArrayList<Pair<String, String>> typesAndValues;

		/**
		 * Initialize an instance of this object by parsing the given argument.
		 * 
		 * @param sms
		 *            The received SMS with the described pattern.
		 */
		public ClassTree(String sms) {
			typesAndValues = new ArrayList<Pair<String, String>>();
			String[] specs = sms.split(";");
			className = specs[0];
			for (int i = 1; i < specs.length; i++) {
				String[] pair = specs[i].split(":");
				typesAndValues.add(new Pair<String, String>(pair[0], pair[1]));
			}
		}
	}

	private final static String TAG = "TreatSMSThread";
	private final static String servicePackage = "engine.services";
	private final static HashMap<String, Pair<Class<?>, Method>> conversionTable = new HashMap<String, Pair<Class<?>, Method>>();

	private ClassTree classTree;

	/**
	 * Initialize an instance of this thread.
	 * @param sms The SMS to treat.
	 */
	public TreatSMSThread(String sms) {
		classTree = new ClassTree(servicePackage + "." + sms);

		try {
			conversionTable.put("int", new Pair<Class<?>, Method>(
					Integer.class, Integer.class.getDeclaredMethod("parseInt",
							new Class[] { String.class })));
			conversionTable.put("long", new Pair<Class<?>, Method>(Long.class,
					Long.class.getDeclaredMethod("parseLong",
							new Class[] { String.class })));
			conversionTable.put("double", new Pair<Class<?>, Method>(
					Double.class, Double.class.getDeclaredMethod("parseDouble",
							new Class[] { String.class })));
			conversionTable.put("float", new Pair<Class<?>, Method>(
					Float.class, Float.class.getDeclaredMethod("parseFloat",
							new Class[] { String.class })));
			conversionTable.put("boolean", new Pair<Class<?>, Method>(
					Boolean.class, Boolean.class.getDeclaredMethod(
							"parseBoolean", new Class[] { String.class })));
		} catch (SecurityException e) {
			Log.e(TAG, "Unable to create conversion table. Stack Trace:");
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			Log.e(TAG, "Unable to create conversion table. Stack Trace:");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			Class<?> c = Class.forName(classTree.className);
			Class<?>[] argsClasses = new Class[classTree.typesAndValues.size()];
			Object[] args = new Object[classTree.typesAndValues.size()];
			for (int i = 0; i < argsClasses.length; i++) {
				if (classTree.typesAndValues.get(i).first.equals("string")) {
					argsClasses[i] = String.class;
					args[i] = classTree.typesAndValues.get(i).second;
					continue;
				}
				argsClasses[i] = conversionTable.get(classTree.typesAndValues
						.get(i).first).first;
				args[i] = conversionTable
						.get(classTree.typesAndValues.get(i).first).second
						.invoke(conversionTable.get(classTree.typesAndValues
								.get(i).first),
								classTree.typesAndValues.get(i).second);
			}
			Constructor<?> constructor = c.getConstructor(argsClasses);
			Service<?> serv = (Service<?>) constructor.newInstance(args);
			Log.d(TAG, "Gateway executing service.....");
			serv.execute();
			Log.d(TAG, "Gateway executed service.");
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "Unable to create service class. Stack Trace:");
			e.printStackTrace();
		} catch (SecurityException e) {
			Log.e(TAG, "Unable to create service class. Stack Trace:");
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			Log.e(TAG, "Unable to create service class. Stack Trace:");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "Unable to create service class. Stack Trace:");
			e.printStackTrace();
		} catch (InstantiationException e) {
			Log.e(TAG, "Unable to create service class. Stack Trace:");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Log.e(TAG, "Unable to create service class. Stack Trace:");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			Log.e(TAG, "Unable to create service class. Stack Trace:");
			e.printStackTrace();
		}
	}
}
