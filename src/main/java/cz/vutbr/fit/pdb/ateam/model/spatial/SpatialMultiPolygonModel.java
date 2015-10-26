package cz.vutbr.fit.pdb.ateam.model.spatial;

import oracle.spatial.geometry.JGeometry;

import java.awt.*;

/**
 * Created by Tomas Mlynaric on 26.10.2015.
 */
public class SpatialMultiPolygonModel extends SpatialObjectModel {
	public SpatialMultiPolygonModel(Long id, String name, SpatialObjectTypeModel spatialType, JGeometry geometry) {
		super(id, name, spatialType, geometry);
	}

	@Override
	public Shape createShape() {
		return geometry.createShape();
	}
}
