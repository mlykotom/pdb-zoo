package model;

/**
 * Class represents one row in Spatial_Object_Type table. Object type
 * represents type of the building at the zoo map.
 *
 * @author Jakub Tutko
 */
public class SpatialObjectType {
	private Long id;
	private String type;

	public SpatialObjectType(Long id, String type) {
		this.id = id;
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SpatialObjectType that = (SpatialObjectType) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		return !(type != null ? !type.equals(that.type) : that.type != null);

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (type != null ? type.hashCode() : 0);
		return result;
	}
}
