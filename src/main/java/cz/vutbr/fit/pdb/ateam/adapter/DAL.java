package cz.vutbr.fit.pdb.ateam.adapter;

import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.utils.Logger;

import java.util.ArrayList;

/**
 * Data access layer - DAL stores cached tables from DB and helps to keep the data updated
 * and allows to work with same data throughout whole app.
 *
 * @author Tomas Hanus
 */
public class DAL {
	private static DAL instance = new DAL();

	private ArrayList<SpatialObjectModel> spatialObjects;

	/**
	 * Method is used to get instance of DAL wherever it's needed. Instance of this class
	 * is unique so Singleton pattern is used. It's instantiated at start of the app.
	 *
	 * @return instance of the DAL
	 */
	public synchronized static DAL getInstance() {
		return instance;
	}

	/**
	 * Loads all tables from DB.
	 */
	public void LoadDataFromDB(){
		try {
			this.spatialObjects = DataManager.getInstance().getAllSpatialObjects();
		} catch (DataManagerException e) {
			Logger.createLog(Logger.ERROR_LOG, e.getMessage());
		}
	}

	/**
	 * Is used to reload all spatial objects from DB.
	 * @return Returns newly loaded list of SpatialObjects
	 */
	public ArrayList<SpatialObjectModel> reloadSpatialObjects(){
		try{
			this.spatialObjects = DataManager.getInstance().getAllSpatialObjects();
		} catch (DataManagerException e){
			Logger.createLog(Logger.ERROR_LOG, e.getMessage());
		}

		//TODO notify all if needed

		return this.spatialObjects;
	}

	/**
	 * Is used to get cached data from SpatialObjects Table.
	 * @return list of SpatialObjects
	 */
	public ArrayList<SpatialObjectModel> getSpatialObjects(){
		return this.spatialObjects;
	}

}
