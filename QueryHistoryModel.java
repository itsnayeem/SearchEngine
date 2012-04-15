import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class QueryHistoryModel {

	/**
	 * Get queries performed by given user
	 * 
	 * @param username
	 * @return ArrayList of queries that match the given user
	 */
	public static ArrayList<String> getQueries(String username) {
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
						.executeQuery("SELECT query FROM queries WHERE user_id='"
								+ userId + "'");
				retval = new ArrayList<String>();
				while (result.next()) {
					retval.add(result.getString("query"));
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
	 * Add query for given user
	 * 
	 * @param username
	 * @param query
	 * @return results of operation
	 */
	public static boolean addQuery(String username, String query) {
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
				stmt.execute("INSERT INTO queries (query,user_id) "
						+ "VALUES ('" + query + "','" + userId + "')");
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
	 * Clear all queries matching given user
	 * 
	 * @param username
	 * @return results of operation
	 */
	public static boolean clearQueries(String username) {
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
				stmt.execute("DELETE FROM queries where user_id='" + userId
						+ "'");
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
