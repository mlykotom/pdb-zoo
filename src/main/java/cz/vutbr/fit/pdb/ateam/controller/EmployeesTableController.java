package cz.vutbr.fit.pdb.ateam.controller;

import cz.vutbr.fit.pdb.ateam.model.employee.EmployeeModel;

/**
 * Created by Tomas on 11/28/2015.
 */
public interface EmployeesTableController {
	void EmployeesTableEditAction(EmployeeModel employeeModel);
	void EmployeesTableDeleteAction(EmployeeModel employeeModel);
}
