package server.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * The Singleton {@link Persistence} java class which handles the persisting of
 * incoming data in the DATA BASE. There are different methods for different
 * types of persisting.
 * 
 * @author costi.dumitrescu
 */
public class Persistence {

	/**
	 * Logger for logging.
	 */
	private static Logger logger = Logger.getLogger(Connection.class);
	
	/**
	 * Credentials.
	 */
	private Properties credentials;
	
	/**
	 * Singleton instance.
	 */
	private static Persistence INSTANCE = null;
	
	/**
	 * POSTGRESQL Driver main class.
	 */
	public static final String DRIVER = "org.postgresql.Driver";
	
	/**
	 * A connection (session) with a specific database. SQL statements are
	 * executed and results are returned within the context of a connection.
	 */
	private Connection connection = null;
	
	/**
	 * Private constructor. Singleton purpose.
	 */
	private Persistence() {
	}

	/**
	 * Returns the single reference for the {@link Persistence} instance. 
	 * Singleton purpose.
	 * 
	 * @return    The single reference for the {@link Persistence} instance.
	 */
	public static synchronized Persistence getInstance() {
		if (INSTANCE == null) {
			try {
				registerJdbcDriver();
				INSTANCE = new Persistence();
				INSTANCE.loadCredentials();
				INSTANCE.establishConnection();
			} catch (ClassNotFoundException e) {
				logger.error("Failed registering the driver. No message will be persisted in the data base for this session");
				INSTANCE = null;
			} catch (IOException e) {
				logger.error("Failed loading the credentials file. No message will be persisted in the data base for this session");
				INSTANCE = null;
			} catch (SQLException e) {
				logger.error("Failed establishing a connection with the Data Base. No message will be persisted in the data base for this session");
				INSTANCE = null;
			}
		}
		return INSTANCE;
	}

	/**
	 * Register the JDBC driver.
	 * 
	 * For this job, we have two approaches :
	 * 1 - Approach I  -> Class.forName()
	 * 2 - Approach II -> DriverManager.registerDriver()
	 * 
	 * Q : WHAT IS JDBC DRIVER? 
	 * A : JDBC drivers implement the defined interfaces in the
	 * 	   JDBC API, for interacting with your database server, in a third-party
	 *     jar. Third party vendors implements the java.sql.Driver interface in
	 *     their database driver.
	 *     
	 * @exception LinkageError if the linkage fails
     * @exception ExceptionInInitializerError if the initialization provoked
     *            by this method fails
     * @exception ClassNotFoundException if the class cannot be located
	 */
	private static void registerJdbcDriver() throws ClassNotFoundException {
		
		/*
		 * Register the JDBC driver.
		 */
		logger.warn("Registering the JDBC driver...");
		
		Class.forName(DRIVER);
	}
	
