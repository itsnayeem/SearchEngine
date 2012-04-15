import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserModel {

	/**
	 * Authenticate user with database. If given username exists, check to see
	 * if stored password matches given password.
	 * 
	 * @param username
	 * @param password
	 * @return results of operation
	 */
	public static boolean authUser(String username, String password) {
		Connection con = null;
		boolean retval = false;
		try {
			String storedPass;
			con = DatabaseConnection.getConnection();
			Statement stmt = con.createStatement();
			ResultSet result = stmt.executeQuery("SELECT * "
					+ "FROM users WHERE username=\"" + username + "\"");

			if (result.next()) {
				storedPass = result.getString("password");
				if (password.equals(storedPass)) {
					retval = true;
				}
			}
		} catch (SQLException e) {
			System.out.println("Connection error");
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
			}
		}
		return retval;
	}

	/**
	 * If username exists and previous password matches stores password, replace
	 * stored password with given new password.
	 * 
	 * @param username
	 * @param oldpass
	 * @param newpass
	 * @return results of operation
	 */
	public static boolean changePassword(String username, String oldpass,
			String newpass) {
		Connection con = null;
		boolean retval = false;
		int userId;
		try {
			String storedPass;
			con = DatabaseConnection.getConnection();
			Statement stmt = con.createStatement();
			ResultSet result = stmt.executeQuery("SELECT * "
					+ "FROM users WHERE username=\"" + username + "\"");

			if (result.next()) {
				storedPass = result.getString("password");
				userId = result.getInt("id");
				if (oldpass.equals(storedPass)) {
					stmt = con.createStatement();
					stmt.execute("UPDATE users set password='" + newpass
							+ "' where id='" + userId + "';");
					retval = true;
				}
			}
		} catch (SQLException e) {
			System.out.println("Connection error");
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
			}
		}
		return retval;
	}

	/**
	 * If username does not exist, add user with given username and password
	 * 
	 * @param username
	 * @param password
	 */
	public static void addUser(String username, String password) {
		Connection con = null;
		try {
			con = DatabaseConnection.getConnection();
			Statement stmt = con.createStatement();
			ResultSet result = stmt.executeQuery("SELECT * "
					+ "FROM users WHERE username=\"" + username + "\"");

			if (!result.next()) {
				con = DatabaseConnection.getConnection();
				stmt = con.createStatement();
				stmt.execute("INSERT INTO users (username,password) "
						+ "VALUES ('" + username + "','" + password + "')");
			}
		} catch (SQLException e) {
			System.out.println("Connection error");
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
			}
		}
	}
}
