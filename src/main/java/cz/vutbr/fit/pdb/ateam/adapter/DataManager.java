package cz.vutbr.fit.pdb.ateam.adapter;

import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.exception.ModelException;
import cz.vutbr.fit.pdb.ateam.model.BaseModel;
import cz.vutbr.fit.pdb.ateam.model.animal.AnimalModel;
import cz.vutbr.fit.pdb.ateam.model.employee.EmployeeModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectTypeModel;
import cz.vutbr.fit.pdb.ateam.tasks.AsyncTask;
import cz.vutbr.fit.pdb.ateam.utils.Logger;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.pool.OracleDataSource;
import oracle.ord.im.OrdImage;
import oracle.spatial.geometry.JGeometry;
import oracle.sql.ORAData;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Class for communicating with the database.
 * <p/>
 * All database operations needed in the application are
 * included in this class. Class uses singleton pattern
 * and static method getInstance() should be called
 * instead of the constructor.
 *
 * @author Tomas Hanus
 * @author Tomas Mlynaric
 * @author Jakub Tutko
 */
public class DataManager {
	private static DataManager instance = new DataManager();

	private Connection connection;

	private List<SpatialObjectModel> spatialObjects;
	private List<SpatialObjectTypeModel> spatialObjectTypes;
	private List<EmployeeModel> employees;
	private List<AnimalModel> animals;


	public static final int SQL_FUNCTION_WITHIN_DISTANCE = 1;
	public static final int SQL_FUNCTION_SDO_RELATE = 2;

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
	public void disconnectDatabase() throws DataManagerException {
		if (connection != null) {
			try {
				connection.close();
				connection = null;
				Logger.createLog(Logger.DEBUG_LOG, "DB connection closed!");
			} catch (SQLException e) {
				throw new DataManagerException("SQLException: " + e.getMessage());
			}
		}
	}

	/**
	 * Clears cached objects which will need to be reload again
	 */
	public void clearCache() {
		this.spatialObjectTypes = null;
		this.spatialObjects = null;
		this.employees = null;
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
		} else if (model instanceof SpatialObjectTypeModel) {
			spatialObjectTypes.remove(model);
		} else if (model instanceof EmployeeModel) {
			employees.remove(model);
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
			return false;
		}


