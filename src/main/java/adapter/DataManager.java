package adapter;

import exception.DataManagerException;
import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.ORAData;
import utils.Logger;
import oracle.jdbc.pool.OracleDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class for communicating with the database.
 * <p>
 * All database operations needed in the application are
 * included in this class. Class uses singleton pattern
 * and static method getInstance() should be called
 * instead of the constructor.
 *
 * @author Jakub Tutko
 */
public class DataManager {
	private static DataManager instance = new DataManager();

	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;

	/**
	 * Method connects to the database and save connection for later use.
	 * Database's URL is fixed.
	 *
	 * @param userName user name
	 * @param password user password
	 * @throws DataManagerException when can not connect to the database
	 */
	public void connectDatabase(String userName, String password) throws DataManagerException {
		try {
			OracleDataSource ods = new OracleDataSource();

			ods.setURL("jdbc:oracle:thin:@gort.fit.vutbr.cz:1521:dbgort");
			ods.setUser(userName);
			ods.setPassword(password);

			this.connection = ods.getConnection();

		} catch (SQLException sqlEx) {
			throw new DataManagerException("SQLException: " + sqlEx.getMessage());
		}
	}

	/**
	 * Method opens script saved on local disk and initialize database data.
	 * Initialization includes deleting all tables, creating all tables and
	 * filling up tables with some general data.
	 *
	 * @throws DataManagerException when connection is not set yet
	 */
	public void initDatabase() throws DataManagerException {
		tryConnection();

		// TODO here will be run initialize script
	}

	/**
	 * Method executes sql query on database server. This method is used for
	 * SELECT queries. After successful execution method returns ResultSet which
	 * contains all results returned from the server.
	 *
	 * @param sqlQuery string of the query, which will be sent
	 * @return ResultSet, which contains all results returned from the server
	 * @throws DataManagerException when connection is not set yet, or when some
	 * sql exception occurs during execution
	 */
	private ResultSet createDatabaseQuery(String sqlQuery) throws DataManagerException {
		tryConnection();

		try {
			if (statement == null) {
				statement = connection.createStatement();
			}

			resultSet = statement.executeQuery(sqlQuery);
		}
		catch (SQLException ex) {
			throw new DataManagerException("SQLException: " + ex.getMessage());
		}

		return resultSet;
	}

	/**
	 * Method executes update at the database. This method is used for CREATE, DROP,
	 * UPDATE, INSERT, DELETE clauses.
	 *
	 * @param sqlUpdate string of the update, which will be sent
	 * @throws DataManagerException when connection is not set yet, or when some
	 * sql exception occurs during execution
	 */
	private void createDatabaseUpdate(String sqlUpdate) throws DataManagerException {
		tryConnection();

		try {
			if (statement == null) {
				statement = connection.createStatement();
			}

			statement.executeUpdate(sqlUpdate);
		}
		catch (SQLException ex) {
			throw new DataManagerException("SQLException: " + ex.getMessage());
		}
	}

	/**
	 * Method executes update at the database with prepared statement. This method is used for CREATE, DROP,
	 * UPDATE, INSERT, DELETE clauses.
	 *
	 * @param sqlUpdate string of the update, which will be sent with prepared statement
	 * @param data data, which will be filled into prepared statement in right order
	 * @throws DataManagerException when connection is not set yet, or when some
	 * sql exception occurs during execution
	 */
	private void createDatabaseUpdatePrepared(String sqlUpdate, ORAData... data) throws DataManagerException {
		tryConnection();

		try {
			OraclePreparedStatement preparedStatement = (OraclePreparedStatement) connection.prepareStatement(sqlUpdate);

			int i = 1;
			for(ORAData actualData : data) {
				preparedStatement.setORAData(i++, actualData);
			}

			preparedStatement.executeUpdate();
			preparedStatement.close();
		}
		catch (SQLException ex) {
			throw new DataManagerException("SQLException: " + ex.getMessage());
		}
	}

	/**
	 * Method checks if database connection is made.
	 *
	 * @throws DataManagerException when database connection is not made
	 */
	private void tryConnection() throws DataManagerException {
		if(this.connection == null) {
			throw new DataManagerException("Database is not connected!");
		}
	}

	/**
	 * Closes connection if opened.
	 */
	public void disconnectDatabase() {
		if(resultSet != null) {
			try {
				resultSet.close();
				resultSet = null;
				Logger.createLog(Logger.DEBUG_LOG, "ResultSet closed!");
			} catch (SQLException e) {
				Logger.createLog(Logger.ERROR_LOG, "Can not close resultSet!");
			}
		}

		if(statement != null) {
			try {
				statement.close();
				statement = null;
				Logger.createLog(Logger.DEBUG_LOG, "Statement closed!");
			} catch (SQLException e) {
				Logger.createLog(Logger.ERROR_LOG, "Can not close statement!");
			}
		}

		if(connection != null) {
			try {
				connection.close();
				connection = null;
				Logger.createLog(Logger.DEBUG_LOG, "DB connection closed!");
			} catch (SQLException e) {
				Logger.createLog(Logger.ERROR_LOG, "Can not close connection!");
			}
		}
	}

	/**
	 * Method returns instance of the DataManager object, which was
	 * instantiated at the application start. This way singleton pattern
	 * is used.
	 *
	 * @return instance of the DataManager
	 */
	public synchronized static DataManager getInstance() {
		return instance;
	}
}
