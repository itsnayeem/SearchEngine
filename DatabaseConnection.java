import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

	/**
	 * Connects to database and returns connection
	 * 
	 * @return connection to database
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException {

		String host = "localhost"; // no slash
		String db = "user13";
		String port = "3306";
		String dbuser = "user13";
		String dbpass = "user13";

		try {
			// load driver
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			System.err.println("Can't find driver");
			System.exit(1);
		}

		// format "jdbc:mysql://[hostname][:port]/[dbname]"
		String urlString = "jdbc:mysql://" + host + ":" + port + "/" + db;
		return DriverManager.getConnection(urlString, dbuser, dbpass);
	}

}
