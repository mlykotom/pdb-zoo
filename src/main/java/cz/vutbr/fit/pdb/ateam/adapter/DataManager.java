package cz.vutbr.fit.pdb.ateam.adapter;

import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.exception.ModelException;
import cz.vutbr.fit.pdb.ateam.model.BaseModel;
import cz.vutbr.fit.pdb.ateam.model.animal.AnimalModel;
import cz.vutbr.fit.pdb.ateam.model.employee.EmployeeModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectTypeModel;
import cz.vutbr.fit.pdb.ateam.utils.Logger;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.pool.OracleDataSource;
import oracle.ord.im.OrdImage;
import oracle.spatial.geometry.JGeometry;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

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
	public static final int SQL_FUNCTION_WITHIN_DISTANCE = 1;
	public static final int SQL_FUNCTION_SDO_RELATE = 2;
	private static DataManager instance = new DataManager();
	private Connection connection;
	private List<SpatialObjectModel> spatialObjects = new ArrayList<>();
	private List<SpatialObjectTypeModel> spatialObjectTypes = new ArrayList<>();
	private List<EmployeeModel> employees;
	private List<AnimalModel> animals;

	public DataManager() {
		this.spatialObjects = new ArrayList<>();
		this.spatialObjectTypes = new ArrayList<>();
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

		try {
			connection.setAutoCommit(false);
			Statement stmt = connection.createStatement();
			ArrayList<String> initScripts = new ArrayList<>();

			Logger.createLog(Logger.DEBUG_LOG, "-----------------------------------------------------");
			Logger.createLog(Logger.DEBUG_LOG, "INITIALIZE DATABASE\n");

			// DROPPING ------------------------------------

			initScripts.add("DROP SEQUENCE Spatial_Object_Types_seq");
			initScripts.add("DROP SEQUENCE Spatial_Objects_seq");
			initScripts.add("DROP SEQUENCE Employees_seq");
			initScripts.add("DROP SEQUENCE Employees_Shift_seq");
			initScripts.add("DROP SEQUENCE Animals_seq");
			initScripts.add("DROP SEQUENCE Animals_Records_seq");
			initScripts.add("DROP INDEX SPATIAL_OBJECTS_INDEX");
			initScripts.add("DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME = 'SPATIAL_OBJECTS'");
			initScripts.add("DROP TABLE Employees_Shift");
			initScripts.add("DROP TABLE Employees");
			initScripts.add("DROP TABLE Animals_Records");
			initScripts.add("DROP TABLE Animals");
			initScripts.add("DROP TABLE Spatial_Objects");
			initScripts.add("DROP TABLE Spatial_Object_Types");

			for (String sql : initScripts) {
				try {
					Logger.createLog(Logger.DEBUG_LOG, "INIT SCRIPT: " + sql);
					stmt.executeUpdate(sql);
				} catch (SQLException e) {
					Logger.createLog(Logger.DEBUG_LOG, "INIT ERROR: " + e.getMessage());
				}
			}

			// CREATING ------------------------------------

			initScripts.clear();

			initScripts.add(
					"CREATE TABLE Spatial_Object_Types ( " +
							"  ID    INT                       NOT NULL, " +
							"  Name  VARCHAR(255)              NOT NULL, " +
							"  Color VARCHAR(6) DEFAULT 000000 NOT NULL, " +
							"  PRIMARY KEY (ID) " +
							")"
			);
			initScripts.add(
					"CREATE TABLE Spatial_Objects ( " +
							"  ID       INT          NOT NULL, " +
							"  Name     VARCHAR(255) NOT NULL, " +
							"  Type     INT, " +
							"  Geometry SDO_GEOMETRY NOT NULL, " +
							"  ZIndex INT DEFAULT 0 NULL, " +
							" " +
							"  PRIMARY KEY (ID), " +
							"  FOREIGN KEY (Type) REFERENCES Spatial_Object_Types (ID) ON DELETE SET NULL " +
							")"
			);
			initScripts.add(
					"INSERT INTO USER_SDO_GEOM_METADATA VALUES ( " +
							"  'SPATIAL_OBJECTS', " +
							"  'GEOMETRY', " +
							"  SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X', 0, 640, 1), SDO_DIM_ELEMENT('Y', 0, 480, 1)), " +
							"  NULL " +
							")"
			);
			initScripts.add(
					"CREATE INDEX SPATIAL_OBJECTS_INDEX ON SPATIAL_OBJECTS(GEOMETRY) INDEXTYPE IS MDSYS.SPATIAL_INDEX"
			);
			initScripts.add(
					"CREATE SEQUENCE Spatial_Object_Types_seq"
			);
			initScripts.add(
					"CREATE OR REPLACE TRIGGER Spatial_Object_Types_bir " +
							"BEFORE INSERT ON Spatial_Object_Types " +
							"FOR EACH ROW " +
							"  BEGIN " +
							"    SELECT Spatial_Object_Types_seq.NEXTVAL " +
							"    INTO :new.id " +
							"    FROM dual; " +
							"  END;"
			);
			initScripts.add(
					"CREATE SEQUENCE Spatial_Objects_seq"
			);
			initScripts.add(
					"CREATE OR REPLACE TRIGGER Spatial_Objects_bir " +
							"BEFORE INSERT ON Spatial_Objects " +
							"FOR EACH ROW " +
							"  BEGIN " +
							"    SELECT Spatial_Objects_seq.NEXTVAL " +
							"    INTO :new.id " +
							"    FROM dual; " +
							"  END;"
			);
			initScripts.add(
					"CREATE TABLE Employees ( " +
							"  ID INT NOT NULL, " +
							"  Name VARCHAR(255) NOT NULL, " +
							"  Surname VARCHAR (255) NOT NULL, " +
							"  PRIMARY KEY (ID) " +
							")"
			);
			initScripts.add(
					"CREATE TABLE Animals ( " +
							"  ID INT NOT NULL, " +
							"  Name VARCHAR(255) NOT NULL, " +
							"  Species VARCHAR (255) NOT NULL, " +
							"  PHOTO ORDSYS.ORDIMAGE, " +
							"  PHOTO_SI ORDSYS.SI_STILLIMAGE, " +
							"  PHOTO_AC ORDSYS.SI_AVERAGECOLOR, " +
							"  PHOTO_CH ORDSYS.SI_COLORHISTOGRAM, " +
							"  PHOTO_PC ORDSYS.SI_POSITIONALCOLOR, " +
							"  PHOTO_TX ORDSYS.SI_TEXTURE, " +
							"  PRIMARY KEY (ID) " +
							")"
			);
			initScripts.add(
					"CREATE TABLE Employees_Shift ( " +
							"  ID INT NOT NULL, " +
							"  EmplID INT NOT NULL, " +
							"  Location INT, " +
							"  dFrom DATE NOT NULL, " +
							"  dTo DATE NOT NULL, " +
							"  PRIMARY KEY (ID), " +
							"  FOREIGN KEY (Location) REFERENCES Spatial_Objects (ID), " +
							"  FOREIGN KEY (EmplID) REFERENCES Employees (ID) " +
							")"
			);
			initScripts.add(
					"CREATE TABLE Animals_Records ( " +
							"  ID INT NOT NULL, " +
							"  AnimalID INT NOT NULL, " +
							"  Location INT, " +
							"  Weight NUMBER, " +
							"  dFrom DATE NOT NULL, " +
							"  dTo DATE NOT NULL, " +
							"  PRIMARY KEY (ID), " +
							"  FOREIGN KEY (Location) REFERENCES Spatial_Objects (ID), " +
							"  FOREIGN KEY (AnimalID) REFERENCES Animals (ID) " +
							")"
			);
			initScripts.add(
					"CREATE SEQUENCE Employees_seq"
			);
			initScripts.add(
					"CREATE OR REPLACE TRIGGER Employees_bir " +
							"BEFORE INSERT ON Employees " +
							"FOR EACH ROW " +
							"  BEGIN " +
							"    SELECT Employees_seq.NEXTVAL " +
							"    INTO :new.id " +
							"    FROM dual; " +
							"  END;"
			);
			initScripts.add(
					"CREATE SEQUENCE Employees_Shift_seq"
			);
			initScripts.add(
					"CREATE OR REPLACE TRIGGER Employees_Shift_bir " +
							"BEFORE INSERT ON Employees_Shift " +
							"FOR EACH ROW " +
							"  BEGIN " +
							"    SELECT Employees_Shift_seq.NEXTVAL " +
							"    INTO :new.id " +
							"    FROM dual; " +
							"  END;"
			);
			initScripts.add(
					"CREATE SEQUENCE Animals_seq"
			);
			initScripts.add(
					"CREATE OR REPLACE TRIGGER Animals_bir " +
							"BEFORE INSERT ON Animals " +
							"FOR EACH ROW " +
							"  BEGIN " +
							"    SELECT Animals_seq.NEXTVAL " +
							"    INTO :new.id " +
							"    FROM dual; " +
							"  END;"
			);
			initScripts.add(
					"CREATE SEQUENCE Animals_Records_seq"
			);
			initScripts.add(
					"CREATE OR REPLACE TRIGGER Animals_Records_bir " +
							"BEFORE INSERT ON Animals_Records " +
							"FOR EACH ROW " +
							"  BEGIN " +
							"    SELECT Animals_Records_seq.NEXTVAL " +
							"    INTO :new.id " +
							"    FROM dual; " +
							"  END;"
			);

			initScripts.add(
					"CREATE OR REPLACE PROCEDURE updateEmployeeTable(p_employeeID IN INT, p_dateFrom IN Date, p_dateTo IN Date, p_new_location in INT) IS " +
							"employeeID INT; " +
							"dateFrom DATE; " +
							"dateTo DATE; " +
							"new_location INT; " +
							" " +
							"isUpdated BOOLEAN; " +
							" " +
							"A_OLD_FROM DATE; " +
							"A_OLD_TO DATE; " +
							"A_OLD_LOC INT; " +
							"BEGIN " +
							"  employeeID := p_employeeID; " +
							"  dateFrom := p_dateFrom; " +
							"  dateTo := p_dateTo; " +
							"  new_location := p_new_location; " +
							" " +
							" " +
							"  FOR rec IN (SELECT * FROM EMPLOYEES_SHIFT sh WHERE sh.EmplID = employeeID AND (dFrom <= dateTo AND dTo >= dateFrom) ORDER BY sh.dFrom) " +
							"  LOOP " +
							"    isUpdated := FALSE; " +
							" " +
							"    IF (rec.DFROM < dateFrom) THEN " +
							"      IF (rec.Location = new_location) THEN " +
							"        dateFrom := rec.dFrom; " +
							"      ELSE " +
							"        A_OLD_FROM := rec.dFrom; " +
							"        A_OLD_LOC := rec.Location; " +
							"        A_OLD_TO := dateFrom -1; " +
							" " +
							"        UPDATE Employees_Shift " +
							"        SET dTo = A_OLD_TO, dFrom = A_OLD_FROM, Location = A_OLD_LOC " +
							"        WHERE ID = rec.ID; " +
							" " +
							"        rec.dFrom := dateFrom; " +
							"        isUpdated := TRUE; " +
							"      END IF; " +
							"    END IF; " +
							"    IF (rec.dTo <= dateTo) THEN " +
							"      if (isUpdated = FALSE) THEN " +
							"        DELETE Employees_Shift WHERE ID = rec.ID; " +
							"      END IF; " +
							"    ELSE " +
							"      IF (rec.Location = new_location) THEN " +
							"        dateTo := rec.dTo; " +
							"        DELETE Employees_Shift WHERE ID = rec.ID; " +
							"      ELSE " +
							"        if (isUpdated = FALSE) THEN " +
							"          DELETE Employees_Shift WHERE ID = rec.ID; " +
							"        END IF; " +
							"        rec.dFrom := dateTo + 1; " +
							"        INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) " +
							"        VALUES (employeeID, rec.Location, rec.dFrom, rec.dTo ); " +
							"      END IF; " +
							"    END IF; " +
							"  END LOOP; " +
							"  INSERT INTO Employees_Shift (EmplID, Location, dFrom, dTo) " +
							"  VALUES (employeeID, new_location, dateFrom, dateTo); " +
							" " +
							"end;"
			);

			initScripts.add(
					"CREATE OR REPLACE PROCEDURE deleteEmployeeShiftTable(p_animalID IN INT, p_dateFrom IN Date, p_dateTo IN Date) IS " +
							"  animalID INT; " +
							"  dateFrom DATE; " +
							"  dateTo DATE; " +
							" " +
							"  isUpdated BOOLEAN; " +
							" " +
							"  BEGIN " +
							"    animalID := p_animalID; " +
							"    dateFrom := p_dateFrom; " +
							"    dateTo := p_dateTo; " +
							" " +
							"    FOR rec IN (SELECT * FROM EMPLOYEES_SHIFT sh WHERE sh.EmplID = animalID AND (dFrom <= dateTo AND dTo >= dateFrom)) " +
							"    LOOP " +
							"      isUpdated := FALSE; " +
							"      IF (rec.DFROM < dateFrom) THEN " +
							"        UPDATE Employees_Shift " +
							"        SET dTo = dateFrom -1 " +
							"        WHERE ID = rec.ID; " +
							" " +
							"        rec.dFrom := dateFrom; " +
							" " +
							"        isUpdated := TRUE; " +
							"      END IF; " +
							" " +
							"      IF (rec.dFrom >= dateFrom AND rec.dTo <= dateTo) THEN " +
							"        IF ( isUpdated = FALSE) THEN " +
							"          DELETE Employees_Shift WHERE ID = rec.ID; " +
							"        END IF; " +
							"      ELSE " +
							"        rec.dFrom := dateTo + 1; " +
							"        IF (isUpdated = TRUE) THEN " +
							"          INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) " +
							"          VALUES (animalID, rec.Location, rec.dFrom, rec.dTo ); " +
							"        ELSE " +
							"          UPDATE Employees_Shift " +
							"          SET dTo = rec.dTo, dFrom = rec.dFrom " +
							"          WHERE ID = rec.ID; " +
							"        END IF; " +
							"      END IF; " +
							"    END LOOP; " +
							"  end;"
			);
			initScripts.add(
					"CREATE OR REPLACE PROCEDURE updateAnimalTable(p_animalID IN INT, p_dateFrom IN Date, p_dateTo IN Date, p_new_location in INT, p_new_weight in NUMBER) IS " +
							"new_animalID INT; " +
							"dateFrom DATE; " +
							"dateTo DATE; " +
							"new_location INT; " +
							"new_weight NUMBER; " +
							" " +
							"isUpdated BOOLEAN; " +
							" " +
							"A_OLD_FROM DATE; " +
							"A_OLD_TO DATE; " +
							"A_OLD_LOC INT; " +
							"BEGIN " +
							"  new_animalID := p_animalID; " +
							"  dateFrom := p_dateFrom; " +
							"  dateTo := p_dateTo; " +
							"  new_location := p_new_location; " +
							"  new_weight := p_new_weight; " +
							" " +
							"  FOR rec IN (SELECT * FROM Animals_Records sh WHERE sh.AnimalID = new_animalID AND (sh.dFrom <= dateTo AND sh.dTo >= dateFrom) ORDER BY sh.dFrom) " +
							"  LOOP " +
							"    isUpdated := FALSE; " +
							" " +
							"    IF (rec.DFROM < dateFrom) THEN " +
							"      IF (rec.Location = new_location AND rec.Weight = new_weight) THEN " +
							"        dateFrom := rec.dFrom; " +
							"      ELSE " +
							"        A_OLD_FROM := rec.dFrom; " +
							"        A_OLD_LOC := rec.Location; " +
							"        A_OLD_TO := dateFrom -1; " +
							" " +
							"        UPDATE Animals_Records " +
							"        SET dTo = A_OLD_TO, dFrom = A_OLD_FROM, Location = A_OLD_LOC " +
							"        WHERE ID = rec.ID; " +
							" " +
							"        rec.dFrom := dateFrom; " +
							"        isUpdated := TRUE; " +
							"      END IF; " +
							"    END IF; " +
							"    IF (rec.dTo <= dateTo) THEN " +
							"      if (isUpdated = FALSE) THEN " +
							"        DELETE Animals_Records WHERE ID = rec.ID; " +
							"      END IF; " +
							"    ELSE " +
							"      IF (rec.Location = new_location AND rec.Weight = new_weight) THEN " +
							"        dateTo := rec.dTo; " +
							"        DELETE Animals_Records WHERE ID = rec.ID; " +
							"      ELSE " +
							"        if (isUpdated = FALSE) THEN " +
							"          DELETE Animals_Records WHERE ID = rec.ID; " +
							"        END IF; " +
							"        rec.dFrom := dateTo + 1; " +
							"        INSERT INTO Animals_Records ( AnimalID, Location, Weight, dFrom, dTo ) " +
							"        VALUES (new_animalID, rec.Location, rec.Weight, rec.dFrom, rec.dTo ); " +
							"      END IF; " +
							"    END IF; " +
							"  END LOOP; " +
							"  INSERT INTO Animals_Records (AnimalID, Location, Weight, dFrom, dTo) " +
							"  VALUES (new_animalID, new_location, new_weight, dateFrom, dateTo); " +
							" " +
							"end;"
			);
			initScripts.add(
					"CREATE OR REPLACE PROCEDURE deleteAnimalsRecordsTable(p_animalID IN INT, p_dateFrom IN Date, p_dateTo IN Date) IS " +
							"  new_animalID INT; " +
							"  dateFrom DATE; " +
							"  dateTo DATE; " +
							" " +
							"  isUpdated BOOLEAN; " +
							" " +
							"  BEGIN " +
							"    new_animalID := p_animalID; " +
							"    dateFrom := p_dateFrom; " +
							"    dateTo := p_dateTo; " +
							"    isUpdated := FALSE; " +
							" " +
							"    FOR rec IN (SELECT * FROM Animals_Records ar WHERE ar.AnimalID = new_animalID AND (ar.dFrom <= dateTo AND ar.dTo >= dateFrom)) " +
							"    LOOP " +
							"      IF (rec.DFROM < dateFrom) THEN " +
							"        UPDATE Animals_Records " +
							"        SET dTo = dateFrom -1 " +
							"        WHERE ID = rec.ID; " +
							" " +
							"        rec.dFrom := dateFrom; " +
							" " +
							"        isUpdated := TRUE; " +
							"      END IF; " +
							" " +
							"      IF (rec.dFrom >= dateFrom AND rec.dTo <= dateTo) THEN " +
							"        IF ( isUpdated = FALSE) THEN " +
							"          DELETE Animals_Records WHERE ID = rec.ID; " +
							"        END IF; " +
							"      ELSE " +
							"        rec.dFrom := dateTo + 1; " +
							"        IF (isUpdated = TRUE) THEN " +
							"          INSERT INTO Animals_Records ( AnimalID, Location, Weight, dFrom, dTo ) " +
							"          VALUES (new_animalID, rec.Location, rec.Weight, rec.dFrom, rec.dTo ); " +
							"        ELSE " +
							"          UPDATE Animals_Records " +
							"          SET dTo = rec.dTo, dFrom = rec.dFrom " +
							"          WHERE ID = rec.ID; " +
							"        END IF; " +
							"      END IF; " +
							"    END LOOP; " +
							"  end;"
			);

			for (String sql : initScripts) {
				Logger.createLog(Logger.DEBUG_LOG, "INIT SCRIPT: " + sql);
				stmt.executeUpdate(sql);
			}

			initScripts.clear();

			ClassLoader classLoader = getClass().getClassLoader();
			URL resourceFile = classLoader.getResource("insertData.sql");
			if (resourceFile == null)
				throw new DataManagerException("Cannot open resource file [insertData.sql]!");
			File file = new File(resourceFile.getFile());

			Scanner scanner = new Scanner(file);

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.length() == 0) continue;
				if (line.charAt(0) == '-') continue;
				String insertSQL = line.substring(0, line.length() - 1);
				initScripts.add(insertSQL);
			}

			scanner.close();

			for (String sql : initScripts) {
				Logger.createLog(Logger.DEBUG_LOG, "INIT SCRIPT: " + sql);
				stmt.executeUpdate(sql);
			}

			connection.commit();
			connection.setAutoCommit(true);

			insertImageIntoAnimalModel("horse.jpg", 1L);
			insertImageIntoAnimalModel("bear1.jpg", 4L);
			insertImageIntoAnimalModel("bear2.jpg", 5L);
			insertImageIntoAnimalModel("goat.jpg", 3L);

			Logger.createLog(Logger.DEBUG_LOG, "DATABASE INITIALIZED");
			Logger.createLog(Logger.DEBUG_LOG, "-----------------------------------------------------");

		} catch (SQLException | IOException e) {
			Logger.createLog(Logger.DEBUG_LOG, "DATABASE NOT INITIALIZED");
			Logger.createLog(Logger.DEBUG_LOG, "-----------------------------------------------------");
			try {
				if (!connection.getAutoCommit()) connection.setAutoCommit(true);

				throw new DataManagerException("SQLException: " + e.getMessage());
			} catch (SQLException e1) {
				throw new DataManagerException("SQLException: " + e1.getMessage());
			}
		}
	}

	/**
	 * Inserts animal image from resources into database animal record.
	 *
	 * @param resourceFileName name of the file in resource file
	 * @param animalID         id of the animal
	 * @throws DataManagerException when resource file can not be open or when animal do not exists
	 */
	private void insertImageIntoAnimalModel(String resourceFileName, Long animalID) throws DataManagerException {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			URL resourceFile = classLoader.getResource(resourceFileName);
			if (resourceFile == null) {
				Logger.createLog(Logger.ERROR_LOG, "Cannot open resource file: " + resourceFileName);
				throw new DataManagerException("Cannot open resource file!");
			}
			File file = new File(resourceFile.getFile());
			AnimalModel model;
			BufferedImage image;
			model = getAnimal(animalID);
			image = ImageIO.read(file);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ImageIO.write(image, "png", outputStream);
			model.setImageByteArray(outputStream.toByteArray());
			try {

				connection.setAutoCommit(false);

				Statement statement = connection.createStatement();
				String updateSQL = "UPDATE Animals SET photo = ordsys.ordimage.init() WHERE id = " + model.getId();
				Logger.createLog(Logger.DEBUG_LOG, "SENDING UPDATE: " + updateSQL);
				statement.executeUpdate(updateSQL);
				statement.close();

				Statement stmt = connection.createStatement();

				String selectSQL = ""
						+ "SELECT photo "
						+ "FROM " + model.getTableName() + " "
						+ "WHERE id=" + model.getId() + " "
						+ " FOR UPDATE";

				Logger.createLog(Logger.DEBUG_LOG, "SENDING QUERY: " + selectSQL);
				OracleResultSet rset = (OracleResultSet) stmt.executeQuery(selectSQL);
				if (!rset.next()) {
					throw new DataManagerException("Object with id=[" + model.getId() + "] not found!");
				}
				OrdImage imageDB = (OrdImage) rset.getORAData("photo", OrdImage.getORADataFactory());
				rset.close();
				stmt.close();

				model.setImage(imageDB);

				model.getImage().loadDataFromByteArray(model.getImageByteArray());
				model.getImage().setProperties();

				updateSQL = "";
				updateSQL += "UPDATE " + model.getTableName() + " ";
				if(model.getImage() != null)
					updateSQL += "SET name = ?, Species = ?, photo = ? ";
				else
					updateSQL += "SET name = ?, Species = ? ";
				updateSQL += "WHERE id=" + model.getId();
				OraclePreparedStatement preparedStmt = (OraclePreparedStatement) connection.prepareStatement(updateSQL);
				preparedStmt.setString(1, model.getName());
				preparedStmt.setString(2, model.getSpecies());
				if(model.getImage() != null) preparedStmt.setORAData(3, model.getImage());
				Logger.createLog(Logger.DEBUG_LOG, "SENDING QUERY: " + updateSQL + " | photo=" + (imageDB == null ? null : imageDB.toString()));
				preparedStmt.executeUpdate();
				preparedStmt.close();

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

				connection.commit();
			} catch (SQLException e) {
				throw new DataManagerException("SQLException: " + e.getMessage());
			} catch (IOException e) {
				throw new DataManagerException("IOException: " + e.getMessage());
			} finally {
				try {
					connection.setAutoCommit(true);
				} catch (SQLException e) {
					Logger.createLog(Logger.ERROR_LOG, "Cannot set auto-commit to true");
				}
			}
		} catch (IOException e) {
			Logger.createLog(Logger.ERROR_LOG, "IOEXception: " + e.getMessage());
			throw new DataManagerException("IOException: " + e.getMessage());
		}
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

		if (model instanceof SpatialObjectModel) {
			spatialObjects.remove(model);
		} else if (model instanceof SpatialObjectTypeModel) {
			spatialObjectTypes.remove(model);
		} else if (model instanceof EmployeeModel) {
			employees.remove(model);
		}
	}

	/**
	 * Saves model based on its type
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
		} else {
			throw new DataManagerException("saveModel(): Unsupported model to save");
		}
	}

	/**
	 * Saves employee model to DB
	 *
	 * @param employee model which will be inserted or updated
	 * @return success
	 */
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

			updateEmployeeShifts(employee.getId(), employee.getDateFrom(), employee.getDateTo(), employee.getLocation());

			employee.setIsChanged(false);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// -----------------------------------------
	// ------------- METHODS FOR SPATIAL OBJECTS
	// -----------------------------------------

	/**
	 * Mirrors animal's image.
	 *
	 * @param model model of the animal
	 * @throws DataManagerException when some sql error occurs
	 */
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

	/**
	 * Returns array of the threee the most similar animals
	 *
	 * @param sourceModel
	 * @return
	 * @throws DataManagerException when some sql error occurs
	 */
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

	/**
	 * Returns specified animal from the database
	 *
	 * @param id
	 * @return
	 * @throws DataManagerException when animal is not found
	 */
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
	 * Saves model of the animal into the database.
	 *
	 * @param model model of the animal
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
			} else if (model.getImage() == null && model.getImageByteArray() != null) {
				Statement statement = connection.createStatement();
				String updateSQL = "UPDATE Animals SET photo = ordsys.ordimage.init() WHERE id = " + model.getId();
				Logger.createLog(Logger.DEBUG_LOG, "SENDING UPDATE: " + updateSQL);
				statement.executeUpdate(updateSQL);
				statement.close();
			}

			Statement stmt = connection.createStatement();
			OrdImage image = null;

			String selectSQL = ""
					+ "SELECT photo "
					+ "FROM " + model.getTableName() + " "
					+ "WHERE id=" + model.getId() + " "
					+ " FOR UPDATE";

			Logger.createLog(Logger.DEBUG_LOG, "SENDING QUERY: " + selectSQL);
			OracleResultSet rset = (OracleResultSet) stmt.executeQuery(selectSQL);
			if (!rset.next()) {
				throw new DataManagerException("Object with id=[" + model.getId() + "] not found!");
			}
			image = (OrdImage) rset.getORAData("photo", OrdImage.getORADataFactory());
			rset.close();
			stmt.close();

			model.setImage(image);

			if (model.getImageByteArray() != null) {
				model.getImage().loadDataFromByteArray(model.getImageByteArray());
				model.getImage().setProperties();
			}

			String updateSQL = "";
			updateSQL += "UPDATE " + model.getTableName() + " ";
			if(model.getImage() != null)
				updateSQL += "SET name = ?, Species = ?, photo = ? ";
			else
				updateSQL += "SET name = ?, Species = ? ";
			updateSQL += "WHERE id=" + model.getId();
			OraclePreparedStatement preparedStmt = (OraclePreparedStatement) connection.prepareStatement(updateSQL);
			preparedStmt.setString(1, model.getName());
			preparedStmt.setString(2, model.getSpecies());
			if(model.getImage() != null) preparedStmt.setORAData(3, model.getImage());
			Logger.createLog(Logger.DEBUG_LOG, "SENDING QUERY: " + updateSQL + " | photo=" + (image == null ? null : image.toString()));
			preparedStmt.executeUpdate();
			preparedStmt.close();

			if (model.getImageByteArray() != null) {
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

			connection.commit();

			updateAnimalShifts(model.getId(), model.getDateFrom(), model.getDateTo(), model.getLocation(), model.getWeight());

		} catch (SQLException e) {
			throw new DataManagerException("SQLException: " + e.getMessage());
		} catch (IOException e) {
			throw new DataManagerException("IOException: " + e.getMessage());
		} finally {
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

		if (spatialObjectTypes == null || spatialObjectTypes.isEmpty()) reloadAllSpatialObjectTypes();

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

	public SpatialObjectModel getSpatialObjectModelWithID(long id) {
		for (SpatialObjectModel model : getSpatialObjects()) {
			if (model.getId() == id)
				return model;
		}
		return null;
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

	/**
	 * Returns all employes which are valid in specified date
	 *
	 * @param date
	 * @return
	 * @throws DataManagerException
	 */
	public List<EmployeeModel> getEmployeesAtDate(final Date date) throws DataManagerException {
		final ArrayList<EmployeeModel> employees = new ArrayList<>();
		String simpleDate = new SimpleDateFormat("dd-MMM-yyyy").format(date);
		String sqlQuery = "SELECT DISTINCT e.ID as EmployeeID, e.Name, e.Surname, s.Location, s.dFrom, s.dTo " +
				"FROM EMPLOYEES e LEFT JOIN Employees_Shift s ON e.ID = s.EmplId " +
				"WHERE s.dFrom <= '" + simpleDate + "' AND s.dTo >= '" + simpleDate + "'";

		System.out.println(sqlQuery);
		ResultSet resultSet = createDatabaseQuery(sqlQuery);

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

		return employees;
	}

	/**
	 * Method is used to update EmployeeShifts records.
	 *
	 * @param employeeID
	 * @param arDateFrom
	 * @param arDateTo
	 * @param location
	 * @return
	 */
	public boolean updateEmployeeShifts(final Long employeeID, final Date arDateFrom, final Date arDateTo, final Long location) throws DataManagerException {
		try {
			java.sql.Date dateFrom = new java.sql.Date(arDateFrom.getTime());
			java.sql.Date dateTo = new java.sql.Date(arDateTo.getTime());

			CallableStatement cstmt = connection.prepareCall("BEGIN updateEmployeeTable(?, ?, ?, ?); END;");
			cstmt.setLong(1, employeeID);
			cstmt.setDate(2, dateFrom);
			cstmt.setDate(3, dateTo);
			cstmt.setLong(4, location);

			cstmt.execute();
			return true;
		} catch (SQLException e) {
			throw new DataManagerException("updateEmployeeShifts: SQLException: " + e.getMessage());
		}
	}

	/**
	 * Method is used to delete temporalData from Employees_Shift table from specified interval.
	 *
	 * @param employeeID
	 * @param arDateFrom
	 * @param arDateTo
	 * @return
	 */
	public boolean deleteEmployeeShifts(final Long employeeID, final Date arDateFrom, final Date arDateTo) throws DataManagerException {
		java.sql.Date dateFrom = new java.sql.Date(arDateFrom.getTime());
		java.sql.Date dateTo = new java.sql.Date(arDateTo.getTime());

		try {
			CallableStatement cstmt = connection.prepareCall("BEGIN deleteEmployeeShiftTable(?, ?, ?); END;");
			cstmt.setLong(1, employeeID);
			cstmt.setDate(2, dateFrom);
			cstmt.setDate(3, dateTo);

			cstmt.execute();

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DataManagerException("deleteEmployeeShifts: SQLException: " + e.getMessage());
		}
	}


	/**
	 * Method returns all employeeHistory in ZOO (locations).
	 *
	 * @param employeeID
	 * @return
	 * @throws DataManagerException
	 */
	public ArrayList<EmployeeModel> getEmployeeHistory(final long employeeID) throws DataManagerException {
		final ArrayList<EmployeeModel> employees = new ArrayList<>();
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

		return employees;
	}

// -----------------------------------------
// ------------- METHODS FOR ANIMALS -------
// -----------------------------------------

	/**
	 * Method returns record for all animals from specific date.
	 *
	 * @param date
	 * @return
	 * @throws DataManagerException
	 */
	public ArrayList<AnimalModel> getAnimalsAtDate(final Date date) throws DataManagerException {
		final ArrayList<AnimalModel> animals = new ArrayList<>();

		String simpleDate = new SimpleDateFormat("dd-MMM-yyyy").format(date);
		String sqlQuery = "" +
				"SELECT an.ID as AnimalID, an.Name, an.Species, an.photo, ar.Location, ar.Weight, ar.dFrom, ar.dTo " +
				"FROM Animals an INNER JOIN Animals_Records ar on an.ID = ar.AnimalID " +
				"WHERE ar.dFrom <= '" + simpleDate + "' AND ar.dTo >= '" + simpleDate + "' ";

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
		return animals;
	}

	/**
	 * Method is used to update AnimalRecords or to create new animal Record.
	 *
	 * @param animalID
	 * @param arDateFrom
	 * @param arDateTo
	 * @param location
	 * @param weight
	 * @return
	 * @throws DataManagerException
	 */
	public boolean updateAnimalShifts(final Long animalID, final Date arDateFrom, final Date arDateTo, final Long location, final float weight) throws DataManagerException {
		java.sql.Date dateFrom = new java.sql.Date(arDateFrom.getTime());
		java.sql.Date dateTo = new java.sql.Date(arDateTo.getTime());

		try {
			CallableStatement cstmt = connection.prepareCall("BEGIN updateAnimalTable(?, ?, ?, ?, ?); END;");

			cstmt.setLong(1, animalID);
			cstmt.setDate(2, dateFrom);
			cstmt.setDate(3, dateTo);
			cstmt.setLong(4, location);
			cstmt.setFloat(5, weight);

			cstmt.execute();

			return true;
		} catch (SQLException e) {
			Logger.createLog(Logger.ERROR_LOG, "SQLException: " + e.getMessage());
			throw new DataManagerException("updateAnimalShifts: SQLException: " + e.getMessage());
		}
	}


	/**
	 * Method is used to delete temporalData from Animals_Records table from specified interval.
	 *
	 * @param animalID
	 * @param arDateFrom
	 * @param arDateTo
	 * @return
	 */
	public boolean deleteAnimalRecords(final Long animalID, final Date arDateFrom, final Date arDateTo) throws DataManagerException {
		java.sql.Date dateFrom = new java.sql.Date(arDateFrom.getTime());
		java.sql.Date dateTo = new java.sql.Date(arDateTo.getTime());

		try {
			CallableStatement cstmt = connection.prepareCall("BEGIN deleteAnimalsRecordsTable(?, ?, ?); END;");

			cstmt.setLong(1, animalID);
			cstmt.setDate(2, dateFrom);
			cstmt.setDate(3, dateTo);

			cstmt.execute();
		} catch (SQLException e) {
			throw new DataManagerException("deleteAnimalRecords: SQLException: " + e.getMessage());
		}

		return true;
	}


	/**
	 * Method returns all temporal animal records for specified animal.
	 *
	 * @param animalID
	 * @return
	 * @throws DataManagerException
	 */
	public ArrayList<AnimalModel> getAnimalHistory(final long animalID) throws DataManagerException {
		final ArrayList<AnimalModel> animals = new ArrayList<>();
		String sqlQuery = "" +
				"SELECT a.ID as AnimalID, a.name, a.Species, s.ID as ShiftID, s.Location, s.Weight, s.dFrom, s.dTo " +
				"FROM Animals a LEFT JOIN Animals_Records s ON a.ID = s.AnimalID " +
				"WHERE a.ID = " + animalID;

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
		return animals;
	}

	/**
	 * Method returns a list of Employees who has been taking care of specified animal.
	 *
	 * @param animalModel
	 * @return
	 * @throws DataManagerException
	 */
	public List<EmployeeModel> getEmployeesForAnimal(final AnimalModel animalModel) throws DataManagerException {
		final ArrayList<EmployeeModel> employees = new ArrayList<>();
		String sqlQuery = "SELECT empl.id, empl.name, empl.Surname, es.Location, GREATEST(es.dFrom, ar.dFrom) as dateFrom, LEAST(es.dTo,ar.dTo) as dateTo FROM Employees empl " +
				"JOIN Employees_Shift es on empl.ID = es.EmplID " +
				"JOIN Animals_Records ar on es.Location = ar.Location " +
				"JOIN Animals an on an.ID = ar.AnimalID " +
				"WHERE es.dFrom <= ar.dTo AND es.dTo >= ar.dFrom and an.ID = " + animalModel.getId();

		ResultSet resultSet = createDatabaseQuery(sqlQuery);

		try {
			while (resultSet.next()) {
				Long emplID = resultSet.getLong("ID");
				String name = resultSet.getString("Name");
				String surname = resultSet.getString("Surname");
				Long location = resultSet.getLong("Location");
				Date dateFrom = resultSet.getDate("dateFrom");
				Date dateTo = resultSet.getDate("dateTo");
				employees.add(new EmployeeModel(emplID, name, surname, location, dateFrom, dateTo));
			}
		} catch (SQLException e) {
			throw new DataManagerException("getAllEmployeesForAnimal: SQLException: " + e.getMessage());
		}
		return employees;
	}

	/**
	 * Method finds highest weight that employee has weighed until now.
	 *
	 * @param employeeModel
	 * @return
	 */
	public Float calculateMaxWeightForEmployee(final EmployeeModel employeeModel) throws DataManagerException {
		String sqlQuery = "SELECT MAX(weight) as Weight FROM (SELECT ar.Weight as weight FROM Employees empl JOIN Employees_Shift es on empl.ID = es.EmplID " +
				"JOIN Animals_Records ar on es.Location = ar.Location " +
				"WHERE es.dFrom <= ar.dTo AND es.dTo >= ar.dFrom AND empl.id = " + employeeModel.getId() + ") ";

		ResultSet resultSet = createDatabaseQuery(sqlQuery);

		try {
			if (!resultSet.next()) {
				throw new DataManagerException("getAllEmployeesForAnimal: Statement result not successfull");
			}

			return resultSet.getFloat("Weight");
		} catch (SQLException e) {
			throw new DataManagerException("getAllEmployeesForAnimal: SQLException: " + e.getMessage());
		}
	}
}
