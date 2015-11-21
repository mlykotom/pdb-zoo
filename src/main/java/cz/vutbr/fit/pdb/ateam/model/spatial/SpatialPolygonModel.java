package cz.vutbr.fit.pdb.ateam.model.spatial;

import oracle.spatial.geometry.JGeometry;

import java.awt.*;

/**
 * Representation of polygon cz.vutbr.fit.pdb.ateam.model in ODB & Shape in Swing
 * Created by Tomas Mlynaric on 20.10.2015.
 */
public class SpatialPolygonModel extends SpatialObjectModel {
	public SpatialPolygonModel(String name, SpatialObjectTypeModel type, JGeometry geometry) {
		super(name, type, geometry);
	}

	@Override
	public Shape createShape() {
		return geometry.createShape();
	}
}
