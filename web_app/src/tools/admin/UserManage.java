package tools.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.Console;

import btaw.server.auth.Authenticator;
import btaw.server.auth.BCrypt;
import btaw.shared.model.BTAWDatabaseException;

public class UserManage {
	private static String newUserString = "INSERT INTO REDACTED VALUES (?, ?)";
	private static String deleteUserString = "DELETE FROM REDACTED WHERE username = ?";

	private Connection conn;

	public static void main(String[] args) {
		UserManage me = new UserManage();
		
		newMain(me);
		
		/*if (args.length == 9) {
			doStaticConnect(me, args);
			if (args[8].equals("-new")) {
				newMain(me);
			} else if (args[8].equals("-delete")) {
				deleteMain(me);
			}
		} else {
			System.out.println("Not enough arguments.");
			System.exit(-1);
		}*/
		System.out.println("Thank you for using UserManage");
		me.disconnect();
		System.exit(0);
	}
	
	private static void newMain(UserManage me) {
		//String newUser = me.promptUser();
		//String password = me.promptPassword(newUser);
		String newUser = "big_test_user";
		String password = "test";
		
		try {
			me.createUser(newUser, password);
		} catch (Exception ex) {
			System.out.println("Error adding user: " + ex.getMessage());
			System.exit(-1);
		}
	}
	
	private static void deleteMain(UserManage me) {
		String user = me.promptUser();
		try {
			me.deleteUser(user);
		} catch (Exception ex) {
			System.out.println("Error deleting user: " + ex.getMessage());
			System.exit(-1);
		}
	}
	
	private static void doStaticConnect(UserManage me, String[] args) {
		try {
			me.doConnect(args);
		} catch (BTAWDatabaseException ex) {
			System.out.println("Could not get connection to database.");
			ex.printStackTrace();
			System.exit(-1);
		}
	}

	private static String promptUser() {
		String ret = null;
		Console cons = System.console();
		if (cons == null) {
			return ret;
		}

		ret = new String(cons.readLine("%s\n? ", "Please enter web user name."));
		return ret;
	}

	private static String promptPassword(String user) {
		String ret = null;
		Console cons = System.console();
		if (cons == null) {
			return ret;
		}

		ret = new String(cons.readPassword("%s\n? ", "Please enter a password for user \"" + user + "\"."));
		return ret;
	}
	
	public void doConnect(String[] args) throws BTAWDatabaseException {
		conn = AdminConnector.doConnect(args);
	}

	public void createUser(String newUser, String password) throws SQLException, BTAWDatabaseException {
		if ((newUser == null) || (password == null) ||
				(newUser.length() == 0) || (password.length() == 0)) {
			throw new BTAWDatabaseException("Invalid username or password.");
		}
		System.out.println("username: " + newUser);
		
		String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(Authenticator.BCRYPT_SALT_ROUNDS));
		System.out.println("password: " + hashedPassword);
		
		PreparedStatement stmt = conn.prepareStatement(newUserString);
		stmt.setString(1, newUser);
		stmt.setString(2, hashedPassword);
		int updated = stmt.executeUpdate();
		if (updated != 1) {
			System.out.println("WTF user not added.");
		}
		conn.commit();
	}
	
	public void deleteUser(String user) throws SQLException, BTAWDatabaseException {
		PreparedStatement stmt = conn.prepareStatement(deleteUserString);
		stmt.setString(1, user);
		int updated = stmt.executeUpdate();
		if (updated != 1) {
			System.out.println("WTF user not deleted.");
		}
		conn.commit();
	}
	
	public void disconnect() {
		try {
			conn.commit();
			conn.close();
		} catch (Exception ex) {
			System.out.println("Error closing connection.");
			ex.printStackTrace();
		}
	}
}
