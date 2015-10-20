package model.spatial;

import exception.DataManagerException;
import model.BaseModel;
import oracle.spatial.geometry.JGeometry;

import java.awt.*;

/**
 * Created by mlyko on 20.10.2015.
 */
abstract public class SpatialObjectModel extends BaseModel {
	protected JGeometry geometry;
	protected Shape shape;
	protected SpatialObjectTypeModel spatialObjectType;

	protected SpatialObjectModel(long id, SpatialObjectTypeModel type, JGeometry geometry) throws Exception {
		super(id);
		this.spatialObjectType = type;
		this.geometry = geometry;
		regenerateShape();
	}

	/**
	 * Creates specific SpatialObject based on type from JGeometry which is served in raw format
	 * @param id
	 * @param spatialType
	 * @param rawGeometry
	 * @return
	 * @throws Exception
	 */
	public static SpatialObjectModel createFromType(Long id, SpatialObjectTypeModel spatialType, byte[] rawGeometry) throws Exception {
		JGeometry geometry = JGeometry.load(rawGeometry);
		SpatialObjectModel newModel;
		switch(geometry.getType()){

			case JGeometry.GTYPE_POLYGON:
				newModel = new SpatialPolygonModel(id, spatialType, geometry);
				break;


			case JGeometry.GTYPE_POINT:
				newModel = new SpatialPointModel(id, spatialType, geometry);
				break;

			default:
				// TODO model exception?
				throw new DataManagerException("Not existing type of SpatialObjectModel");
		}

		return newModel;
	}

	/**
	 * Creates new shape based on class type
	 */
	abstract public void regenerateShape();

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

	public void setGeometry(JGeometry geometry) {
		this.geometry = geometry;
	}
}
