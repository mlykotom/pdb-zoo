package cz.vutbr.fit.pdb.ateam.model;

/**
 * Created by Tomas on 10/25/2015.
 */
public class EmployeeModel extends BaseModel {
	private String surname;
	private long location;

	public EmployeeModel(long id, String name, String surname, Long location) {
		super(id, name);
		this.surname = surname;
		this.location = location;
	}

	public long getLocation() {
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

	@Override
	public String getTableName() {
		return "Employees";
	}
}
