package model;

import exception.DataManagerException;
import oracle.spatial.geometry.JGeometry;

import java.awt.*;

/**
 * Created by mlyko on 20.10.2015.
 */
public class SpatialObjectModel extends BaseModel {
	private JGeometry geometry;
	private Shape shape;
	private SpatialObjectTypeModel spatialObjectType;

	public SpatialObjectModel(long id, SpatialObjectTypeModel type, byte[] rawGeometry) throws Exception {
		super(id);
		this.spatialObjectType = type;
		this.geometry = JGeometry.load(rawGeometry);
		regenerateShape();
	}

	/**
	 * Creates new shape based on models jGeometry type
	 * @throws DataManagerException
	 */
	public void regenerateShape() throws DataManagerException {
		Shape shape;
		switch (geometry.getType()) {
			case JGeometry.GTYPE_POLYGON:
				shape = geometry.createShape();
				break;
			default:
				throw new DataManagerException("jGeometry2Shape: Can not convert jGeometry!");
		}
		this.shape = shape;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SpatialObjectModel that = (SpatialObjectModel) o;
		return !(this.getId() != null ? !getId().equals(that.getId()) : that.getId() != null) && !(getType() != null ? !getType().equals(that.getType()) : that.getType() != null) && !(shape != null ? !shape.equals(that.shape) : that.shape != null);
	}

	@Override
	public int hashCode() {
		int result = getId() != null ? getId().hashCode() : 0;
		result = 31 * result + (getType() != null ? getType().hashCode() : 0);
		result = 31 * result + (shape != null ? shape.hashCode() : 0);
		return result;
	}

	// ---- GETTERS && SETTERS ---- //

	public Shape getShape() {
		return shape;
	}

	public SpatialObjectTypeModel getType() {
		return spatialObjectType;
	}

	public JGeometry getGeometry() {
		return geometry;
	}
}
