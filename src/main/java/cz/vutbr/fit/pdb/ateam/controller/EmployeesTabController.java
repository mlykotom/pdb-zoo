package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.gui.tabs.EmployeesTab;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.observer.SpatialObjectSelectionChangeObservable;


/**
 * Created by Tomas on 10/24/2015.
 */
public class EmployeesTabController extends Controller implements SpatialObjectSelectionChangeObservable.SpatialObjectSelectionChangedListener {
	private EmployeesTab panel;

	public EmployeesTabController(EmployeesTab employeesPanel) {
		this.panel = employeesPanel;
		SpatialObjectSelectionChangeObservable.getInstance().subscribe(this);
	}


	/**
	 * Fires when spatial object is selected on zoo map canvas.
	 *
	 * @param spatialObjectModel selected spatial object model
	 */
	@Override
	public void spatialObjectSelectionChangedListener(SpatialObjectModel spatialObjectModel) {
		//TODO Do a reacton on NotifyObjectSelectonCHanged()
	}
}
