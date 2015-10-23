package cz.vutbr.fit.pdb.ateam.model;

/**
 * Generic model containing data which should be in any model in system
 * Created by Tomas Mlynaric on 20.10.2015.
 */
abstract public class BaseModel {
	private long id;
	protected String name;
	protected boolean isChanged = false;

	public BaseModel(long id) {
		this.id = id;
	}

	public BaseModel(long id, String name) {
		this.id = id;
		this.name = name;
	}

//	abstract public boolean save();


	/**
	 * Serves for manipulating with any model in DataManager
	 * @return
	 */
	abstract public String getTableName();


	/**
	 * Compares id && name for
	 *
	 * @param o
	 * @return
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BaseModel that = (BaseModel) o;
		return !(getId() != null ? !getId().equals(that.getId()) : that.getId() != null) && !(getName() != null ? !getName().equals(that.getName()) : that.getName() != null);
	}

	/**
	 * Creates hash from model based on id && name
	 *
	 * @return
	 */
	@Override
	public int hashCode() {
		int result = getId() != null ? getId().hashCode() : 0;
		result = 31 * result + (getName() != null ? getName().hashCode() : 0);
		return result;
	}

	/**
	 * Checks if model new based on Id
	 *
	 * @return
	 */
	public boolean isNew() {
		return this.id == 0;
	}

	public void setIsChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}

	public boolean isChanged() {
		return isChanged;
	}

	public Long getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
