package model;

import java.util.HashSet;
import java.util.Set;

/**
 * Class represents one row in Spatial_Object_Type table.
 *
 * @author Jakub Tutko
 */
public class SpatialObjectType {
	private Long id;
	private String type;
	private Set<SpatialObject> spatialObjects;

	public SpatialObjectType(Long id, String type) {
		this.id = id;
		this.type = type;
		this.spatialObjects = new HashSet<>();
	}

	public Long getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public void addSpatialObject(SpatialObject object) {
		spatialObjects.add(object);
	}

	public void deleteSpatialObject(SpatialObject object) {
		spatialObjects.remove(object);
	}

	public Set<SpatialObject> getSpatialObjects() {
		return spatialObjects;
	}

	public void setSpatialObjects(Set<SpatialObject> spatialObjects) {
		this.spatialObjects = spatialObjects;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SpatialObjectType that = (SpatialObjectType) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (type != null ? !type.equals(that.type) : that.type != null) return false;
		return !(spatialObjects != null ? !spatialObjects.equals(that.spatialObjects) : that.spatialObjects != null);

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (spatialObjects != null ? spatialObjects.hashCode() : 0);
		return result;
	}
}
