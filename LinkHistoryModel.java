import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class LinkHistoryModel {

	/**
	 * Get links visited by given user
	 * 
	 * @param username
	 * @return
	 */
	public static ArrayList<String> getLinks(String username) {
		Connection con = null;
		int userId;
		ArrayList<String> retval = null;
		try {
			con = DatabaseConnection.getConnection();
			Statement stmt = con.createStatement();
			ResultSet result = stmt.executeQuery("SELECT id "
					+ "FROM users WHERE username=\"" + username + "\"");
			if (result.next()) {
				userId = result.getInt("id");
				stmt = con.createStatement();
				result = stmt
						.executeQuery("SELECT link FROM links WHERE user_id='"
								+ userId + "'");
				retval = new ArrayList<String>();
				while (result.next()) {
					retval.add(result.getString("link"));
				}
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
		return retval;
	}

	/**
	 * Add link visited by user
	 * 
	 * @param username
	 * @param link
	 * @return
	 */
	public static boolean addLink(String username, String link) {
		Connection con = null;
		int userId;
		boolean retval = false;
		try {
			con = DatabaseConnection.getConnection();
			Statement stmt = con.createStatement();
			ResultSet result = stmt.executeQuery("SELECT id "
					+ "FROM users WHERE username=\"" + username + "\"");
			if (result.next()) {
				userId = result.getInt("id");
				stmt = con.createStatement();
				stmt.execute("INSERT INTO links (link,user_id) " + "VALUES ('"
						+ link + "','" + userId + "')");
				retval = true;
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
		return retval;
	}

	/**
	 * Clear all links visited by given user
	 * 
	 * @param username
	 * @return
	 */
	public static boolean clearLinks(String username) {
		Connection con = null;
		int userId;
		boolean retval = false;
		try {
			con = DatabaseConnection.getConnection();
			Statement stmt = con.createStatement();
			ResultSet result = stmt.executeQuery("SELECT id "
					+ "FROM users WHERE username=\"" + username + "\"");
			if (result.next()) {
				userId = result.getInt("id");
				stmt = con.createStatement();
				stmt.execute("DELETE FROM links where user_id='" + userId + "'");
				retval = true;
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
		return retval;
	}
}
