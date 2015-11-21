package cz.vutbr.fit.pdb.ateam.adapter;

import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.exception.ModelException;
import cz.vutbr.fit.pdb.ateam.model.BaseModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectTypeModel;
import cz.vutbr.fit.pdb.ateam.utils.Logger;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.pool.OracleDataSource;
import oracle.spatial.geometry.JGeometry;
import oracle.sql.ORAData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Class for communicating with the database.
 * <p/>
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

	private ArrayList<SpatialObjectModel> spatialObjects;
	// TODO spatialObjectTypes

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

	/**
	 * Method connects to the database and save connection for later use.
	 * Database's URL is fixed.
	 *
	 * @param userName user name
	 * @param password user password
	 * @throws DataManagerException when can not connect to the database
	 */
	public void connectDatabase(String userName, String password) throws DataManagerException {
		if (userName == null || password == null)
			throw new DataManagerException("connectDatabase: Null userName or password received");

		try {
			OracleDataSource ods = new OracleDataSource();

			ods.setURL("jdbc:oracle:thin:@gort.fit.vutbr.cz:1521:dbgort");
			ods.setUser(userName);
			ods.setPassword(password);

			this.connection = ods.getConnection();

		} catch (SQLException sqlEx) {
			throw new DataManagerException("connectDatabase: SQLException: " + sqlEx.getMessage(), sqlEx.getErrorCode());
		}
	}

	/**
	 * Closes connection if opened.
	 */
	public void disconnectDatabase() {
		if (connection != null) {
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
	 * Method checks if database connection is made.
	 *
	 * @throws DataManagerException when database connection is not made
	 */
	private void tryConnection() throws DataManagerException {
		if (this.connection == null) {
			throw new DataManagerException("Database is not connected!");
		}
	}

	// --------------------------------------------
	// ------------- METHODS FOR ACCESSING DATABASE
	// --------------------------------------------

	/**
	 * Method executes sql query on database server. This method is used for
	 * SELECT queries. After successful execution method returns ResultSet which
	 * contains all results returned from the server.
	 *
	 * @param sqlQuery string of the query, which will be sent
	 * @return ResultSet, which contains all results returned from the server
	 * @throws DataManagerException when connection is not set yet, or when some
	 *                              sql cz.vutbr.fit.pdb.ateam.exception occurs during execution
	 */
	private ResultSet createDatabaseQuery(String sqlQuery) throws DataManagerException {
		if (sqlQuery == null) throw new DataManagerException("createDatabaseQuery: Null sqlQuery received!");

		tryConnection();

		ResultSet resultSet;
		Statement statement;

		try {
			statement = connection.createStatement();

			Logger.createLog(Logger.DEBUG_LOG, "Sending query: " + sqlQuery);

			resultSet = statement.executeQuery(sqlQuery);
		} catch (SQLException ex) {
			throw new DataManagerException("createDatabaseQuery: SQLException: " + ex.getMessage());
		}

		return resultSet;
	}

	/**
	 * Method executes update at the database. This method is used for CREATE, DROP,
	 * UPDATE, INSERT, DELETE clauses.
	 *
	 * @param sqlUpdate string of the update, which will be sent
	 * @throws DataManagerException when connection is not set yet, or when some
	 *                              sql cz.vutbr.fit.pdb.ateam.exception occurs during execution
	 */
	private void createDatabaseUpdate(String sqlUpdate) throws DataManagerException {
		if (sqlUpdate == null) throw new DataManagerException("createDatabaseUpdate: Null sqlUpdate received!");

		tryConnection();

		Statement statement;

		try {
			statement = connection.createStatement();

			Logger.createLog(Logger.DEBUG_LOG, "Sending update: " + sqlUpdate);

			statement.executeUpdate(sqlUpdate);
			statement.close();
		} catch (SQLException ex) {
			throw new DataManagerException("createDatabaseUpdate: SQLException: " + ex.getMessage());
		}
	}

	/**
	 * Method executes update at the database with prepared statement. This method is used for CREATE, DROP,
	 * UPDATE, INSERT, DELETE clauses.
	 *
	 * @param sqlUpdate string of the update, which will be sent with prepared statement
	 * @param data      data, which will be filled into prepared statement in right order
	 * @throws DataManagerException when connection is not set yet, or when some
	 *                              sql cz.vutbr.fit.pdb.ateam.exception occurs during execution
	 */
	private void createDatabaseUpdatePrepared(String sqlUpdate, ORAData... data) throws DataManagerException {
		if (sqlUpdate == null) throw new DataManagerException("createDatabaseUpdatePrepared: Null sqlUpdate received!");

		tryConnection();

		try {
			OraclePreparedStatement preparedStatement = (OraclePreparedStatement) connection.prepareStatement(sqlUpdate);

			int i = 1;
			for (ORAData actualData : data) {
				preparedStatement.setORAData(i++, actualData);
			}

			Logger.createLog(Logger.DEBUG_LOG, "Sending update: " + sqlUpdate);

			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException ex) {
			throw new DataManagerException("createDatabaseUpdatePrepared: SQLException: " + ex.getMessage());
		}
	}


	// ---------------------------------------------------
	// ------------- METHODS FOR MANIPULATING MODELS IN DB
	// ---------------------------------------------------

	/**
	 * Allows delete any model inherited from BaseModel by ID
	 *
	 * @param model Model which will be deleted (e.g. SpatialObjectModel)
	 * @throws DataManagerException
	 */
	public void deleteModel(BaseModel model) throws DataManagerException {
		String sql = "DELETE FROM " + model.getTableName() + " WHERE ID = " + model.getId();
		createDatabaseUpdate(sql);
	}

	// -----------------------------------------
	// ------------- METHODS FOR SPATIAL OBJECTS
	// -----------------------------------------

	/**
	 * Method parses all needed data from SpacialObject and builds sql query, which is sent
	 * at the server with the createDatabaseUpdate() method.
	 *
	 * @param spatialObject object, which is filled up with all mandatory fields
	 * @throws DataManagerException when some mandatory field is missing or when exception
	 *                              from createDatabaseUpdate() is received
	 * @return id of save model (0 if not successfull, > 0 if ok)
	 */
	public long saveSpatial(SpatialObjectModel spatialObject) throws DataManagerException {
		if (spatialObject == null) {
			throw new DataManagerException("saveSpatial: Null spatialObject received!");
		}

		if (!spatialObject.isChanged()) {
			return 0;
		}

		if (spatialObject.getType() == null) {
			throw new DataManagerException("saveSpatial: Null spatialObject's Type!");
		}
		if (spatialObject.getGeometry() == null) {
			throw new DataManagerException("saveSpatial: Null spatialObject's Geometry!");
		}

		String sqlPrep;

		if (spatialObject.isNew()) {
			sqlPrep = "INSERT INTO Spatial_Objects (Name, Type, Geometry) VALUES(?, ?, ?)";
		} else {
			sqlPrep = "UPDATE Spatial_Objects SET Name = ?, Type = ?, Geometry = ? WHERE ID = ?";
		}

		try {
			OraclePreparedStatement preparedStatement = (OraclePreparedStatement) connection.prepareStatement(sqlPrep);
			preparedStatement.setString(1, spatialObject.getName());
			preparedStatement.setLong(2, spatialObject.getType().getId());
			preparedStatement.setObject(3, JGeometry.store(connection, spatialObject.getGeometry()));

			// TODO when inserted must delete old model and insert new because id is set only in local model not in list

			if (!spatialObject.isNew()) {
				preparedStatement.setLong(4, spatialObject.getId());
				preparedStatement.execute();
			}
			else {
				preparedStatement.execute();

				// TODO when inserting -> should reload that object (because od ID)
				ResultSet rs = preparedStatement.getGeneratedKeys();
				if (rs.next()) {
					//this is way we can get last inserted ID
					long last_inserted_id = rs.getLong(1);
					spatialObject.setId(last_inserted_id);
					Logger.createLog(Logger.DEBUG_LOG, String.format("nove id %d", spatialObject.getId()));
				}
			}

			spatialObject.setIsChanged(false);
			return spatialObject.getId();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * Method gets all spatial objects saved in the database. Spatial object
	 * represents one building at the zoo map.
	 *
	 * @return all zoo buildings saved in the database
	 * @throws DataManagerException when cz.vutbr.fit.pdb.ateam.exception from createDatabaseQuery() is received or
	 *                              when SQLException is caught
	 */
	public ArrayList<SpatialObjectModel> reloadAllSpatialObjects() throws DataManagerException {
		ArrayList<SpatialObjectModel> spatialObjects = new ArrayList<>();

		String sqlQuery = "SELECT * FROM Spatial_Objects";
		ResultSet resultSet = createDatabaseQuery(sqlQuery);

		try {
			while (resultSet.next()) {
				try {
					Long id = resultSet.getLong("ID");
					Long typeID = resultSet.getLong("Type");
					String name = resultSet.getString("Name");
					SpatialObjectTypeModel spatialType = getSpatialObjectType(typeID);

					byte[] rawGeometry = resultSet.getBytes("Geometry");
					spatialObjects.add(SpatialObjectModel.loadFromDB(id, name, spatialType, rawGeometry));
				} catch (ModelException mE) {
					Logger.createLog(Logger.ERROR_LOG, mE.getMessage());
				}
			}
		} catch (SQLException ex) {
			throw new DataManagerException("reloadAllSpatialObjects: SQLException: " + ex.getMessage());
		} catch (DataManagerException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new DataManagerException("reloadAllSpatialObjects: JGeometry exception: " + ex.getMessage());
		}

		this.spatialObjects = spatialObjects;

		// TODO void
		return spatialObjects;
	}

	/**
	 * Is used to get cached data from SpatialObjects Table.
	 *
	 * @return list of SpatialObjects
	 */
	public ArrayList<SpatialObjectModel> getSpatialObjects() {
		return this.spatialObjects;
	}



	/**
	 * Method returns spatial object type with specific ID. If type with this ID
	 * does not exists, method returns null pointer.
	 *
	 * @param typeID ID of the type in the database
	 * @return SpatialObjectTypeModel of the concrete type or null
	 * @throws DataManagerException when cz.vutbr.fit.pdb.ateam.exception from createDatabaseQuery() is received
	 */
	public SpatialObjectTypeModel getSpatialObjectType(Long typeID) throws DataManagerException {
		if (typeID == null) throw new DataManagerException("getType: Null typeID received");

		SpatialObjectTypeModel spatialObjectType = null;

		String sqlQuery = "SELECT * FROM Spatial_Object_Types WHERE ID=" + typeID.toString();
		ResultSet resultSet = createDatabaseQuery(sqlQuery);

		try {
			if (resultSet.next()) {
				Long id = resultSet.getLong("ID");
				String name = resultSet.getString("Name");
				String colorHexString = resultSet.getString("Color");
				spatialObjectType = new SpatialObjectTypeModel(id, name, colorHexString);
			}
		} catch (SQLException ex) {
			throw new DataManagerException("getType: SQLException: " + ex.getMessage());
		}

		return spatialObjectType;
	}

	/**
	 * Method returns all spatial objects types from the database.
	 *
	 * @return Set of the SpacialObjectType, which contains all object types saved in the database
	 * @throws DataManagerException when cz.vutbr.fit.pdb.ateam.exception from createDatabaseQuery() is received
	 */
	public ArrayList<SpatialObjectTypeModel> getAllSpatialObjectTypes() throws DataManagerException {
		ArrayList<SpatialObjectTypeModel> spacialTypes = new ArrayList<>();

		String sqlQuery = "SELECT * FROM Spatial_Object_Types";
		ResultSet resultSet = createDatabaseQuery(sqlQuery);

		try {
			while (resultSet.next()) {
				Long id = resultSet.getLong("ID");
				String name = resultSet.getString("Name");
				String colorHexString = resultSet.getString("Color");
				spacialTypes.add(new SpatialObjectTypeModel(id, name, colorHexString));
			}
		} catch (SQLException ex) {
			throw new DataManagerException("getAllSpatialObjectTypes: SQLException: " + ex.getMessage());
		}

		return spacialTypes;
	}
}
