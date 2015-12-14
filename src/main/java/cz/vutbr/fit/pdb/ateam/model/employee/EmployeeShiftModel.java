package cz.vutbr.fit.pdb.ateam.model.employee;

import cz.vutbr.fit.pdb.ateam.model.BaseModel;

/**
 * @author Tomas Hanus on 12/3/2015.
 */
public class EmployeeShiftModel extends BaseModel {
	private static final String TABLE_NAME = "Employees_Shift";

	public EmployeeShiftModel(long id, String name) {
		super(id, name);
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}
}
