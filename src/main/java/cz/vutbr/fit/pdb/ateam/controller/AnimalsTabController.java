package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.adapter.DataManager;
import cz.vutbr.fit.pdb.ateam.exception.ControllerException;
import cz.vutbr.fit.pdb.ateam.exception.DataManagerException;
import cz.vutbr.fit.pdb.ateam.gui.components.AnimalDetailTable;
import cz.vutbr.fit.pdb.ateam.gui.components.AnimalsTable;
import cz.vutbr.fit.pdb.ateam.gui.components.EmployeesHistoryTable;
import cz.vutbr.fit.pdb.ateam.gui.components.MultimediaPanel;
import cz.vutbr.fit.pdb.ateam.gui.dialog.CompareImagesDialog;
import cz.vutbr.fit.pdb.ateam.gui.tabs.AnimalsTab;
import cz.vutbr.fit.pdb.ateam.gui.tabs.details.AnimalDetailPanel;
import cz.vutbr.fit.pdb.ateam.gui.tabs.details.AnimalShiftEditPanel;
import cz.vutbr.fit.pdb.ateam.gui.tabs.lists.AnimalsListPanel;
import cz.vutbr.fit.pdb.ateam.model.animal.AnimalModel;
import cz.vutbr.fit.pdb.ateam.model.employee.EmployeeModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.observer.ISpatialObjectSelectionChangedListener;
import cz.vutbr.fit.pdb.ateam.observer.SpatialObjectSelectionChangeObservable;
import cz.vutbr.fit.pdb.ateam.tasks.AsyncTask;
import cz.vutbr.fit.pdb.ateam.utils.Logger;
import cz.vutbr.fit.pdb.ateam.utils.Utils;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Created by Tomas on 12/12/2015.
 */
