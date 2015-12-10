package cz.vutbr.fit.pdb.ateam.model;

import java.util.List;

/**
 * Generic model containing data which should be in any model in system
 * Created by Tomas Mlynaric on 20.10.2015.
 */
abstract public class BaseModel {
	public static final String NEW_MODEL_NAME = "<< New >>";
	protected long id;
	protected String name;
	protected boolean isChanged = false;
	protected boolean isDeleted = false;
	public static final long NULL_ID = -1L;


	public BaseModel() {
	}

	public BaseModel(long id, String name) {
		this.id = id;
		this.name = name;
	}

//	abstract public boolean saveModel();


	/**
	 * Serves for manipulating with any model in DataManager
	 *
	 * @return SQL table name
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
		return isNew() || isChanged;
	}

	// TODO shouln't be this way, because we can easily change object's primary key!
	public void setId(Long id) {
		this.id = id;
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

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean deleted) {
		isDeleted = deleted;
	}

	/**
	 * Finds model by specified Id
	 * @param Id of any model, if null -> does not even try to find
	 * @param hayStack list of objects which inherits of BaseModel
	 * @param <T> any model inheriting from BaseModel
	 * @return null if not found, otherwise found model
	 */
	public static <T extends BaseModel> T findById(Long Id, List<T> hayStack){
		if(Id == null) return null;

		for (T obj: hayStack) {
			if(Id.equals(obj.getId())) return obj;
		}

		return null;
	}
}
