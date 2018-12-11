package btaw.server.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import btaw.shared.model.BTAWDatabaseException;

public class Authenticator {
	public static int BCRYPT_SALT_ROUNDS = 12;

	private static String authString = "SELECT password_hash,access FROM login.users WHERE username = ?";
	private static String change_password_String = "UPDATE login.users SET password_hash = ? WHERE username = ?";
	
	private PreparedStatement authQuery = null;
	private PreparedStatement change_password_Query = null;
	private static String fakePassword = "FAKEPASSWORD";
	private String fakeHash = null;
	private String access = null;
	
	public Authenticator(Connection conn) throws BTAWDatabaseException {
		try {
			authQuery = conn.prepareStatement(authString);
		} catch (SQLException ex) {
			throw new BTAWDatabaseException("Unable to prepare authentication query.", ex);
		}

		//The following three lines will hash a password when creating a new user.
		//String newPassword = "t47dhj2olc";
		//String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt(BCRYPT_SALT_ROUNDS));
		//System.err.println("New Hash: " + newHash);

		fakeHash = BCrypt.hashpw(fakePassword, BCrypt.gensalt(BCRYPT_SALT_ROUNDS));
	}
	
	public boolean login(String name, String password) throws BTAWDatabaseException {
		if (authQuery == null) {
			throw new BTAWDatabaseException("Null authentication query.");
		}
		String hashedPass = null;
		try {
			authQuery.setString(1, name);
			ResultSet rset = authQuery.executeQuery();
			if (rset.next()) {
				hashedPass = rset.getString(1);
				access = rset.getString(2);
			} else {
				/* User doesn't exist, but we don't tell the caller this.  In fact, we
				 * waste a little time here pretending we tried to check it, so that nobody
				 * is wiser about whether the username is valid.
				 */
				BCrypt.checkpw(fakePassword, fakeHash);
				return false;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new BTAWDatabaseException("Database error during authentication.", ex);
		}
		return BCrypt.checkpw(password, hashedPass);
	}
	
	public boolean change_password(String username, String new_password, Connection conn) throws BTAWDatabaseException {
		try {
			change_password_Query = conn.prepareStatement(change_password_String);
		} catch (SQLException ex) {
			throw new BTAWDatabaseException("Unable to prepare change password query.", ex);
		}
		
		String new_hash = BCrypt.hashpw(new_password, BCrypt.gensalt(BCRYPT_SALT_ROUNDS));
		try {
			change_password_Query.setString(1, new_hash);
			change_password_Query.setString(2, username);
			int count = change_password_Query.executeUpdate();
			if (count==1)
				conn.commit();
			return (count == 1);
		} catch (SQLException ex) {
			ex.printStackTrace();
			throw new BTAWDatabaseException("Database error during authentication.", ex);
		}
	}
	
	public String getAccessLevel(){
		return access;
	}
	
	
}