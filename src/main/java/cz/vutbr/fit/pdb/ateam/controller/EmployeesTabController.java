package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.adapter.DataManager;
import cz.vutbr.fit.pdb.ateam.gui.components.EmployeesTable;
import cz.vutbr.fit.pdb.ateam.gui.tabs.EmployeesTab;
import cz.vutbr.fit.pdb.ateam.gui.tabs.details.EmployeeDetailPanel;
import cz.vutbr.fit.pdb.ateam.gui.tabs.lists.EmployeesListPanel;
import cz.vutbr.fit.pdb.ateam.model.EmployeeModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.observer.ISpatialObjectSelectionChangedListener;
import cz.vutbr.fit.pdb.ateam.observer.SpatialObjectSelectionChangeObservable;
import cz.vutbr.fit.pdb.ateam.utils.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Author Tomas Hanus
 */
public class EmployeesTabController extends Controller implements ISpatialObjectSelectionChangedListener, EmployeesTableController {
	public static final Long ALL_LOCATIONS = Long.valueOf(-1);

	private EmployeesTab employeesTab;
	private EmployeesListPanel employeesListPanel;
	private EmployeeDetailPanel employeeDetailPanel;
	private SpatialObjectModel selectedSpatialObject;
	private Date dateToDisplay;


	public EmployeesTabController(EmployeesTab employeesPanel) {
		super();
		this.employeesTab = employeesPanel;
		this.employeesListPanel = new EmployeesListPanel(this);
		this.employeeDetailPanel = new EmployeeDetailPanel(employeesTab);
		this.selectedSpatialObject = null;
		this.dateToDisplay = Calendar.getInstance().getTime();

		SpatialObjectSelectionChangeObservable.getInstance().subscribe(this);

		showEmployeeList();
	}


	/**
	 * Creates Panel to show actual Employees list.
	 */
	public void showEmployeeList(){
		showEmployeeList(Calendar.getInstance().getTime());
	}

	/**
	 * Creates Panel to show actual Employees list.
	 */
	public void showEmployeeList(Date date){
		fillUpEmployeesTable(date);
		Utils.changePanelContent(this.employeesTab, this.employeesListPanel);
	}

	private void fillUpEmployeesTable() {
		fillUpEmployeesTable(Calendar.getInstance().getTime());
	}

	/**
	 * Filling up data table in the employee tab
	 * @param dateToDisplay
	 */
	private void fillUpEmployeesTable(Date dateToDisplay) {
		EmployeesTable table = new EmployeesTable(this);
		List<EmployeeModel> models;

		models = getEmployees();

		// TODO fill models by parameter from controller not the other way
//		if (Utils.removeTime(dateToDisplay).before(Utils.removeTime(Calendar.getInstance().getTime()))){
////		models = DataManager.getInstance().getEmployeesFromTO(dateToDisplay,dateToDisplay);
//		}else{
//			models = getEmployees();
//		}

		for (EmployeeModel model : models) {
			if (selectedSpatialObject == null){
				table.addEmployeeModel(model);
			}
			else{
				if (selectedSpatialObject.getId() == model.getLocation())
					table.addEmployeeModel(model);
			}
//			if (location == ALL_LOCATIONS ||  (location != ALL_LOCATIONS && location == model.getLocation()))
//				table.addEmployeeModel(model);
		}

		employeesListPanel.setEmployeesTable(table);
	}


	/**
	 * This method is supposed to show AddNewEmployee employeesTab.
	 */
	public void addNewEmployeeAction(){
		EmployeeModel newEmployeeModel = new EmployeeModel(1, "izydor", "AnoAno", (long) 2); //TODO Generate new ID

		////////////////////////// TEMP
		newEmployeeModel.setName("Tomasko");
		newEmployeeModel.setSurname("Tralala");
		newEmployeeModel.setLocation(12);
		////////////////////////// TEMP

		editEmployeeDetail(newEmployeeModel, EmployeeDetailPanel.NEW_EMPLOYEE);
	}


	/**
	 * Action called when EditEmployee button is clicked.
	 */
	public void editEmployeeAction(){
		//TODO get selected Employee
		EmployeeModel selectedEmployee = new EmployeeModel(2, "pa", "pi", (long) 2); // TODO REMOVE LATER

		editEmployeeDetail(selectedEmployee, EmployeeDetailPanel.EDIT_EMPLOYEE);
	}

	/**
	 * Populates edit/new employee employeesTab and then shows it
	 * @param employee Shows detail employeesTab with new/existing employee.
	 * @param newOrEditHeader
	 */
	public void editEmployeeDetail(EmployeeModel employee, int newOrEditHeader){
		this.employeeDetailPanel = new EmployeeDetailPanel(this.employeesTab);

		ArrayList<SpatialObjectModel> locations = dataManager.getSpatialObjects();

		this.employeeDetailPanel.populateEmployeeDetailPanel(employee, locations, newOrEditHeader);

//		/* Save reference to getEmployeeDetail employeesTab to EmployeeTab */
//		this.employeesTab.setEmployeeDetail(this.employeeDetailPanel);

		Utils.changePanelContent(employeesTab, this.employeeDetailPanel);
	}


	/**
	 * Gets list of locations in ZOO.
	 * @return Returns only spatial objects which represent a certain location.
	 */
	private ArrayList<SpatialObjectModel> getLocationsFromSpatialObjects(ArrayList<SpatialObjectModel> spatialObjects) {
		//TODO filter Locations from all spatial objects
		return spatialObjects;
	}

//	/**
//	 * Loads all spatialObjects
//	 */
//	private void populateSpatialObjects() {
//		AsyncTask getSpatialObjects = new AsyncTask() {
//			@Override
//			protected void whenDone(boolean success) {
//				// Nothing
//			}
//			@Override
//			protected Boolean doInBackground() throws Exception {
//				try{
//					spatialObjects = DataManager.getInstance().getAllSpatialObjects();
//					return true;
//				}catch (DataManagerException ex){
//					Logger.createLog(Logger.ERROR_LOG, ex.getMessage());
//					return false;
//				}
//			}
//		};
//		getSpatialObjects.start();
//	}

	public void discardUserAction() {
		showEmployeeList();
	}

	public void saveEmployee() {
	}

	/**
	 * Fires when spatial object is selected on zoo map canvas.
	 *
	 * @param spatialObjectModel selected spatial object model
	 */
	@Override
	public void spatialObjectSelectionChangedListener(SpatialObjectModel spatialObjectModel) {
		//TODO Do a reacton on NotifyObjectSelectonCHanged()
//		this.selectedSpatialObject = spatialObjectModel;
//		showEmployeeList(dateToDisplay);
	}

	@Override
	public void EmployeesTableEditAction(EmployeeModel employeeModel) {
//		editEmployeeDetail(employeeModel, EmployeeDetailPanel.EDIT_EMPLOYEE);
		//TODO Show DetailPanel for selected Employee
	}

	@Override
	public void EmployeesTableDeleteAction(EmployeeModel employeeModel) {
//		System.out.println("vymazavam zamestnanca menom " + employeeModel.getName());
		//TODO Delete selected employee
	}

	public void datePickerChangedAction(Date newDate) {
//		if (!Utils.removeTime(dateToDisplay).equals(Utils.removeTime(newDate))){
//			dateToDisplay = newDate;
//			showEmployeeList(dateToDisplay);
//		}

	}
}
