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
