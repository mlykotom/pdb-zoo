package model;

/**
 * Created by mlyko on 20.10.2015.
 */
abstract public class BaseModel {
	private long id;
	protected String name;

	public BaseModel(long id) {
		this.id = id;
	}

	public BaseModel(long id, String name) {
		this.id = id;
		this.name = name;
	}

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
	 * @return
	 */
	public boolean isNew(){
		return this.id == 0;
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
