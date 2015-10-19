package model;

import oracle.spatial.geometry.JGeometry;

import java.awt.*;

/**
 * Class represents one row in Spatial_Object table. Spatial object
 * represents one building at the zoo map which contains its coordinates,
 * shape and type of the object.
 *
 * @author Jakub Tutko
 */
public class SpatialObject {

	private Long id;
	private SpatialObjectType type;
	private JGeometry geometry;
	private Shape shape;

	public SpatialObject(Long id, SpatialObjectType type, JGeometry geometry, Shape shape) {
		this.id = id;
		this.type = type;
		this.geometry = geometry;
		this.shape = shape;
	}

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public Long getId() {
		return id;
	}

	public SpatialObjectType getType() {
		return type;
	}

	public JGeometry getGeometry() {
		return geometry;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SpatialObject that = (SpatialObject) o;

		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (type != null ? !type.equals(that.type) : that.type != null) return false;
		return !(shape != null ? !shape.equals(that.shape) : that.shape != null);

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (shape != null ? shape.hashCode() : 0);
		return result;
	}
}