		if (model instanceof SpatialObjectModel) {
			return saveSpatial((SpatialObjectModel) model);
		} else if (model instanceof EmployeeModel) {
			return saveEmployee((EmployeeModel) model);
		} else if (model instanceof AnimalModel) {
			return saveAnimal((AnimalModel) model);
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

			if (isNewEmployee) {
				sqlPrep = "INSERT INTO Employees (Name, Surname) VALUES(?, ?)";
			} else {
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

	private boolean saveAnimalOld(AnimalModel animalModel) {
		try {
			String sqlPrep = null;
			Boolean isNewAnimal = animalModel.isNew();

			if (isNewAnimal) {
				sqlPrep = "INSERT INTO Animals (Name, Species) VALUES(?, ?)";
			} else {
				sqlPrep = "UPDATE Animals SET Name = ?, Species = ? WHERE ID = ?";
			}

			PreparedStatement preparedStatement = connection.prepareStatement(sqlPrep);
			preparedStatement.setString(1, animalModel.getName());
			preparedStatement.setString(2, animalModel.getSpecies());

			Logger.createLog(Logger.DEBUG_LOG, "Sending query: " + sqlPrep + " | name = '" + animalModel.getName() + "', species = '" + animalModel.getSpecies() + "', id = '" + animalModel.getId() + "'");
			if (isNewAnimal) {
				this.executeInsertAndSetId(preparedStatement, animalModel);
			} else {
				preparedStatement.setLong(3, animalModel.getId());
				preparedStatement.executeUpdate();
			}

			updateAnimalShifts(animalModel.getId(), animalModel.getDateFrom(), animalModel.getDateTo(), animalModel.getLocation(), animalModel.getWeight());

			animalModel.setIsChanged(false);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void saveEmployeeTemporalData(EmployeeModel employee, boolean isNewEmployee) throws SQLException {
		String sqlPrepTemp = null;
		updateEmployeeShifts(employee.getId(), employee.getDateFrom(), employee.getDateTo(), employee.getLocation());
//		if (isNewEmployee)
//		sqlPrepTemp = "INSERT INTO Employees_Shift (EmplID, Location, dFrom, dTo) VALUES(?, ?, ?, ?)";
//		else {
//			//TODO implement
//		}
//
//		java.sql.Date dateFrom = new java.sql.Date(employee.getDateFrom().getTime());
//		java.sql.Date dateTo = new java.sql.Date(employee.getDateTo().getTime());
//
//		PreparedStatement preparedTemporalStatement = connection.prepareStatement(sqlPrepTemp);
//		preparedTemporalStatement.setLong(1, employee.getId());
//		preparedTemporalStatement.setLong(2, employee.getLocation());
//		preparedTemporalStatement.setDate(3, dateFrom);
//		preparedTemporalStatement.setDate(4, dateTo);
//
//		Logger.createLog(Logger.DEBUG_LOG, "Sending query: " + sqlPrepTemp + " | name = '" + employee.getName() + "', surname = '" + employee.getSpecies() + "', id = '" + employee.getId() + "'");
//		if (isNewEmployee) {
//			this.executeTemporalInsertAndSetId(preparedTemporalStatement, employee);
//		} else {
//		}
	}


	// -----------------------------------------
	// ------------- METHODS FOR SPATIAL OBJECTS
	// -----------------------------------------

	public synchronized void mirrorImage(AnimalModel model) throws DataManagerException {

		String mirrorSQL = ""
				+ "DECLARE "
				+ "obj ORDSYS.ORDImage; "
				+ "BEGIN "
				+ "SELECT photo INTO obj FROM Animals "
				+ "WHERE id = " + model.getId() + " FOR UPDATE; "
				+ "obj.process('mirror'); "
				+ "UPDATE Animals SET photo = obj WHERE id = " + model.getId() + "; "
				+ "COMMIT; "
				+ "EXCEPTION "
				+ "WHEN ORDSYS.ORDImageExceptions.DATA_NOT_LOCAL THEN "
				+ "DBMS_OUTPUT.PUT_LINE('Data is not local'); "
				+ "END; ";

		createDatabaseUpdate(mirrorSQL);

		saveAnimal(model);
	}

	public synchronized ArrayList<AnimalModel> getThreeSimilarImages(AnimalModel sourceModel) throws DataManagerException {
		ArrayList<AnimalModel> modelsList = new ArrayList<>();

		try {
			String selectSQL = "";
			selectSQL += "SELECT destination FROM (SELECT src.id AS source, dst.id AS destination, SI_ScoreByFtrList( ";
			selectSQL += "new SI_FeatureList(src.photo_ac, 0.3, src.photo_ch, 0.3, ";
			selectSQL += "src.photo_pc, 0.1, src.photo_tx, 0.3), dst.photo_si) AS similarity ";
			selectSQL += "FROM Animals src, Animals dst ";
			selectSQL += "WHERE src.id <> dst.id AND src.id=" + sourceModel.getId() + " ";
			selectSQL += "ORDER BY similarity ASC) img WHERE ROWNUM <= 3 ";

			ResultSet resultSet = createDatabaseQuery(selectSQL);

			while (resultSet.next()) {
				Long id = resultSet.getLong("destination");
				AnimalModel model = getAnimal(id);

				modelsList.add(model);
			}

			resultSet.close();
		} catch (SQLException e) {
			throw new DataManagerException("SQLException: " + e.getMessage());
		}

		return modelsList;
	}

	public synchronized AnimalModel getAnimal(Long id) throws DataManagerException {
		try {
			Statement stmt = connection.createStatement();
			String selectSQL = "";
			selectSQL += "SELECT * ";
			selectSQL += "FROM Animals ";
			selectSQL += "WHERE id=" + id + " ";
			selectSQL += " FOR UPDATE";
			Logger.createLog(Logger.DEBUG_LOG, "SENDING QUERY: " + selectSQL);
			OracleResultSet rset = (OracleResultSet) stmt.executeQuery(selectSQL);
			if (!rset.next()) {
				throw new DataManagerException("Object with id=[" + id + "] not found!");
			}
			String name = rset.getString("name");
			String species = rset.getString("Species");
			OrdImage image = (OrdImage) rset.getORAData("photo", OrdImage.getORADataFactory());
			rset.close();
			stmt.close();

			AnimalModel model = new AnimalModel(id, name, species);
			model.setImage(image);

			return model;
		} catch (SQLException e) {
			throw new DataManagerException("SQLException: " + e.getMessage());
		}
	}

	/**
	 * TODO
	 */
	public synchronized boolean saveAnimal(AnimalModel model) throws DataManagerException {
		try {

			connection.setAutoCommit(false);

			if (model.isNew()) {
				Statement statement = connection.createStatement();
				String insertSQL = "";
				insertSQL += "INSERT INTO " + model.getTableName() + " (Name, Species, photo) ";
				insertSQL += "VALUES ('" + model.getName() + "','" + model.getSpecies() + "', ordsys.ordimage.init())";
				Logger.createLog(Logger.DEBUG_LOG, "SENDING QUERY: " + insertSQL);
				statement.executeUpdate(insertSQL);
				ResultSet getIdResultSet = connection.createStatement().executeQuery("SELECT " + model.getTableName() + "_seq.currval FROM dual");
				if (getIdResultSet.next()) {
					model.setId(getIdResultSet.getLong(1));
				}
				statement.close();
			} else if(model.getImage() == null && model.getImageByteArray() != null) {
				Statement statement = connection.createStatement();
				String updateSQL = "UPDATE Animals SET photo = ordsys.ordimage.init() WHERE id = " + model.getId();
				Logger.createLog(Logger.DEBUG_LOG, "SENDING UPDATE: " + updateSQL);
				statement.executeUpdate(updateSQL);
				statement.close();
			}

			Statement stmt = connection.createStatement();
			String selectSQL = "";
			selectSQL += "SELECT photo ";
			selectSQL += "FROM " + model.getTableName() + " ";
			selectSQL += "WHERE id=" + model.getId() + " ";
			selectSQL += " FOR UPDATE";
			Logger.createLog(Logger.DEBUG_LOG, "SENDING QUERY: " + selectSQL);
			OracleResultSet rset = (OracleResultSet) stmt.executeQuery(selectSQL);
			if (!rset.next()) {
				throw new DataManagerException("Object with id=[" + model.getId() + "] not found!");
			}
			OrdImage image = (OrdImage) rset.getORAData("photo", OrdImage.getORADataFactory());
			rset.close();
			stmt.close();

			model.setImage(image);

			if (model.getImageByteArray() != null) {
				model.getImage().loadDataFromByteArray(model.getImageByteArray());
				model.getImage().setProperties();
			}

			String updateSQL = "";
			updateSQL += "UPDATE " + model.getTableName() + " ";
			updateSQL += "SET name = ?, Species = ?, photo = ? ";
			updateSQL += "WHERE id=" + model.getId();
			OraclePreparedStatement preparedStmt = (OraclePreparedStatement) connection.prepareStatement(updateSQL);
			preparedStmt.setString(1, model.getName());
			preparedStmt.setString(2, model.getSpecies());
			preparedStmt.setORAData(3, model.getImage());
			Logger.createLog(Logger.DEBUG_LOG, "SENDING QUERY: " + updateSQL + " | photo=" + (image == null ? null : image.toString()));
			preparedStmt.executeUpdate();
			preparedStmt.close();

			if(model.getImageByteArray() != null) {
				stmt = connection.createStatement();
				String updateStillImageSQL = "";
				updateStillImageSQL += "UPDATE " + model.getTableName() + " t ";
				updateStillImageSQL += "SET t.photo_si=SI_STILLImage(t.photo.getContent()) ";
				updateStillImageSQL += "WHERE id=" + model.getId();
				Logger.createLog(Logger.DEBUG_LOG, "SENDING QUERY: " + updateStillImageSQL);
				stmt.executeQuery(updateStillImageSQL);

				String updateFeaturesSQL = "";
				updateFeaturesSQL += "UPDATE " + model.getTableName() + " t SET ";
				updateFeaturesSQL += "t.photo_ac=SI_AverageColor(t.photo_si), ";
				updateFeaturesSQL += "t.photo_ch=SI_ColorHistogram(t.photo_si), ";
				updateFeaturesSQL += "t.photo_pc=SI_PositionalColor(t.photo_si), ";
				updateFeaturesSQL += "t.photo_tx=SI_Texture(t.photo_si) ";
				updateFeaturesSQL += "WHERE id=" + model.getId();
				Logger.createLog(Logger.DEBUG_LOG, "SENDING QUERY: " + updateFeaturesSQL);
				stmt.executeQuery(updateFeaturesSQL);
				stmt.close();
			}

			updateAnimalShifts(model.getId(), model.getDateFrom(), model.getDateTo(), model.getLocation(), model.getWeight());

			connection.commit();

		} catch (SQLException e) {
			throw new DataManagerException("SQLException: " + e.getMessage());
		} catch (IOException e) {
			throw new DataManagerException("IOException: " + e.getMessage());
		}
		finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				Logger.createLog(Logger.ERROR_LOG, "Cannot set auto-commit to true");
			}
		}

		model.setImageByteArray(null);
		model.setIsChanged(false);
		return true;
	}

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
			String sqlPrep = spatialObject.isNew() ?
					"INSERT INTO Spatial_Objects (Name, Type, Geometry, ZIndex) VALUES(?, ?, ?, ?)" :
					"UPDATE Spatial_Objects SET Name = ?, Type = ?, Geometry = ?, ZIndex = ? WHERE ID = ?";

			PreparedStatement preparedStatement = connection.prepareStatement(sqlPrep);

			preparedStatement.setString(1, spatialObject.getName());
			Long id = spatialObject.getType().getId();
			if (id == BaseModel.NULL_ID) {
				preparedStatement.setNull(2, Types.INTEGER);
			} else {
				preparedStatement.setLong(2, id);
			}

			preparedStatement.setObject(3, JGeometry.store(connection, spatialObject.getGeometry()));
			preparedStatement.setInt(4, spatialObject.getzIndex());

			Logger.createLog(Logger.DEBUG_LOG, "Sending query: " + sqlPrep + " | name = '" + spatialObject.getName() + "', type = '" + spatialObject.getType().getId() + "', id = '" + spatialObject.getId() + "'");
			if (spatialObject.isNew()) {
				this.executeInsertAndSetId(preparedStatement, spatialObject);
			} else {
				preparedStatement.setLong(5, spatialObject.getId());
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
	 * @throws DataManagerException when exception from createDatabaseQuery() is received or
	 *                              when SQLException is caught
	 */
	public void reloadAllSpatialObjects() throws DataManagerException {
		List<SpatialObjectModel> spatialObjects = new ArrayList<>();

		String sqlQuery = "SELECT * FROM Spatial_Objects";
		ResultSet resultSet = createDatabaseQuery(sqlQuery);

		try {
			while (resultSet.next()) {
				try {
					Long id = resultSet.getLong("ID");
					Long typeID = resultSet.getLong("Type");
					String name = resultSet.getString("Name");
					int zIndex = resultSet.getInt("ZIndex");
					SpatialObjectTypeModel spatialType = getSpatialObjectType(typeID);

					byte[] rawGeometry = resultSet.getBytes("Geometry");
					spatialObjects.add(SpatialObjectModel.loadFromDB(id, zIndex, name, spatialType, rawGeometry));
				} catch (ModelException mE) {
					Logger.createLog(Logger.ERROR_LOG, mE.getMessage());
				}
			}
			// sorts collection by zIndex
			Collections.sort(spatialObjects);
		} catch (SQLException ex) {
			throw new DataManagerException("reloadAllSpatialObjects: SQLException: " + ex.getMessage());
		} catch (DataManagerException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new DataManagerException("reloadAllSpatialObjects: JGeometry exception: " + ex.getMessage());
		}

		this.spatialObjects = spatialObjects;
	}

	/**
	 * Is used to get cached data from SpatialObjects Table.
	 *
	 * @return list of SpatialObjects
	 */
	public List<SpatialObjectModel> getSpatialObjects() {
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

		if (spatialObjectTypes == null) reloadAllSpatialObjectTypes();

		for (SpatialObjectTypeModel type : spatialObjectTypes) {
			if (type.getId().equals(typeID)) return type;
		}

		return null;
	}

	/**
	 * Method returns all spatial objects types from the database.
	 *
	 * @throws DataManagerException when exception from createDatabaseQuery() is received
	 */
	public void reloadAllSpatialObjectTypes() throws DataManagerException {
		ArrayList<SpatialObjectTypeModel> spacialTypes = new ArrayList<>();

		spacialTypes.add(SpatialObjectTypeModel.UnknownSpatialType);

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
	}

	/**
	 * Calculates area of spatial object
	 *
	 * @param spatialLeft informations will be calculated for this object
	 * @return array with area, length data
	 * @throws DataManagerException
	 */
	public double[] getSpatialObjectAnalyticFunction(SpatialObjectModel spatialLeft) throws DataManagerException {
		String sqlQuery = "SELECT SDO_GEOM.SDO_AREA(geometry, 1) AS Area, SDO_GEOM.SDO_LENGTH(geometry, 1) AS Length FROM SPATIAL_OBJECTS WHERE ID='" + spatialLeft.getId() + "'";
		ResultSet resultSet = createDatabaseQuery(sqlQuery);

		try {
			if (!resultSet.next()) {
				throw new DataManagerException("Analytic informations about object with id=[" + spatialLeft.getId() + "] not found!");
			}
			double area = resultSet.getDouble("Area");
			double length = resultSet.getDouble("Length");
			return new double[]{area, length};
		} catch (SQLException ex) {
			throw new DataManagerException("getAllSpatialObjectTypes: SQLException: " + ex.getMessage());
		}

	}

	/**
	 * Counts minimal distance between two spatialObjectModels
	 *
	 * @param spatialLeft  first object (counting from)
	 * @param spatialRight object counting to
	 * @return distance
	 * @throws DataManagerException
	 */
	public double getDistanceToOtherSpatialObject(SpatialObjectModel spatialLeft, SpatialObjectModel spatialRight) throws DataManagerException {
		String sqlQuery = "SELECT SDO_GEOM.SDO_DISTANCE(L.Geometry, R.Geometry, 1) AS Distance FROM SPATIAL_OBJECTS L, SPATIAL_OBJECTS R WHERE L.ID = '" + spatialLeft.getId() + "' AND R.ID = '" + spatialRight.getId() + "'";
		ResultSet resultSet = createDatabaseQuery(sqlQuery);

		try {
			if (!resultSet.next()) {
				throw new DataManagerException("Distance with id=[" + spatialLeft.getId() + "] not found!");
			}
			return resultSet.getDouble("Distance");
		} catch (SQLException ex) {
			throw new DataManagerException("getAllSpatialObjectTypes: SQLException: " + ex.getMessage());
		}
	}

	/**
	 * Gets length of all spatial objects by type name
	 *
	 * @param typeName e.g. usage - Path
	 * @return sum of object's length
	 * @throws DataManagerException
	 */
	public double getWholeLengthBySpatialTypeName(String typeName) throws DataManagerException {
		String sqlQuery = "" +
				"SELECT SUM(SDO_GEOM.SDO_LENGTH(geometry, 1)) WholePathLength " +
				"FROM SPATIAL_OBJECTS JOIN SPATIAL_OBJECT_TYPES ON SPATIAL_OBJECTS.TYPE = SPATIAL_OBJECT_TYPES.ID " +
				"WHERE SPATIAL_OBJECT_TYPES.NAME LIKE '" + typeName + "'";

		ResultSet resultSet = createDatabaseQuery(sqlQuery);

		try {
			if (!resultSet.next()) {
				throw new DataManagerException("Problem with loading whole path length not loaded!");
			}
			return resultSet.getDouble("WholePathLength");
		} catch (SQLException ex) {
			throw new DataManagerException("getAllSpatialObjectTypes: SQLException: " + ex.getMessage());
		}
	}

	/**
	 * Get all spatial objects based on sql function type.
	 *
	 * @param functionType {@link #SQL_FUNCTION_WITHIN_DISTANCE}, {@link #SQL_FUNCTION_SDO_RELATE}
	 * @param spatialLeft  model which is function determining from
	 * @param additional   parameter
	 * @return list of spatial objects which are affected by function
	 * @throws DataManagerException
	 */
	public List<SpatialObjectModel> getAllSpatialObjectsFromFunction(int functionType, SpatialObjectModel spatialLeft, Integer additional) throws DataManagerException {
		String sqlQuery = "SELECT R.ID FROM SPATIAL_OBJECTS L, SPATIAL_OBJECTS R WHERE L.ID = " + spatialLeft.getId() + " AND ";
		switch (functionType) {
			case SQL_FUNCTION_WITHIN_DISTANCE:
				sqlQuery += "SDO_WITHIN_DISTANCE(L.Geometry, R.GEOMETRY, 'distance=" + additional + "') = 'TRUE'";
				break;

			case SQL_FUNCTION_SDO_RELATE:
				sqlQuery += "L.ID != R.ID AND SDO_RELATE(L.GEOMETRY, R.GEOMETRY, 'mask=ANYINTERACT') = 'TRUE'";
				break;

			default:
				throw new DataManagerException("getAllSpatialObjectsFromFunction(): NO EXISTING FUNCTION TYPE");
		}

		ResultSet resultSet = createDatabaseQuery(sqlQuery);
		try {
			List<SpatialObjectModel> resultModels = new ArrayList<>();
			// iterating all found rows
			while (resultSet.next()) {
				Long spatialId = resultSet.getLong("Id");
				SpatialObjectModel foundModel = BaseModel.findById(spatialId, getSpatialObjects());
				if (foundModel == null) continue;
				resultModels.add(foundModel);
			}

			return resultModels;
		} catch (SQLException ex) {
			throw new DataManagerException("getAllSpatialObjectsFromFunction: SQLException: " + ex.getMessage());
		}
	}

	/**
	 * Gets all N closest spatial objects with distance
	 *
	 * @param spatialLeft object which is related to
	 * @param count       of objects to select
	 * @param sameType    whether will search only objects with same type
	 * @return list of spatial objects with additional information -> distance
	 * @throws DataManagerException
	 */
	public List<SpatialObjectModel> getClosestNSpatialObjects(SpatialObjectModel spatialLeft, Integer count, boolean sameType) throws DataManagerException {
		String sqlQuery = "" +
				"SELECT /*+ ORDERED */ R.ID, SDO_NN_DISTANCE(1) AS Distance " +
				"FROM SPATIAL_OBJECTS L, SPATIAL_OBJECTS R " +
				"WHERE " +
				"L.ID = " + spatialLeft.getId() + " AND " +
				"L.ID != R.ID AND " +
				"SDO_NN(R.GEOMETRY, L.GEOMETRY, 'SDO_BATCH_SIZE=10', 1) = 'TRUE' AND " +
				"ROWNUM <= " + count + " " +
				(sameType ? "AND L.TYPE = R.TYPE " : "") +
				"ORDER BY Distance";

		ResultSet resultSet = createDatabaseQuery(sqlQuery);
		try {
			List<SpatialObjectModel> resultModels = new ArrayList<>();
			// iterating all found rows
			while (resultSet.next()) {
				Long spatialId = resultSet.getLong("Id");
				Double distance = resultSet.getDouble("Distance");
				SpatialObjectModel foundModel = BaseModel.findById(spatialId, getSpatialObjects());
				if (foundModel == null) continue;
				foundModel.getAdditionalInformations().put(SpatialObjectModel.ADDITIONAL_CLOSEST_DISTANCE, distance);
				resultModels.add(foundModel);
			}

			return resultModels;
		} catch (SQLException ex) {
			throw new DataManagerException("getAllSpatialObjectsFromFunction: SQLException: " + ex.getMessage());
		}
	}

	/**
	 * Is used to get cached data from SpatialObjectTypes Table.
	 *
	 * @return list of SpatialObjectTypes
	 */
	public List<SpatialObjectTypeModel> getSpatialObjectTypes() {
		return spatialObjectTypes;
	}

	// -----------------------------------------
	// ------------- METHODS FOR EMPLOYEES -----
	// -----------------------------------------

	// TODO should be used DataManager.findById()
	public SpatialObjectModel getSpatialObjectModelWithID(long id) {
		for (SpatialObjectModel model : getSpatialObjects()) {
			if (model.getId() == id)
				return model;
		}
		return null;
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
	public List<EmployeeModel> getEmployees() {
		return employees;
	}


	public ArrayList<EmployeeModel> getEmployeesAtDate(final Date date) throws DataManagerException {
		final ArrayList<EmployeeModel> employees = new ArrayList<>();
		AsyncTask asyncTask = new AsyncTask() {
			@Override
			protected void onDone(boolean success) {
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

	public boolean updateEmployeeShifts(final Long employeeID, final Date arDateFrom, final Date arDateTo, final Long location) {
		AsyncTask asyncTask = new AsyncTask() {
			@Override
			protected void onDone(boolean success) {
				//
			}

			@Override
			protected Boolean doInBackground() throws Exception {
				java.sql.Date dateFrom = new java.sql.Date(arDateFrom.getTime());
				java.sql.Date dateTo = new java.sql.Date(arDateTo.getTime());

				CallableStatement cstmt = connection.prepareCall("BEGIN updateEmployeeTable(?, ?, ?, ?); END;");

				cstmt.setLong(1, employeeID);
				cstmt.setDate(2, dateFrom);
				cstmt.setDate(3, dateTo);
				cstmt.setLong(4, location);

				cstmt.execute();

				return true;
			}
		};
		asyncTask.start();
		return true;
	}


	public boolean deleteEmployeeShifts(final Long employeeID, final Date arDateFrom, final Date arDateTo) {

		AsyncTask asyncTask = new AsyncTask() {
			@Override
			protected void onDone(boolean success) {
				//
			}

			@Override
			protected Boolean doInBackground() throws Exception {
				java.sql.Date dateFrom = new java.sql.Date(arDateFrom.getTime());
				java.sql.Date dateTo = new java.sql.Date(arDateTo.getTime());

				CallableStatement cstmt = connection.prepareCall("BEGIN deleteEmployeeShiftTable(?, ?, ?); END;");

				cstmt.setLong(1, employeeID);
				cstmt.setDate(2, dateFrom);
				cstmt.setDate(3, dateTo);

				cstmt.execute();

				return true;
			}
		};
		asyncTask.start();
		return true;
	}

	public ArrayList<EmployeeModel> getEmployeeHistory(final long employeeID) throws DataManagerException {
		final ArrayList<EmployeeModel> employees = new ArrayList<>();
		AsyncTask asyncTask = new AsyncTask() {
			@Override
			protected void onDone(boolean success) {
			}

			@Override
			protected Boolean doInBackground() throws Exception {
				String sqlQuery = "SELECT e.ID as EmployeeID, e.name, e.surname, s.ID as ShiftID, s.Location, s.dFrom, s.dTo " +
						"FROM EMPLOYEES e LEFT JOIN Employees_Shift s ON e.ID = s.EmplId WHERE e.ID = " + employeeID;

				ResultSet resultSet = createDatabaseQuery(sqlQuery);

				try {
					while (resultSet.next()) {
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

	// -----------------------------------------
	// ------------- METHODS FOR ANIMALS -------
	// -----------------------------------------

	/**
	 * Is used to get cached data from Animals Table.
	 *
	 * @return list of Animals
	 */
	public List<AnimalModel> getAnimals() {
		return animals;
	}


	public ArrayList<AnimalModel> getAnimalsAtDate(final Date date) throws DataManagerException {
		final ArrayList<AnimalModel> animals = new ArrayList<>();
		AsyncTask asyncTask = new AsyncTask() {
			@Override
			protected void onDone(boolean success) {
			}

			@Override
			protected Boolean doInBackground() throws Exception {
				String datum = new SimpleDateFormat("dd-MMM-yyyy").format(date);
				String sqlQuery = "SELECT an.ID as AnimalID, an.Name, an.Species, an.photo, ar.Location, ar.Weight, ar.dFrom, ar.dTo FROM Animals an INNER JOIN Animals_Records ar on an.ID = ar.AnimalID " +
						"WHERE ar.dFrom <= '" + datum + "' AND ar.dTo >= '" + datum + "' ";
				ResultSet resultSet = createDatabaseQuery(sqlQuery);

				try {
					while (resultSet.next()) {
						Long id = resultSet.getLong("AnimalID");
						String name = resultSet.getString("Name");
						String species = resultSet.getString("Species");
						Long location = resultSet.getLong("Location");
						Float weight = resultSet.getFloat("Weight");

						OracleResultSet rset = (OracleResultSet) resultSet;
						OrdImage image = (OrdImage) rset.getORAData("photo", OrdImage.getORADataFactory());

						Date dateFrom = resultSet.getDate("dFrom");
						Date dateTo = resultSet.getDate("dTo");

						AnimalModel animal = new AnimalModel(id, name, species, location, weight, dateFrom, dateTo);
						animal.setImage(image);

						animals.add(animal);

					}
				} catch (SQLException e) {
					throw new DataManagerException("getAllAnimalsAtDate: SQLException: " + e.getMessage());
				}
				return true;
			}
		};
		asyncTask.start();
		return animals;
	}

	public boolean updateAnimalShifts(final Long animalID, final Date arDateFrom, final Date arDateTo, final Long location, final float weight) {
		AsyncTask asyncTask = new AsyncTask() {
			@Override
			protected void onDone(boolean success) {
				//
			}

			@Override
			protected Boolean doInBackground() throws Exception {
				java.sql.Date dateFrom = new java.sql.Date(arDateFrom.getTime());
				java.sql.Date dateTo = new java.sql.Date(arDateTo.getTime());

				CallableStatement cstmt = connection.prepareCall ("BEGIN updateAnimalTable(?, ?, ?, ?, ?); END;");
				cstmt.setLong(1, animalID);
				cstmt.setDate(2, dateFrom);
				cstmt.setDate(3, dateTo);
				cstmt.setLong(4, location);
				cstmt.setFloat(5, weight);

				cstmt.execute();

				return true;
			}
		};
		asyncTask.start();
		return true;
	}


	public boolean deleteAnimalRecords(final Long animalID, final Date arDateFrom, final Date arDateTo){

		AsyncTask asyncTask = new AsyncTask() {
			@Override
			protected void onDone(boolean success) {
				//
			}

			@Override
			protected Boolean doInBackground() throws Exception {
				java.sql.Date dateFrom = new java.sql.Date(arDateFrom.getTime());
				java.sql.Date dateTo = new java.sql.Date(arDateTo.getTime());

				CallableStatement cstmt = connection.prepareCall ("BEGIN deleteAnimalsRecordsTable(?, ?, ?); END;");

				cstmt.setLong(1, animalID);
				cstmt.setDate(2, dateFrom);
				cstmt.setDate(3, dateTo);

				cstmt.execute();

				return true;
			}
		};
		asyncTask.start();
		return true;
	}

	public ArrayList<AnimalModel> getAnimalHistory(final long animalID) throws DataManagerException {
		final ArrayList<AnimalModel> animals = new ArrayList<>();
		AsyncTask asyncTask = new AsyncTask() {
			@Override
			protected void onDone(boolean success) {

			}

			@Override
			protected Boolean doInBackground() throws Exception {
				String sqlQuery = "SELECT a.ID as AnimalID, a.name, a.Species, s.ID as ShiftID, s.Location, s.Weight, s.dFrom, s.dTo " +
						"FROM Animals a LEFT JOIN Animals_Records s ON a.ID = s.AnimalID WHERE a.ID = " + animalID;

				ResultSet resultSet = createDatabaseQuery(sqlQuery);

				try {
					while (resultSet.next()) {
						Long emplID = resultSet.getLong("AnimalID");
						Long shiftID = resultSet.getLong("ShiftID");
						String name = resultSet.getString("Name");
						String species = resultSet.getString("Species");
						Float weight = resultSet.getFloat("Weight");
						Long location = resultSet.getLong("Location");
						Date dateFrom = resultSet.getDate("dFrom");
						Date dateTo = resultSet.getDate("dTo");
						animals.add(new AnimalModel(emplID, name, species, location, weight, dateFrom, dateTo, shiftID));
					}
				} catch (SQLException e) {
					throw new DataManagerException("getAllAnimalsAtDate: SQLException: " + e.getMessage());
				}
				return true;
			}
		};
		asyncTask.start();
		return animals;
	}
}
