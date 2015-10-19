package adapter;

import exception.DataManagerException;
import model.SpatialObject;
import model.SpatialObjectType;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.pool.OracleDataSource;
import oracle.spatial.geometry.JGeometry;
import oracle.sql.ORAData;
import utils.Logger;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

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
	private static final int INSERT = 0;
	private static final int UPDATE = 1;

	private Connection connection;

	/**
	 * Method connects to the database and save connection for later use.
	 * Database's URL is fixed.
	 *
	 * @param userName user name
	 * @param password user password
	 * @throws DataManagerException when can not connect to the database
	 */
	public void connectDatabase(String userName, String password) throws DataManagerException {
		if(userName == null || password == null) throw new DataManagerException("connectDatabase: Null userName or password received");

		try {
			OracleDataSource ods = new OracleDataSource();

			ods.setURL("jdbc:oracle:thin:@gort.fit.vutbr.cz:1521:dbgort");
			ods.setUser(userName);
			ods.setPassword(password);

			this.connection = ods.getConnection();

		} catch (SQLException sqlEx) {
			throw new DataManagerException("connectDatabase: SQLException: " + sqlEx.getMessage());
		}
	}

	/**
	 * Closes connection if opened.
	 */
	public void disconnectDatabase() {
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
	 * Method transforms SpatialObject into sql UPDATE query and sends database update
	 * with that query.
	 *
	 * @param spatialObject object, which is filled up with all mandatory fields
	 * @throws DataManagerException when some mandatory field is missing or when exception
	 * from createDatabaseUpdate() is received
	 */
	public void updateSpatialObject(SpatialObject spatialObject) throws DataManagerException {
		updateOrInsertSpatialObject(spatialObject, UPDATE);
	}

	/**
	 * Method transforms SpatialObject into sql INSERT query and sends database insert
	 * with that query.
	 *
	 * @param spatialObject object, which is filled up with all mandatory fields
	 * @throws DataManagerException when some mandatory field is missing or when exception
	 * from createDatabaseUpdate() is received
	 */
	public void insertSpatialObject(SpatialObject spatialObject) throws DataManagerException {
		updateOrInsertSpatialObject(spatialObject, INSERT);
	}

	/**
	 * Method gets all spatial objects saved in the database. Spatial object
	 * represents one building at the zoo map.
	 *
	 * @return all zoo buildings saved in the database
	 * @throws DataManagerException when exception from createDatabaseQuery() is received or
	 * when SQLException is caught
	 */
	public ArrayList<SpatialObject> getAllSpatialObjects() throws DataManagerException {
		ArrayList<SpatialObject> spatialObjects = new ArrayList<>();

		String sqlQuery = "SELECT ID, Type, Geometry FROM Spatial_Objects";
		ResultSet resultSet = createDatabaseQuery(sqlQuery);

		try {
			while (resultSet.next()) {
				Long id = resultSet.getLong("ID");

				Long typeID = resultSet.getLong("Type");
				SpatialObjectType type = getSpatialObjectType(typeID);

				byte[] image = resultSet.getBytes("Geometry");
				JGeometry jGeometry = JGeometry.load(image);
				Shape shape = jGeometry2Shape(jGeometry);

				SpatialObject spatialObject = new SpatialObject(id, type, jGeometry, shape);
				spatialObjects.add(spatialObject);
			}
		} catch (SQLException ex) {
			throw new DataManagerException("getAllSpatialObjects: SQLException: " + ex.getMessage());
		} catch (DataManagerException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new DataManagerException("getAllSpatialObjects: JGeometry exception: " + ex.getMessage());
		}

		return spatialObjects;
	}

	private Shape jGeometry2Shape(JGeometry jGeometry) throws DataManagerException {
		if(jGeometry == null) throw new DataManagerException("jGeometry2Shape: Null jGeometry received!");

		Shape shape;

		switch (jGeometry.getType()) {
			case JGeometry.GTYPE_POLYGON:
				shape = jGeometry.createShape();
				break;
			default:
				throw new DataManagerException("jGeometry2Shape: Can not convert jGeometry!");
		}
		return shape;
	}

	/**
	 * Method returns spatial object type with specific ID. If type with this ID
	 * does not exists, method returns null pointer.
	 *
	 * @param typeID ID of the type in the database
	 * @return SpatialObjectType of the concrete type or null
	 * @throws DataManagerException when exception from createDatabaseQuery() is received
	 */
	public SpatialObjectType getSpatialObjectType(Long typeID) throws DataManagerException {
		if(typeID == null) throw new DataManagerException("getSpatialObjectType: Null typeID received");

		SpatialObjectType spatialObjectType = null;

		String sqlQuery = "SELECT ID, Type FROM Spatial_Object_Types WHERE ID=" + typeID.toString();
		ResultSet resultSet = createDatabaseQuery(sqlQuery);

		try {
			if(resultSet.next()) {

				Long id = resultSet.getLong("ID");
				String type = resultSet.getString("Type");

				spatialObjectType = new SpatialObjectType(id, type);
			}
		} catch (SQLException ex) {
			throw new DataManagerException("getSpatialObjectType: SQLException: " + ex.getMessage());
		}

		return spatialObjectType;
	}

	/**
	 * Method returns all spatial objects types from the database.
	 *
	 * @return Set of the SpacialObjectType, which contains all object types saved in the database
	 * @throws DataManagerException when exception from createDatabaseQuery() is received
	 */
	public ArrayList<SpatialObjectType> getAllSpatialObjectTypes() throws DataManagerException {
		ArrayList<SpatialObjectType> spatialObjectTypes = new ArrayList<>();

		String sqlQuery = "SELECT ID, Type FROM Spatial_Object_Types";
		ResultSet resultSet = createDatabaseQuery(sqlQuery);

		try {
			while (resultSet.next()) {
				Long id = resultSet.getLong("ID");
				String type = resultSet.getString("Type");
				SpatialObjectType spatialObjectType = new SpatialObjectType(id, type);
				spatialObjectTypes.add(spatialObjectType);
			}
		} catch (SQLException ex) {
			throw new DataManagerException("getAllSpatialObjectTypes: SQLException: " + ex.getMessage());
		}

		return spatialObjectTypes;
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
		if(sqlQuery == null) throw new DataManagerException("createDatabaseQuery: Null sqlQuery received!");

		tryConnection();

		ResultSet resultSet;
		Statement statement;

		try {
			statement = connection.createStatement();

			Logger.createLog(Logger.DEBUG_LOG, "Sending query: " + sqlQuery);

			resultSet = statement.executeQuery(sqlQuery);
		}
		catch (SQLException ex) {
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
	 * sql exception occurs during execution
	 */
	private void createDatabaseUpdate(String sqlUpdate) throws DataManagerException {
		if(sqlUpdate == null) throw new DataManagerException("createDatabaseUpdate: Null sqlUpdate received!");

		tryConnection();

		Statement statement;

		try {
			statement = connection.createStatement();

			Logger.createLog(Logger.DEBUG_LOG, "Sending update: " + sqlUpdate);

			statement.executeUpdate(sqlUpdate);
			statement.close();
		}
		catch (SQLException ex) {
			throw new DataManagerException("createDatabaseUpdate: SQLException: " + ex.getMessage());
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
		if(sqlUpdate == null) throw new DataManagerException("createDatabaseUpdatePrepared: Null sqlUpdate received!");

		tryConnection();

		try {
			OraclePreparedStatement preparedStatement = (OraclePreparedStatement) connection.prepareStatement(sqlUpdate);

			int i = 1;
			for(ORAData actualData : data) {
				preparedStatement.setORAData(i++, actualData);
			}

			Logger.createLog(Logger.DEBUG_LOG, "Sending update: " + sqlUpdate);

			preparedStatement.executeUpdate();
			preparedStatement.close();
		}
		catch (SQLException ex) {
			throw new DataManagerException("createDatabaseUpdatePrepared: SQLException: " + ex.getMessage());
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
	 * Method parses all needed data from SpacialObject and builds sql query, which is sent
	 * at the server with the createDatabaseUpdate() method.
	 *
	 * @param spatialObject object, which is filled up with all mandatory fields
	 * @param action can be UPDATE or INSERT
	 * @throws DataManagerException when some mandatory field is missing or when exception
	 * from createDatabaseUpdate() is received
	 */
	private void updateOrInsertSpatialObject(SpatialObject spatialObject, int action) throws DataManagerException {
		if(spatialObject == null) throw new DataManagerException("updateOrInsertSpatialObject: Null spatialObject received!");

		String id, typeId, sdoGType, sdoSrid, sdoPoint, sdoElemInfo, sdoOrdinates, sdoGeometry, sql;

		if(action == UPDATE && spatialObject.getId() == null) {
			throw new DataManagerException("updateOrInsertSpatialObject: Null spatialObject's ID!");
		} else {
			id = spatialObject.getId().toString();
		}

		if(spatialObject.getType() == null) {
			throw new DataManagerException("updateOrInsertSpatialObject: Null spatialObject's Type!");
		} else {
			typeId = spatialObject.getType().getId().toString();
		}

		if(spatialObject.getGeometry() == null) {
			throw new DataManagerException("updateOrInsertSpatialObject: Null spatialObject's Geometry!");
		} else {
			JGeometry jGeometry = spatialObject.getGeometry();

			sdoGType = "";
			sdoGType += String.valueOf(jGeometry.getDimensions());
			sdoGType += String.valueOf(jGeometry.getLRMDimension());
			sdoGType += '0';
			sdoGType += String.valueOf(jGeometry.getType());

			sdoSrid = (jGeometry.getSRID() == 0 ? "NULL" : String.valueOf(jGeometry.getSRID()));

			double[] point = jGeometry.getPoint();
			sdoPoint = (point == null || point.length < 2 ? "NULL" : "SDO_POINT_TYPE(" + point[0] + ", " + point[1] + ", NULL)");

			int[] elemInfo = jGeometry.getElemInfo();
			sdoElemInfo = (elemInfo == null ? "NULL" : "SDO_ELEM_INFO_ARRAY(" + Arrays.toString(elemInfo)) + ")";
			sdoElemInfo = sdoElemInfo.replace("[", "");
			sdoElemInfo = sdoElemInfo.replace("]", "");

			double[] ordinates = jGeometry.getOrdinatesArray();
			sdoOrdinates = (ordinates == null ? "NULL" : "SDO_ORDINATE_ARRAY(" + Arrays.toString(ordinates)) + ")";
			sdoOrdinates = sdoOrdinates.replace("[", "");
			sdoOrdinates = sdoOrdinates.replace("]", "");
			
			sdoGeometry = "SDO_GEOMETRY("+ sdoGType +", "+ sdoSrid +", "+ sdoPoint +", "+ sdoElemInfo +", "+ sdoOrdinates +")";
		}

		switch (action) {
			case INSERT:
				sql = "INSERT INTO Spatial_Objects (Type, Geometry) VALUES (";
				sql += typeId + ", ";
				sql += sdoGeometry +")";
				break;

			case UPDATE:
				sql = "UPDATE Spatial_Objects ";
				sql += "SET Type=" + typeId + ", ";
				sql += "Geometry=" + sdoGeometry + " ";
				sql += "WHERE ID=" + id;
				break;

			default:
				throw new DataManagerException("updateOrInsertSpatialObject: Unknown action [" + action + "]!");
		}

		createDatabaseUpdate(sql);
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
