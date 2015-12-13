package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.adapter.DataManager;
import cz.vutbr.fit.pdb.ateam.exception.ControllerException;
import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.gui.components.EmployeeDetailTable;
import cz.vutbr.fit.pdb.ateam.gui.components.EmployeesTable;
import cz.vutbr.fit.pdb.ateam.gui.tabs.EmployeesTab;
import cz.vutbr.fit.pdb.ateam.gui.tabs.details.EmployeeDetailPanel;
import cz.vutbr.fit.pdb.ateam.gui.tabs.details.EmployeeShiftEditPanel;
import cz.vutbr.fit.pdb.ateam.gui.tabs.lists.EmployeesListPanel;
import cz.vutbr.fit.pdb.ateam.model.employee.EmployeeModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.observer.ISpatialObjectSelectionChangedListener;
import cz.vutbr.fit.pdb.ateam.observer.SpatialObjectSelectionChangeObservable;
import cz.vutbr.fit.pdb.ateam.utils.Logger;
import cz.vutbr.fit.pdb.ateam.utils.Utils;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Author Tomas Hanus
 */
public class EmployeesTabController extends Controller
		implements
			ISpatialObjectSelectionChangedListener,
			EmployeesTableController {
	public static final Long ALL_LOCATIONS = Long.valueOf(-1);
	public static final Boolean SHOW_ACTUAL_DATA = true;
	public static final Boolean SHOW_HISTORY = false;

	private EmployeesTab employeesTab;
	private EmployeesListPanel employeesListPanel;
	private EmployeeDetailPanel employeeDetailPanel;
	private EmployeeModel selectedEmployeeModel;
	private SpatialObjectModel selectedSpatialObject;
	private Date dateToDisplay;
	private EmployeeShiftEditPanel employeeShiftEditPanel;

	public EmployeesTabController(EmployeesTab employeesPanel) {
		super();
		this.employeesTab = employeesPanel;
		this.employeesListPanel = new EmployeesListPanel(this);
		this.employeeDetailPanel = new EmployeeDetailPanel(employeesTab);
		this.selectedSpatialObject = null;
		this.dateToDisplay = Calendar.getInstance().getTime();

		SpatialObjectSelectionChangeObservable.getInstance().subscribe(this);

		this.employeesTab.getTabPanel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				contentPanelTabSwitchAction();
			}
		});

		showEmployeeList();
	}


	/**
	 * Creates Panel to show actual Employees list.
	 */
	public void showEmployeeList(){
		showEmployeeList(Calendar.getInstance().getTime());
	}

	/**
	 * Populate list panel to show Employees list to actually selected date
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
		//TODO later only fill table with valid data - not creating table again
		EmployeesTable table = new EmployeesTable(this);
		table.setColumnsWidth();
		List<EmployeeModel> models;

		models = new ArrayList<>();
		try {
			models = dataManager.getEmployeesAtDate(dateToDisplay);
		} catch (DataManagerException e) {
			Logger.createLog(Logger.ERROR_LOG, e.getMessage());
		}

		for (EmployeeModel model : models) {
			if (selectedSpatialObject == null){
				table.addEmployeeModel(model);
			}
			else{
				if (selectedSpatialObject.getId() == model.getLocation())
					table.addEmployeeModel(model);
			}
		}

		employeesListPanel.setEmployeesTable(table);
	}


	/**
	 * This method is supposed to show AddNewEmployee employeesTab.
	 */
	public void addNewEmployeeAction(){
		EmployeeModel newEmployeeModel = new EmployeeModel("", ""); //TODO Generate new ID

		editEmployeeDetail(newEmployeeModel, EmployeeDetailPanel.NEW_EMPLOYEE);
	}


	/**
	 * Action called when EditEmployee button is clicked.
	 */
	public void editEmployeeAction(){
		//TODO get selected Employee
		EmployeeModel selectedEmployee = new EmployeeModel(2, "pa", "pi", (long) 2, null, null); // TODO REMOVE LATER

		editEmployeeDetail(selectedEmployee, EmployeeDetailPanel.EDIT_EMPLOYEE);
	}

	/**
	 * Populates edit/new employee employeesTab and then shows it
	 * @param employee Shows detail employeesTab with new/existing employee.
	 * @param newOrEditHeader
	 */
	public void editEmployeeDetail(EmployeeModel employee, int newOrEditHeader){
		this.employeeDetailPanel = new EmployeeDetailPanel(this.employeesTab);

		List<SpatialObjectModel> locations = getSpatialObjects();

		this.employeeDetailPanel.populateEmployeeDetailPanel(employee, locations, newOrEditHeader);

		this.selectedEmployeeModel = employee;

		fillEmployeeDetailTable();

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


	/**
	 * Called when button Discard is hit on EmployeDetailTable. Calls EmployeeList Panel
	 */
	public void discardUserAction() {
		showEmployeeList();
	}

	/**
	 * Called after hit on SaveEmpployee Button on EmployeeDetail Panel. It creates new Employee or update selected employee
	 * from actualDate to 'forever'.
	 * @param employeeDetailPanelMode
	 */
	public void saveEmployee(int employeeDetailPanelMode) {
		this.selectedEmployeeModel.setName(employeeDetailPanel.getNameTextFieldValue());
		this.selectedEmployeeModel.setSurname(employeeDetailPanel.getSurnameTextFieldValue());
		this.selectedEmployeeModel.setLocation(employeeDetailPanel.getLocationComboBoxValue());
		this.selectedEmployeeModel.setDateFrom(Utils.removeTime(Calendar.getInstance().getTime()));
		this.selectedEmployeeModel.setDateTo(Utils.getForeverDate());

		if (employeeDetailPanelMode == EmployeeDetailPanel.EDIT_EMPLOYEE){
			selectedEmployeeModel.setIsChanged(true);
		}

		saveModels(selectedEmployeeModel);

		showEmployeeList();
	}

	/**
	 * Fires when spatial object is selected on zoo map canvas.
	 *
	 * @param spatialObjectModel selected spatial object model
	 */
	@Override
	public void spatialObjectSelectionChangedListener(SpatialObjectModel spatialObjectModel) {
		this.selectedSpatialObject = spatialObjectModel;

		if (employeesTab.isVisible()){
			showEmployeeList(dateToDisplay);
		}

	}

	/**
	 * Action triggered after hit on Edit button in EmployeesTable. It calls method to display employeeDetail Panel.
	 * @param employeeModel
	 */
	@Override
	public void EmployeesTableEditAction(EmployeeModel employeeModel) {
		editEmployeeDetail(employeeModel, EmployeeDetailPanel.EDIT_EMPLOYEE);
		//TODO Show DetailPanel for selected Employee
	}

	@Override
	public void EmployeesTableDeleteAction(EmployeeModel employeeModel) {
		//TODO Delete selected employee
	}

	/**
	 * Whenever user select a date from datePicker on EmployeeListPanel this action is triggered.
	 * Content of EmployeeListPanel is then updated if date is corret.
	 *
	 * If newDate is from future ??alert message pops up??
	 * @param newDate New date selected from DatePicker
	 */
	public void datePickerChangedAction(Date newDate) {
		Date newDateWithoutTime = Utils.removeTime(newDate);
		Date currentlyDisplayedDateWithoutTime = Utils.removeTime(dateToDisplay);
		Date todayDateWithoutTime = Utils.removeTime(Calendar.getInstance().getTime());

		try{
			if (newDateWithoutTime.after(todayDateWithoutTime)){
				throw new ControllerException("datePickerChangedAction: " + "Future date selected!");
			}
		} catch (ControllerException e) {
			Logger.createLog(Logger.WARNING_LOG, "datePickerChangedAction: Future date selected.");
			employeesListPanel.switchToToday();
			newDateWithoutTime = todayDateWithoutTime;
			//TODO Create Alert message ??here or in the listPanel??
		} finally {
			if (!currentlyDisplayedDateWithoutTime.equals(newDateWithoutTime)){
				dateToDisplay = newDateWithoutTime;
				showEmployeeList(dateToDisplay);
			}
		}
	}

	/**
	 * It displays records which are valid today on EmployeesListPanel table
	 * @param isSelected
	 */
	public void actualDateSwitchAction(boolean isSelected) {
		dateToDisplay = Utils.removeTime(Calendar.getInstance().getTime());
		showEmployeeList(dateToDisplay);
		employeesListPanel.switchToToday();
	}


	/**
	 * Shows date picker to choose the date of records you want to display in EmployeesListPanel table
	 */
	public void showHistorySwitchAction() {
		employeesListPanel.switchToPast();
	}


	/**
	 * Action fired when selected TabPanel on ContentPanel has changed
	 */
	public void contentPanelTabSwitchAction() {
		Component selectedComponent = employeesTab.getTabPanel().getSelectedComponent();
		Component employeeTab = this.employeesTab;
		if (selectedComponent == employeeTab){
			fillUpEmployeesTable(dateToDisplay);
		}
	}


	/**
	 * Creates EmployeeDetail table and populates it with data. Then this table is added on EmployeeDetailPanel.
	 */
	public void fillEmployeeDetailTable(){
		EmployeeDetailTable table = new EmployeeDetailTable();
		table.setColumnsWidth();

		List<EmployeeModel> models;

		models = new ArrayList<>();
		try {
			models = dataManager.getEmployeeHistory(selectedEmployeeModel.getId());
		} catch (DataManagerException e) {
			Logger.createLog(Logger.ERROR_LOG, e.getMessage());
		}
		if (models != null && models.size() > 0){
			for (EmployeeModel model : models) {
				table.addEmployeeModel(model);
			}
		}

		employeeDetailPanel.setEmployeeDetailTable(table);

	}


	/**
	 * Action triggered when Show History checkbox is ticked on EmployeeDetailPanel
	 * @param selected set to true/false if checkbox is selected or not
	 */
	public void showHistoryAction(boolean selected) {
		if (selected == true){
			employeeDetailPanel.showHistoryShiftPane();
		}else {
			employeeDetailPanel.hideHistoryShiftPane();
		}
	}

	/**
	 * Returns actually selected EmployeeModel.
	 * @return
	 */
	public EmployeeModel getSelectedEmployeeModel(){
		return selectedEmployeeModel;
	}

	/**
	 * Action triggered after click on Edit button on EmployeeShiftEdit Panel.
	 */
	public void editShiftAction() {
		ArrayList<SpatialObjectModel> locations = (ArrayList<SpatialObjectModel>) getSpatialObjects();
		this.employeeShiftEditPanel = new EmployeeShiftEditPanel(employeesTab, locations);
		Utils.changePanelContent(this.employeesTab, this.employeeShiftEditPanel);
	}

	public void discardEditEmployeeShiftAction() {
		Utils.changePanelContent(this.employeesTab, this.employeeDetailPanel);
	}

	/**
	 * Action called after click on confirmUpdateDeleteAction on EmployeeShiftEdit Panel
	 * @param isHistoryUpdate it's true/false in order action should be update/delete.
	 */
	public void confirmUpdateDeleteAction(boolean isHistoryUpdate) {
		if (isHistoryUpdate){
			DataManager.getInstance().updateEmployeeShifts(selectedEmployeeModel.getId(), employeeShiftEditPanel.getDateFrom(), employeeShiftEditPanel.getDateTo(), employeeShiftEditPanel.getSelectedLocation());
		}else { // if it's not edit action, it's DELETE action
			DataManager.getInstance().deleteEmployeeShifts(selectedEmployeeModel.getId(), employeeShiftEditPanel.getDateFrom(), employeeShiftEditPanel.getDateTo());
		}
		editEmployeeDetail(selectedEmployeeModel, EmployeeDetailPanel.EDIT_EMPLOYEE);
		this.employeeDetailPanel.showHistoryShiftPane();
		//TODO NullPointer crash after deleting all shift history
	}

	public void switchBetweenEditAndDeleteAction(boolean selected) {
		if (selected == true){
			employeeShiftEditPanel.showLocationPicker();
		} else{
			employeeShiftEditPanel.hideLocationPicker();
		}
	}


}
