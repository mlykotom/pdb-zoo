package cz.vutbr.fit.pdb.ateam.model;

import java.util.Collection;
import java.util.HashMap;

/**
 * Generic model containing data which should be in any model in system
 *
 * @author Tomas Mlynaric
 */
abstract public class BaseModel {
	public static final String NEW_MODEL_NAME = "<< New >>";
	public static final long NULL_ID = -1L;
	protected long id;
	protected String name;
	protected boolean isChanged = false;
	protected boolean isDeleted = false;
	private HashMap<String, Object> additionalInformations = new HashMap<>();

	public BaseModel(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public BaseModel() {
	}

	/**
	 * Finds model by specified Id
	 *
	 * @param Id       of any model, if null -> does not even try to find
	 * @param hayStack list of objects which inherits of BaseModel
	 * @param <T>      any model inheriting from BaseModel
	 * @return null if not found, otherwise found model
	 */
	public static <T extends BaseModel> T findById(Long Id, Collection<T> hayStack) {
		if (Id == null) return null;

		for (T obj : hayStack) {
			if (Id.equals(obj.getId())) return obj;
		}

		return null;
	}

	/**
	 * Finds model by specified name
	 *
	 * @param name     of any model, if null -> does not even try to find
	 * @param hayStack list of objects which inherits of BaseModel
	 * @param <T>      any model inheriting from BaseModel
	 * @return null if not found, otherwise found model
	 */
	public static <T extends BaseModel> T findByName(String name, Collection<T> hayStack) {
		if (name == null) return null;

		for (T obj : hayStack) {
			if (name.equals(obj.getName())) return obj;
		}

		return null;
	}

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

	public HashMap<String, Object> getAdditionalInformations() {
		return additionalInformations;
	}

	public boolean isNew() {
		return this.id == 0;
	}

	public void setIsChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}

	public boolean isChanged() {
		return isNew() || isChanged;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean deleted) {
		isDeleted = deleted;
	}
}
