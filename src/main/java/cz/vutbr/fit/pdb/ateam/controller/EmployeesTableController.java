package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.model.EmployeeModel;
import cz.vutbr.fit.pdb.ateam.model.spatial.SpatialObjectModel;

/**
 * Created by Tomas on 11/28/2015.
 */
public interface EmployeesTableController {
	void EmployeesTableEditAction(EmployeeModel employeeModel);
	void EmployeesTableDeleteAction(EmployeeModel employeeModel);
}
