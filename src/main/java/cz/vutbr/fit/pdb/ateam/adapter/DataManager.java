package cz.vutbr.fit.pdb.ateam.adapter;

import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.exception.ModelException;
import cz.vutbr.fit.pdb.ateam.model.BaseModel;
import cz.vutbr.fit.pdb.ateam.model.employee.EmployeeModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectTypeModel;
import cz.vutbr.fit.pdb.ateam.tasks.AsyncTask;
import cz.vutbr.fit.pdb.ateam.utils.Logger;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.pool.OracleDataSource;
import oracle.spatial.geometry.JGeometry;
import oracle.sql.ORAData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;
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
	private ArrayList<SpatialObjectTypeModel> spatialObjectTypes;
	private ArrayList<EmployeeModel> employees;

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
	 * Method connects to the database and saveModel connection for later use.
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
			throw new DataManagerException("createDatabaseQuery: SQLException: " + ex.getMessage(), ex.getErrorCode());
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

	/**
	 * Executes prepared statement for insert and sets latest inserted id to the specified model.
	 * NOTE: Turns off and on autoCommit!
	 *
	 * @param preparedStatement will be executed (must be INSERT query)
	 * @param model             Gets sequence name based on {@link BaseModel#getTableName()}_seq (this sequence MUST BE IN DATABASE!)
	 * @throws SQLException
	 */
	private void executeInsertAndSetId(PreparedStatement preparedStatement, BaseModel model) throws SQLException {
		connection.setAutoCommit(false);
		preparedStatement.executeUpdate();
		ResultSet getIdResultSet = connection.createStatement().executeQuery("SELECT " + model.getTableName() + "_seq.currval FROM dual");
		if (getIdResultSet.next()) {
			model.setId(getIdResultSet.getLong(1));
		}
		connection.commit();
		connection.setAutoCommit(true);
	}

	/**
	 * Executes prepared statement for insert and sets latest inserted id to the specified model.
	 * NOTE: Turns off and on autoCommit!
	 *
	 * @param preparedStatement will be executed (must be INSERT query)
	 * @param model             Gets sequence name based on {@link BaseModel#getTableName()}_seq (this sequence MUST BE IN DATABASE!)
	 * @throws SQLException
	 */
	private void executeTemporalInsertAndSetId(PreparedStatement preparedStatement, EmployeeModel model) throws SQLException {
		connection.setAutoCommit(false);
		preparedStatement.executeUpdate();
		ResultSet getIdResultSet = connection.createStatement().executeQuery("SELECT Employees_Shift_seq.currval FROM dual");
		if (getIdResultSet.next()) {
			model.setShiftID(getIdResultSet.getLong(1));
		}
		connection.commit();
		connection.setAutoCommit(true);
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
	public synchronized void deleteModel(BaseModel model) throws DataManagerException {
		if (!model.isNew()) {
			String sql = "DELETE FROM " + model.getTableName() + " WHERE ID = " + model.getId();
			createDatabaseUpdate(sql);
		}

		// TODO should be specified from which cache will be deleted
		if (model instanceof SpatialObjectModel) {
			spatialObjects.remove(model);
		}
	}

	/**
	 * // TODO need to specify which type of BaseModel can be saved
	 *
	 * @param model any model extendind BaseModel which is implemented here
	 * @return success
	 * @throws DataManagerException
	 */
	public boolean saveModel(BaseModel model) throws DataManagerException {

		if (model == null) {
			throw new DataManagerException("saveModel(): Null model received!");
		}

		if (!model.isChanged()) {
			System.out.println("AKCEEEEEEEEEEEEEEEEEEEEEEEEE");
			return false;
		}


		if (model instanceof SpatialObjectModel) {
			return saveSpatial((SpatialObjectModel) model);
		}else if (model instanceof  EmployeeModel){
			return saveEmployee((EmployeeModel) model);
		}
		// TODO here specify model's saving methods!
		else {
			throw new DataManagerException("saveModel(): Unsupported model to save");
		}
	}

	private boolean saveEmployee(EmployeeModel employee) {
		try {
			String sqlPrep = null;
			Boolean isNewEmployee = employee.isNew();

			if (isNewEmployee){
				sqlPrep = "INSERT INTO Employees (Name, Surname) VALUES(?, ?)";
			}
			else{
				sqlPrep = "UPDATE Employees SET Name = ?, Surname = ? WHERE ID = ?";
			}

			PreparedStatement preparedStatement = connection.prepareStatement(sqlPrep);
			preparedStatement.setString(1, employee.getName());
			preparedStatement.setString(2, employee.getSurname());

			Logger.createLog(Logger.DEBUG_LOG, "Sending query: " + sqlPrep + " | name = '" + employee.getName() + "', surname = '" + employee.getSurname() + "', id = '" + employee.getId() + "'");
			if (isNewEmployee) {
				this.executeInsertAndSetId(preparedStatement, employee);
			} else {
				preparedStatement.setLong(3, employee.getId());
				preparedStatement.executeUpdate();
			}

			saveEmployeeTemporalData(employee, isNewEmployee);

			employee.setIsChanged(false);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void saveEmployeeTemporalData(EmployeeModel employee, boolean isNewEmployee) throws SQLException {
		String sqlPrepTemp = null;
		if (isNewEmployee)
			sqlPrepTemp = "INSERT INTO Employees_Shift (EmplID, Location, dFrom, dTo) VALUES(?, ?, ?, ?)";
		else {
			//TODO implement
		}

		java.sql.Date dateFrom = new java.sql.Date(employee.getDateFrom().getTime());
		java.sql.Date dateTo = new java.sql.Date(employee.getDateTo().getTime());

		PreparedStatement preparedTemporalStatement = connection.prepareStatement(sqlPrepTemp);
		preparedTemporalStatement.setLong(1, employee.getId());
		preparedTemporalStatement.setLong(2, employee.getLocation());
		preparedTemporalStatement.setDate(3, dateFrom);
		preparedTemporalStatement.setDate(4, dateTo);

		Logger.createLog(Logger.DEBUG_LOG, "Sending query: " + sqlPrepTemp + " | name = '" + employee.getName() + "', surname = '" + employee.getSurname() + "', id = '" + employee.getId() + "'");
		if (isNewEmployee) {
			this.executeTemporalInsertAndSetId(preparedTemporalStatement, employee);
		} else {
		}
	}


	// -----------------------------------------
	// ------------- METHODS FOR SPATIAL OBJECTS
	// -----------------------------------------

	/**
	 * Method parses all needed data from SpacialObject and builds sql query, which is sent
	 * at the server with the createDatabaseUpdate() method.
	 *
	 * @param spatialObject object, which is filled up with all mandatory fields
	 * @return id of saveModel model (0 if not successfull, > 0 if ok)
	 * @throws DataManagerException when some mandatory field is missing or when exception
	 *                              from createDatabaseUpdate() is received
	 */
	public boolean saveSpatial(SpatialObjectModel spatialObject) throws DataManagerException {
		if (spatialObject.getType() == null) {
			throw new DataManagerException("saveSpatial: Null spatialObject's Type!");
		}
		if (spatialObject.getGeometry() == null) {
			throw new DataManagerException("saveSpatial: Null spatialObject's Geometry!");
		}

		try {
			String sqlPrep = null;
			sqlPrep = spatialObject.isNew() ?
					"INSERT INTO Spatial_Objects (Name, Type, Geometry) VALUES(?, ?, ?)" :
					"UPDATE Spatial_Objects SET Name = ?, Type = ?, Geometry = ? WHERE ID = ?";

			PreparedStatement preparedStatement = connection.prepareStatement(sqlPrep);
			preparedStatement.setString(1, spatialObject.getName());
			preparedStatement.setLong(2, spatialObject.getType().getId());
			preparedStatement.setObject(3, JGeometry.store(connection, spatialObject.getGeometry()));

			Logger.createLog(Logger.DEBUG_LOG, "Sending query: " + sqlPrep + " | name = '" + spatialObject.getName() + "', type = '" + spatialObject.getType().getId() + "', id = '" + spatialObject.getId() + "'");
			if (spatialObject.isNew()) {
				this.executeInsertAndSetId(preparedStatement, spatialObject);
			} else {
				preparedStatement.setLong(4, spatialObject.getId());
				preparedStatement.executeUpdate();
			}

			spatialObject.setIsChanged(false);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Method gets all spatial objects saved in the database. Spatial object
	 * represents one building at the zoo map.
	 *
	 * @return all zoo buildings saved in the database
	 * @throws DataManagerException when exception from createDatabaseQuery() is received or
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
	 * does not exists, method returns null pointer. Searching in cached types.
	 *
	 * @param typeID ID of the type in the database
	 * @return SpatialObjectTypeModel of the concrete type or null
	 * @throws DataManagerException when exception from createDatabaseQuery() is received
	 */
	public SpatialObjectTypeModel getSpatialObjectType(Long typeID) throws DataManagerException {
		if (typeID == null) throw new DataManagerException("getType: Null typeID received");

		if(spatialObjectTypes == null) reloadAllSpatialObjectTypes();

		for(SpatialObjectTypeModel type: spatialObjectTypes) {
			if(type.getId().equals(typeID)) return type;
		}

		return null;
	}

	/**
	 * Method returns all spatial objects types from the database.
	 *
	 * @return Set of the SpacialObjectType, which contains all object types saved in the database
	 * @throws DataManagerException when exception from createDatabaseQuery() is received
	 */
	public ArrayList<SpatialObjectTypeModel> reloadAllSpatialObjectTypes() throws DataManagerException {
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

		this.spatialObjectTypes = spacialTypes;

		// TODO void???
		return spacialTypes;
	}

	/**
	 * Is used to get cached data from SpatialObjectTypes Table.
	 *
	 * @return list of SpatialObjectTypes
	 */
	public ArrayList<SpatialObjectTypeModel> getSpatialObjectTypes() {
		return spatialObjectTypes;
	}


	/**
	 * Method returns all spatial objects types from the database.
	 *
	 * @return Set of the SpacialObjectType, which contains all object types saved in the database
	 * @throws DataManagerException when exception from createDatabaseQuery() is received
	 */
	public ArrayList<EmployeeModel> reloadAllEmployees() throws DataManagerException {
		ArrayList<EmployeeModel> employees = new ArrayList<>();

		String sqlQuery = "SELECT * FROM Employees";
		ResultSet resultSet = createDatabaseQuery(sqlQuery);

		try {
			while (resultSet.next()) {
				Long id = resultSet.getLong("ID");
				String name = resultSet.getString("Name");
				String surname = resultSet.getString("Surname");
				Long location = Long.valueOf(4);//resultSet.getLong("Location");
				employees.add(new EmployeeModel(id, name, surname, location, null, null));
			}
		} catch (SQLException ex) {
			throw new DataManagerException("getAllEmployees: SQLException: " + ex.getMessage());
		}

		this.employees = employees;

		// TODO void???
		return employees;
	}


	// -----------------------------------------
	// ------------- METHODS FOR EMPLOYEES -----
	// -----------------------------------------



	/**
	 * Is used to get cached data from Employees Table.
	 *
	 * @return list of Employees
	 */
	public ArrayList<EmployeeModel> getEmployees() {
		return employees;
	}


	public ArrayList<EmployeeModel> getEmployeesAtDate(final Date date) throws DataManagerException {
		final ArrayList<EmployeeModel> employees = new ArrayList<EmployeeModel>();
		AsyncTask asyncTask = new AsyncTask() {
			@Override
			protected void onDone(boolean success) {
				System.out.println("Hotovo");
			}

			@Override
			protected Boolean doInBackground() throws Exception {
				java.sql.Date dateToShow = new java.sql.Date(date.getTime());
				String datum = new SimpleDateFormat("dd-MMM-yyyy").format(date);
				String sqlQuery = "SELECT e.ID as EmployeeID, e.Name, e.Surname, s.Location, s.dFrom, s.dTo " +
						"FROM EMPLOYEES e LEFT JOIN Employees_Shift s ON e.ID = s.EmplId " +
						"WHERE s.dFrom <= '" + datum + "' AND s.dTo >= '" + datum + "'";
//				String sqlQuery = "SELECT * FROM Employees_Shift";
//				String sqlQuery = "SELECT * FROM HAHA";
//				String sqlQuery = "INSERT INTO HAHA (ID) VALUES (1)";
//
//				CREATE TABLE HAHA (
//						ID INT NOT NULL,
//						PRIMARY KEY (ID)
//				);

//				String sqlQuery = "\n" +
//						"SELECT e.*, s.Location, s.dFrom, s.dTo  FROM Employees e\n" +
//						"LEFT JOIN Employees_Shift s on e.ID = s.EmplID\n" +
//						"WHERE s.dFrom <= dateToShow and s.dTo >= dateToShow\n";
				ResultSet resultSet = createDatabaseQuery(sqlQuery);

//				PreparedStatement psSelectRecord = null;
//				psSelectRecord = connection.prepareStatement(sqlQuery);
//				ResultSet resultSet = psSelectRecord.executeQuery();

				System.out.println("dasdasadadasdasdasdasdafasd");
				try {
					while (resultSet.next()) {
						Long id = resultSet.getLong("EmployeeID");
						String name = resultSet.getString("Name");
						String surname = resultSet.getString("Surname");
						Long location = resultSet.getLong("Location");
						Date dateFrom = resultSet.getDate("dFrom");
						Date dateTo = resultSet.getDate("dTo");
						employees.add(new EmployeeModel(id, name, surname, location, dateFrom, dateTo));
					}
				} catch (SQLException e) {
					throw new DataManagerException("getAllEmployeesAtDate: SQLException: " + e.getMessage());
				}
				return true;
			}
		};
		asyncTask.start();
		return employees;
	}
	public ArrayList<EmployeeModel> getEmployeeHistory(final long employeeID) throws DataManagerException {
		final ArrayList<EmployeeModel> employees = new ArrayList<EmployeeModel>();
		AsyncTask asyncTask = new AsyncTask() {
			@Override
			protected void onDone(boolean success) {
				System.out.println("Hotovoodasda");
			}

			@Override
			protected Boolean doInBackground() throws Exception {
				String sqlQuery = "SELECT e.ID as EmployeeID, e.name, e.surname, s.ID as ShiftID, s.Location, s.dFrom, s.dTo " +
						"FROM EMPLOYEES e LEFT JOIN Employees_Shift s ON e.ID = s.EmplId WHERE e.ID = " + employeeID;

				ResultSet resultSet = createDatabaseQuery(sqlQuery);

				try {
					while (resultSet.next()) {
						System.out.println(resultSet.getString("Name"));
						Long emplID = resultSet.getLong("EmployeeID");
						Long shiftID = resultSet.getLong("ShiftID");
						String name = resultSet.getString("Name");
						String surname = resultSet.getString("Surname");
						Long location = resultSet.getLong("Location");
						Date dateFrom = resultSet.getDate("dFrom");
						Date dateTo = resultSet.getDate("dTo");
						employees.add(new EmployeeModel(emplID, name, surname, location, dateFrom, dateTo, shiftID));
					}
				} catch (SQLException e) {
					throw new DataManagerException("getAllEmployeesAtDate: SQLException: " + e.getMessage());
				}
				return true;
			}
		};
		asyncTask.start();
		return employees;
	}
	public SpatialObjectModel getSpatialObjectModelWithID(long id){
		for (SpatialObjectModel model : this.spatialObjects) {
			if (model.getId() == id )
				return model;
		}
		return null;
	}

}
