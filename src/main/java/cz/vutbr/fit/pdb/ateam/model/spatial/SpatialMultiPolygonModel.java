package cz.vutbr.fit.pdb.ateam.model.spatial;

import oracle.spatial.geometry.JGeometry;

import java.awt.*;

/**
 * @author Tomas Mlynaric
 */
public class SpatialMultiPolygonModel extends SpatialObjectModel {
	public SpatialMultiPolygonModel(String name, SpatialObjectTypeModel spatialType, JGeometry geometry) {
		super(name, spatialType, geometry);
	}

	@Override
	public Shape createShape() {
		return geometry.createShape();
	}
}