	/**
	 * Load the credentials properties file.
	 * 
	 * @exception  IOException  if an error occurred when reading from the
     *             input stream.
     * @throws     IllegalArgumentException if the input stream contains a
     *             malformed Unicode escape sequence.
	 */
	private void loadCredentials() throws IOException {
		/*
		 * Loading the credentials file.
		 */
		logger.warn("Loading the credentials file...");
		
		this.credentials = new Properties();
		InputStream in = this.getClass().getResourceAsStream("credentials.properties");
		try {
			this.credentials.load(in);
		} catch (Exception e) {
			// Catch and re-throw in java 7 style : the signature says this
			// method throws only IOException, but here I am, and I can throw
			// Exception. But wait, the load() method called in the try block
			// throws only IOException. There you go. That's how it knows the
			// TRUTH.
			// re-throw it away
			throw e;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// just .... do nothing!
			}
		}
	}
	
	/**
	 * Attempts to establish a connection to the database.
	 * 
	 * @exception SQLException if a database access error occurs or the URL is
     * 			  {@code null}
     * 
     * @throws 	  SQLTimeoutException  when the driver has determined that the
     * 			  timeout value specified by the {@code setLoginTimeout} method
     *            has been exceeded and has at least tried to cancel the
     *            current database connection attempt 
	 */
	private void establishConnection() throws SQLException {
		/*
		 * Open a connection.
		 */
		logger.warn("Connecting to the database...");
		
		String user     = (String) this.credentials.get("user");
		String password = (String)  this.credentials.get("password");
		String db_url   = (String)  this.credentials.get("db_url");
		
		// Attempts to establish a connection to the given database URL. The
		// DriverManager attempts to select an appropriate driver from the set
		// of registered JDBC drivers.
		this.connection = DriverManager.getConnection(db_url, user, password);
		
		// Connection is established.
		logger.warn("Connection established.");
	}
	
	/**
	 * Insert a message using simple statements. 
	 * 
	 * @param t_username The user name
	 * @param t_message  The message.
	 * @param t_time     The time.
	 */
	public void insertMessageSimpleStatement(String t_username, String t_message, String t_time) {
		// The object used for executing a static SQL statement and returning
		// the results it produces.
		Statement statement = null;
		try {
			// Create the statement.
			statement = this.connection.createStatement();
			String sql = "INSERT INTO ChatHistory (t_username, t_message, t_time) VALUES ('" + t_username + "', '"
					+ t_message + "', '" + t_time + "');";
			statement.executeUpdate(sql);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Failed to insert new message with simple statement : '" + t_username + ", " + t_message + ", "
					+ t_time + "' in Data Base. Error Message : " + e.getLocalizedMessage());
		}
	}

	/**
	 * Insert a message using prepared statements.
	 * 
	 * @param t_username The user name
	 * @param t_message  The message.
	 * @param t_time     The time.
	 */
	public void insertMessagePreparedStatement(String t_username, String t_message, String t_time) {
		// The object used for executing a prepared statement and returning
		// the results it produces.
		PreparedStatement preparedStatement = null;
		try {
			String sql = "INSERT INTO ChatHistory (t_username, t_message, t_time) VALUES (?, ?, ?);";
			// Create the prepared statement.
			preparedStatement = this.connection.prepareStatement(sql);
			// Set the values.
			preparedStatement.setString(1, t_username);
			preparedStatement.setString(2, t_message);
			preparedStatement.setString(3, t_time);
			// execute the query.
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			logger.error("Failed to insert new message with prepared statement : '" + t_username + ", " + t_message
					+ ", " + t_time + "' in Data Base. Error Message : " + e.getLocalizedMessage());
		}
	}
	
	/**
	 * Insert a message using callable statements.
	 * 
	 * @param t_username The user name
	 * @param t_message  The message.
	 * @param t_time     The time.
	 */
	public void insertMessageCallableStatement(String t_username, String t_message, String t_time) {
		// The object used for executing a SQL callable statement and returning
		// the results it produces.
		CallableStatement callableStatement = null;
		try {
			String sql = "{call addEntryInHistoryTable(?, ?, ?)}";
			// Create the callable statement.
			callableStatement = this.connection.prepareCall(sql);
			// Bind IN parameters.
			callableStatement.setString(1, t_username);
			callableStatement.setString(2, t_message);
			callableStatement.setString(3, t_time);
			// execute the query.
			callableStatement.executeUpdate();
			callableStatement.close();
		} catch (SQLException e) {
			logger.error("Failed to insert new message with callable statement : '" + t_username + ", " + t_message
					+ ", " + t_time + "' in Data Base. Error Message : " + e.getLocalizedMessage());
		}
	}
	
	/**
	 * Displays all entries in the chat history table using simple statement
	 */
	public void displayHistorySimpleStatement() {
		// The object used for executing a static SQL statement and returning
		// the results it produces.
		Statement statement = null;
		try {
			// Create the statement.
			statement = this.connection.createStatement();
			String sql = "Select * from ChatHistory";
			ResultSet rs = statement.executeQuery(sql);
			while(rs.next()) {
				// JUST FOR FUN
				int id = rs.getInt(1);
				String user = rs.getString(2);
				String message = rs.getString(3);
				String time = rs.getString(4);
				System.out.println("Id : " + id + ", User : " + user + ", Message : " + message + ", Time : " + time);
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			logger.error("Failed to display history with simple statement. Error Message : " + e.getLocalizedMessage());
		}
	}
	
	/**
	 * Displays all entries in the chat history table using prepared statement
	 */
	public void displayHistoryPreparedStatement() {
		// The object used for executing a prepared statement and returning
		// the results it produces.
		PreparedStatement preparedStatement = null;
		try {
			String sql = "Select * from ChatHistory Where t_id = ?";
			// Create the statement.
			preparedStatement = this.connection.prepareStatement(sql);
			preparedStatement.setInt(1, 3);
			ResultSet rs = preparedStatement.executeQuery();
			while(rs.next()) {
				// JUST FOR FUN
				int id = rs.getInt(1);
				String user = rs.getString(2);
				String message = rs.getString(3);
				String time = rs.getString(4);
				System.out.println("Id : " + id + ", User : " + user + ", Message : " + message + ", Time : " + time);
			}
			rs.close();
			preparedStatement.close();
		} catch (SQLException e) {
			logger.error("Failed to display history with prepared statement. Error Message : " + e.getLocalizedMessage());
		}
	}
	
	/**
	 * Displays all entries in the chat history table using prepared statement
	 */
	public void displayHistoryCallableStatement() {
		// The object used for executing a SQL callable statement and returning
		// the results it produces.
		CallableStatement callableStatement = null;
		try {
			// We must be inside a transaction for cursors to work.
			this.connection.setAutoCommit(false);
			String sql = "{? = call displayAllEntriesInHistoryTable()}";
			// Create the callable statement.
			callableStatement = this.connection.prepareCall(sql);
			// Bind OUT parameters.
			callableStatement.registerOutParameter(1, java.sql.Types.OTHER);
			// Execute the query.
			callableStatement.execute();
			// Retrieve
			ResultSet rs = (ResultSet) callableStatement.getObject(1);
			while(rs.next()) {
				// JUST FOR FUN
				int id = rs.getInt(1);
				String user = rs.getString(2);
				String message = rs.getString(3);
				String time = rs.getString(4);
				System.out.println("Id : " + id + ", User : " + user + ", Message : " + message + ", Time : " + time);
			}
			rs.close();
			callableStatement.close();
		} catch (SQLException e) {
			logger.error("Failed to display history with callable statement. Error Message : " + e.getLocalizedMessage());
		}
	}
}
