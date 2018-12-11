package tools.admin;

import java.io.Console;
import java.sql.Connection;
import java.util.HashMap;

import btaw.server.model.db.ConnectionPool;
import btaw.shared.model.BTAWDatabaseException;

public class AdminConnector {
	private enum ConnectionProperty {
		URL, USERNAME, PASSWORD, KS, KS_PASS, TS, TS_PASS
	}
	
	
	public static Connection doConnect(String[] args) throws BTAWDatabaseException {
		HashMap<ConnectionProperty,String> props = getProperties(args);
		ConnectionPool pool = new ConnectionPool(
				props.get(ConnectionProperty.URL),
				props.get(ConnectionProperty.USERNAME),
				props.get(ConnectionProperty.PASSWORD),
				props.get(ConnectionProperty.KS),
				props.get(ConnectionProperty.KS_PASS),
				props.get(ConnectionProperty.TS),
				props.get(ConnectionProperty.TS_PASS));
		return pool.getConnection();
	}
	
	private static HashMap<ConnectionProperty,String> getProperties(String[] args) {
		HashMap<ConnectionProperty,String> props = getParams(args);
		for (ConnectionProperty p: ConnectionProperty.values()) {
			if (!props.containsKey(p)) {
				props.put(p, promptFor(p));
			}
		}
		return props;
	}
	
	private static HashMap<ConnectionProperty,String> getParams(String[] args) {
		HashMap<ConnectionProperty, String> props = new HashMap<ConnectionProperty, String>();
		for (int i = 0; (i + 1) < args.length; i++) {
			String param = args[i++];
			String value = args[i];
			if (param.equals("-u")) {
				props.put(ConnectionProperty.URL, value);
			} else if (param.equals("-n")) {
				props.put(ConnectionProperty.USERNAME, value);
			} else if (param.equals("-p")) {
				props.put(ConnectionProperty.PASSWORD, value);
			} else if (param.equals("-k")) {
				props.put(ConnectionProperty.KS, value);
			} else if (param.equals("-K")) {
				props.put(ConnectionProperty.KS_PASS, value);
			} else if (param.equals("-t")) {
				props.put(ConnectionProperty.TS, value);
			} else if (param.equals("-T")) {
				props.put(ConnectionProperty.TS_PASS, value);
			} else {
				System.out.println("Parameter \"" + param + "\" not recognized, ignoring.");
			}
		}
		return props;
	}
	
	private static String promptFor(ConnectionProperty property) {
		String ret = null;
		Console cons = System.console();
		if (cons == null) {
			return ret;
		}
		switch (property) {
			case URL:
				ret = new String(cons.readLine("%s\n? ", "Please enter JDBC URL."));
				break;
			case USERNAME:
				ret = new String(cons.readLine("%s\n? ", "Please enter JDBC username."));
				break;
			case PASSWORD:
				ret = new String(cons.readPassword("%s\n? ", "Please enter JDBC password."));
				break;
			case KS:
				ret = new String(cons.readLine("%s\n? ", "Please enter location of keystore."));
				break;
			case KS_PASS:
				ret = new String(cons.readPassword("%s\n? ", "Please enter keystore password."));
				break;
			case TS:
				ret = new String(cons.readLine("%s\n? ", "Please enter location of truststore."));
				break;
			case TS_PASS:
				ret = new String(cons.readPassword("%s\n? ", "Please enter truststore password."));
				break;
			default:
				// Ignore this
				break;
		}
		return ret;
	}
}