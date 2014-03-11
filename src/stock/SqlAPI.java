package stock;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SqlAPI {
	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	private String DB_END_POINT = "????";
	private final String DB_USER_NAME = "????";
	private final String DB_PWD = "????";
	private final String DB_NAME = "????";
	private final int DB_PORT = 3306;
	
	public void createConnectionAndStatement()
	{
		try{
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:mysql://"+DB_END_POINT+":"+DB_PORT+"/"+DB_NAME,DB_USER_NAME,DB_PWD);

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
			close();
		}
	}

	public void createTable()
	{
		try {
			createConnectionAndStatement();
			String createTableSql = "CREATE TABLE VIDEO_INFO (name VARCHAR(255) not NULL, timestamp TIMESTAMP, " + 
					" s3link VARCHAR(255), cflink VARCHAR(255), rating INTEGER, totalvotes INTEGER)";
			statement.executeUpdate(createTableSql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}

	}
	
	// You need to close the resultSet
	private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	}

} 