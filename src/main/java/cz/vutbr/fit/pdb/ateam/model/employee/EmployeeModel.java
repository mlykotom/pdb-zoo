package cz.vutbr.fit.pdb.ateam.model.employee;

import cz.vutbr.fit.pdb.ateam.model.BaseModel;

import java.util.Date;

/**
 * Created by Tomas on 10/25/2015.
 */
public class EmployeeModel extends BaseModel {
	private String surname;
	private Long location;
	private Date dateFrom;
	private Date dateTo;
	private long shiftID;

	public EmployeeModel(long id, String name, String surname, Long location, Date dateFrom, Date dateTo) {
		super(id, name);
		this.surname = surname;
		this.location = location;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
	}

	public EmployeeModel(long id, String name, String surname, Long location, Date dateFrom, Date dateTo, long shiftID) {
		super(id, name);
		this.surname = surname;
		this.location = location;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.shiftID = shiftID;
	}

	public EmployeeModel(String name, String surname) {
		new EmployeeModel(0, name, surname, Long.valueOf(0) ,null, null);
		// TODO: 12/7/2015 remove magic constants
		this.surname = surname;
	}




	public Long getLocation() {
		return location;
	}

	public void setLocation(long location) {
		this.location = location;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public long getShiftID() {
		return shiftID;
	}

	public void setShiftID(long shiftID) {
		this.shiftID = shiftID;
	}

	@Override
	public String getTableName() {
		return "Employees";
	}

	public String getTemporalTableName() {
		return "Employees_Shift";
	}
}
