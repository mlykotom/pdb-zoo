package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.gui.tabs.EmployeesTab;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;
import cz.vutbr.fit.pdb.ateam.observer.ContentPanelObserverSubject;

/**
 * Created by Tomas on 10/24/2015.
 */
public class EmployeesTabController extends Controller implements ContentPanelObserverSubject.ObjectSelectionChangedListener {
	private EmployeesTab panel;

	public EmployeesTabController(EmployeesTab employeesPanel) {
		this.panel = employeesPanel;
		ContentPanelObserverSubject.getInstance().subscribeForSelectionChange(this);
	}

	@Override
	public void notifyObjectSelectionChanged(SpatialObjectModel spatialObjectModel) {
		//TODO Do a reacton on NotifyObjectSelectonCHanged()
	}
}