public class AnimalsTabController extends Controller
		implements
		ISpatialObjectSelectionChangedListener,
		AnimalsTableController {

	private AnimalsTab animalsTab;
	private AnimalsListPanel animalsListPanel;
	private AnimalDetailPanel animalDetailPanel;
	private AnimalModel selectedAnimalModel;
	private SpatialObjectModel selectedSpatialObject;
	private Date dateToDisplay;
	private AnimalShiftEditPanel animalShiftEditPanel;
	private MultimediaPanel multimediaPanel;
	private Boolean isImageChangedFlag = false;

	public AnimalsTabController(AnimalsTab animalsPanel) {
		super();
		this.animalsTab = animalsPanel;
		this.animalsListPanel = new AnimalsListPanel(this);
		this.animalDetailPanel = new AnimalDetailPanel(animalsTab);
		this.selectedSpatialObject = null;
		this.dateToDisplay = Calendar.getInstance().getTime();

		SpatialObjectSelectionChangeObservable.getInstance().subscribe(this);

		this.animalsTab.getTabPanel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				contentPanelTabSwitchAction();
			}
		});

		showAnimalList();
	}


	/**
	 * Creates Panel to show actual Animals list.
	 */
	public void showAnimalList() {
		showAnimalList(Calendar.getInstance().getTime());
	}

	/**
	 * Populate list panel to show Animals list to actually selected date
	 */
	public void showAnimalList(Date date) {
		fillUpAnimalsTable(date);
		Utils.changePanelContent(this.animalsTab, this.animalsListPanel);
	}

	/**
	 * This method calls method fillUpAnimalsTable(Date date) with today's date.
	 */
	private void fillUpAnimalsTable() {
		fillUpAnimalsTable(Calendar.getInstance().getTime());
	}

	/**
	 * Filling up data table in the animal tab
	 *
	 * @param dateToDisplay
	 */
	private void fillUpAnimalsTable(final Date dateToDisplay) {
		final AnimalsTable table = new AnimalsTable(this);
		table.setColumnsWidth();


		new AsyncTask() {
			List<AnimalModel> models = new ArrayList<>();

			@Override
			protected Boolean doInBackground() throws Exception {
				try {
					models = dataManager.getAnimalsAtDate(dateToDisplay);
					return true;
				} catch (DataManagerException e) {
					Logger.createLog(Logger.ERROR_LOG, e.getMessage());
				}
				return false;
			}

			@Override
			protected void onDone(boolean success) {
				for (AnimalModel model : models) {
					if (selectedSpatialObject == null) {
						table.addAnimalModel(model);
					} else {
						if (Objects.equals(selectedSpatialObject.getId(), model.getLocation())) table.addAnimalModel(model);
					}
				}
				animalsListPanel.setAnimalsTable(table);
			}
		}.start();}

	/**
	 * This method is supposed to show AddNewAnimal animalsTab.
	 */
	public void addNewAnimalAction() {
		AnimalModel newAnimalModel = new AnimalModel("", "");

		editAnimalDetail(newAnimalModel, AnimalDetailPanel.NEW_ANIMAL);
	}

	/**
	 * Populates edit/new animal animalsTab and then shows it
	 *
	 * @param animal          Shows detail animalsTab with new/existing animal.
	 * @param newOrEditHeader
	 */
	public void editAnimalDetail(AnimalModel animal, int newOrEditHeader) {
		this.animalDetailPanel = new AnimalDetailPanel(this.animalsTab);

		this.multimediaPanel = this.animalDetailPanel.getMultimediaPanel();
		this.multimediaPanel.setImage(animal);

		List<SpatialObjectModel> locations = getSpatialObjects();

		this.animalDetailPanel.populateAnimalDetailPanel(animal, locations, newOrEditHeader);

		this.selectedAnimalModel = animal;

		fillAnimalDetailTable();

		Utils.changePanelContent(animalsTab, this.animalDetailPanel);
	}


	/**
	 * Called when button Discard is hit on EmployeDetailTable. Calls AnimalList Panel
	 */
	public void discardUserAction() {
		showAnimalList();
	}

	/**
	 * Called after hit on SaveEmpployee Button on AnimalDetail Panel. It creates new Animal or update selected animal
	 * from actualDate to 'forever'.
	 *
	 * @param animalDetailPanelMode
	 */
	public void saveAnimal(int animalDetailPanelMode) {

		if (animalDetailPanel.getNameTextFieldValue().equals("")) {
			showDialog(ERROR_MESSAGE, "You must insert a name.");
			return;
		}
		if (animalDetailPanel.getSpeciesTextFieldValue().equals("")) {
			showDialog(ERROR_MESSAGE, "You must insert a species.");
			return;
		}

		this.selectedAnimalModel.setName(animalDetailPanel.getNameTextFieldValue());
		this.selectedAnimalModel.setSpecies(animalDetailPanel.getSpeciesTextFieldValue());
		this.selectedAnimalModel.setLocation(animalDetailPanel.getLocationComboBoxValue());
		this.selectedAnimalModel.setWeight(animalDetailPanel.getWeight());
		this.selectedAnimalModel.setDateFrom(Utils.removeTime(Calendar.getInstance().getTime()));
		this.selectedAnimalModel.setDateTo(Utils.getForeverDate());

		if (animalDetailPanelMode == AnimalDetailPanel.EDIT_ANIMAL) {
			selectedAnimalModel.setIsChanged(true);
		}

		selectedAnimalModel.setImageByteArray(multimediaPanel.getImageByteArray());

		saveModels(selectedAnimalModel);

		isImageChangedFlag = false;

		if (animalDetailPanelMode == AnimalDetailPanel.NEW_ANIMAL) {
			editAnimalDetail(selectedAnimalModel, AnimalDetailPanel.EDIT_ANIMAL);
		}
	}

	/**
	 * Fires when spatial object is selected on zoo map canvas.
	 *
	 * @param spatialObjectModel selected spatial object model
	 */
	@Override
	public void spatialObjectSelectionChangedListener(SpatialObjectModel spatialObjectModel) {
		this.selectedSpatialObject = spatialObjectModel;

		if (animalsTab.isVisible()) {
			showAnimalList(dateToDisplay);
		}

	}

	/**
	 * Action triggered after hit on Edit button in AnimalsTable. It calls method to display animalDetail Panel.
	 *
	 * @param animalModel
	 */
	@Override
	public void AnimalsTableEditAction(AnimalModel animalModel) {
		editAnimalDetail(animalModel, AnimalDetailPanel.EDIT_ANIMAL);
	}

	/**
	 * Whenever user select a date from datePicker on AnimalListPanel this action is triggered.
	 * Content of AnimalListPanel is then updated if date is corret.
	 * <p/>
	 * If newDate is from future ??alert message pops up??
	 *
	 * @param newDate New date selected from DatePicker
	 */
	public void datePickerChangedAction(Date newDate) {
		Date newDateWithoutTime = Utils.removeTime(newDate);
		Date currentlyDisplayedDateWithoutTime = Utils.removeTime(dateToDisplay);
		Date todayDateWithoutTime = Utils.removeTime(Calendar.getInstance().getTime());

		try {
			if (newDateWithoutTime.after(todayDateWithoutTime)) {
				throw new ControllerException("datePickerChangedAction: " + "Future date selected!");
			}
		} catch (ControllerException e) {
			Logger.createLog(Logger.WARNING_LOG, "datePickerChangedAction: Future date selected.");
			animalsListPanel.switchToToday();
			newDateWithoutTime = todayDateWithoutTime;
			showDialog(ERROR_MESSAGE, "You can set only today, or day from past.");
		} finally {
			if (!currentlyDisplayedDateWithoutTime.equals(newDateWithoutTime)) {
				dateToDisplay = newDateWithoutTime;
				showAnimalList(dateToDisplay);
			}
		}
	}

	/**
	 * It displays records which are valid today on AnimalsListPanel table
	 *
	 */
	public void actualDateSwitchAction() {
		dateToDisplay = Utils.removeTime(Calendar.getInstance().getTime());
		showAnimalList(dateToDisplay);
		animalsListPanel.switchToToday();
	}


	/**
	 * Shows date picker to choose the date of records you want to display in AnimalsListPanel table
	 */
	public void showHistorySwitchAction() {
		animalsListPanel.switchToPast();
	}


	/**
	 * Action fired when selected TabPanel on ContentPanel has changed
	 */
	public void contentPanelTabSwitchAction() {
		Component selectedComponent = animalsTab.getTabPanel().getSelectedComponent();
		Component animalTab = this.animalsTab;
		if (selectedComponent == animalTab) {
			fillUpAnimalsTable(dateToDisplay);
		}
	}


	/**
	 * Creates AnimalDetail table and populates it with data. Then this table is added on AnimalDetailPanel.
	 */
	public void fillAnimalDetailTable() {
		final AnimalDetailTable table = new AnimalDetailTable();
		table.setColumnsWidth();

		new AsyncTask() {
			List<AnimalModel> models = new ArrayList<>();

			@Override
			protected Boolean doInBackground() throws Exception {
				try {
					models = dataManager.getAnimalHistory(selectedAnimalModel.getId());
					return true;
				} catch (DataManagerException e) {
					Logger.createLog(Logger.ERROR_LOG, e.getMessage());
				}
				return false;
			}

			@Override
			protected void onDone(boolean success) {
				if (models != null && models.size() > 0) {
					for (AnimalModel model : models) {
						table.addAnimalModel(model);
					}
				}
				animalDetailPanel.setAnimalDetailTable(table);
			}
		}.start();
	}


	public void fillEmployeesHistoryDetailTable() {
		final EmployeesHistoryTable table = new EmployeesHistoryTable();
		table.setColumnsWidth();

		new AsyncTask() {
			List<EmployeeModel> models = new ArrayList<>();

			@Override
			protected Boolean doInBackground() throws Exception {
				try {
					models = dataManager.getEmployeesForAnimal(selectedAnimalModel);
					return true;
				} catch (DataManagerException e) {
					Logger.createLog(Logger.ERROR_LOG, e.getMessage());
				}
				return false;
			}

			@Override
			protected void onDone(boolean success) {
				if (models != null && models.size() > 0) {
					for (EmployeeModel model : models) {
						table.addEmployeeModel(model);
					}
				}
				animalDetailPanel.setEmployeesHistoryTable(table);
			}
		}.start();
	}


	/**
	 * Action triggered when Show History checkbox is ticked on AnimalDetailPanel
	 *
	 * @param selected set to true if checkbox is selected
	 */
	public void showHistoryAction(boolean selected) {
		if (selected == true) {
			animalDetailPanel.showHistoryShiftPane();
			fillAnimalDetailTable();
		}
	}

	/**
	 * Returns actually selected AnimalModel.
	 *
	 * @return
	 */
	public AnimalModel getSelectedAnimalModel() {
		return selectedAnimalModel;
	}

	/**
	 * Action triggered after click on Edit button on AnimalShiftEdit Panel.
	 */
	public void editShiftAction() {
		ArrayList<SpatialObjectModel> locations = (ArrayList<SpatialObjectModel>) getSpatialObjects();
		this.animalShiftEditPanel = new AnimalShiftEditPanel(animalsTab, locations);
		Utils.changePanelContent(this.animalsTab, this.animalShiftEditPanel);
	}

	public void discardEditAnimalShiftAction() {
		Utils.changePanelContent(this.animalsTab, this.animalDetailPanel);
	}

	/**
	 * Action called after click on confirmUpdateDeleteAction on AnimalShiftEdit Panel
	 *
	 * @param isHistoryUpdate it's true/false in order action should be update/delete.
	 */
	public void confirmUpdateDeleteAction(final boolean isHistoryUpdate) {
		Date todayDateWithoutTime = Utils.removeTime(Calendar.getInstance().getTime());

		if (Utils.removeTime(animalShiftEditPanel.getDateFrom()).after(todayDateWithoutTime) || Utils.removeTime(animalShiftEditPanel.getDateTo()).after(todayDateWithoutTime)){
			showDialog(ERROR_MESSAGE, "You must choose today or past date.");
			return;
		}
		if (Utils.removeTime(animalShiftEditPanel.getDateFrom()).after(Utils.removeTime(animalShiftEditPanel.getDateTo()))){
			showDialog(ERROR_MESSAGE, "Date from must be earlier or equals to date to.");
			return;
		}

		new AsyncTask() {
			@Override
			protected Boolean doInBackground() throws Exception {
				if (isHistoryUpdate) {
					return dataManager.updateAnimalShifts(selectedAnimalModel.getId(), animalShiftEditPanel.getDateFrom(), animalShiftEditPanel.getDateTo(), animalShiftEditPanel.getSelectedLocation(), animalShiftEditPanel.getWeightTextField());
				} else {
					// if it's not edit action, it's DELETE action
					return dataManager.deleteAnimalRecords(selectedAnimalModel.getId(), animalShiftEditPanel.getDateFrom(), animalShiftEditPanel.getDateTo());
				}
			}

			@Override
			protected void onDone(boolean success) {
				editAnimalDetail(selectedAnimalModel, AnimalDetailPanel.EDIT_ANIMAL);
				animalDetailPanel.showHistoryShiftPane();
			}
		}.start();
	}

	/**
	 * This action is triggered by clicking on editRadioButton on AnimalEditShiftPanel.
	 * It changes acton from update to delete or from delete to update.
	 * @param selected
	 */
	public void switchBetweenEditAndDeleteAction(boolean selected) {
		if (selected == true) {
			animalShiftEditPanel.setShiftEditPanelToEdit();
		} else {
			animalShiftEditPanel.setShiftEditPanelToDelete();
		}
	}

	/**
	 * Action triggered after clicking on Open button on multimediaPanel. It shows dialog for opening a new image.
	 * @param file
	 */
	public void openFileAction(File file) {
		multimediaPanel.changeImagePanelContent(file);

		selectedAnimalModel.setIsChanged(true);
		byte[] bytes = multimediaPanel.getImageByteArray();
		if (bytes == null) {
			showDialog(ERROR_MESSAGE, "Cannot open file.");
			return;
		}

		isImageChangedFlag = true;
	}

	/**
	 * Action triggered after click on Mirror and Save button on AnimalDetail Panel. It mirrors image and save all
	 * data of actually selected model.
	 */
	public void mirrorImageAction() {
		if (selectedAnimalModel == null || isImageChangedFlag) {
			showDialog(ERROR_MESSAGE, "Save animal before mirroring image.");
			return;
		}

		AsyncTask asyncTask = new AsyncTask() {
			@Override
			protected void onDone(boolean success) {
				if (!success) {
					showDialog(ERROR_MESSAGE, "Cannot mirror image.");
				}
			}

			@Override
			protected Boolean doInBackground() throws Exception {
				try {
					DataManager.getInstance().mirrorImage(selectedAnimalModel);

//					saveAnimal(AnimalDetailPanel.EDIT_ANIMAL);
					isImageChangedFlag = false;
					multimediaPanel.setImage(selectedAnimalModel);
				} catch (DataManagerException e) {
					Logger.createLog(Logger.ERROR_LOG, e.getMessage());
					return false;
				}
				return true;
			}
		};
		asyncTask.start();

	}

	/**
	 * Action triggered after click on Compare button on AnimalDetail Panel. It shows dialog with 3 most
	 * similar images of 3 different animals.
	 */
	public void compareImageAction() {
		if (selectedAnimalModel == null) {
			showDialog(ERROR_MESSAGE, "Animal needs to be saved first.");
			return;
		}

		if (selectedAnimalModel.getId() == 0L || isImageChangedFlag) {
			showDialog(ERROR_MESSAGE, "Animal needs to be saved first.");
			return;
		}

		AsyncTask asyncTask = new AsyncTask() {
			AnimalModel animal1 = null;
			AnimalModel animal2 = null;
			AnimalModel animal3 = null;

			@Override
			protected void onDone(boolean success) {
				if (!success) {
					showDialog(ERROR_MESSAGE, "Cannot compare images.");
				} else {
					CompareImagesDialog dialog = new CompareImagesDialog(multimediaPanel, selectedAnimalModel, animal1, animal2, animal3);
					dialog.setVisible(true);
				}
			}

			@Override
			protected Boolean doInBackground() throws Exception {
				try {
					ArrayList<AnimalModel> models;

					models = DataManager.getInstance().getThreeSimilarImages(selectedAnimalModel);

					if (models.size() > 0) {
						animal1 = models.get(0);
					}
					if (models.size() > 1) {
						animal2 = models.get(1);
					}
					if (models.size() > 2) {
						animal3 = models.get(2);
					}

				} catch (DataManagerException e1) {
					Logger.createLog(Logger.ERROR_LOG, e1.getMessage());
					return false;
				}
				return true;
			}
		};
		asyncTask.start();

	}

	/**
	 * Action triggered after clicking on showEmployeesHistoryCheckBox. It displays all employees
	 * who has been taking care of this animal.
	 * @param selected
	 */
	public void showEmployeesHistory(boolean selected) {
		if (selected) {
			animalDetailPanel.showHistoryShiftPane();
			fillEmployeesHistoryDetailTable();
		}
	}
}
