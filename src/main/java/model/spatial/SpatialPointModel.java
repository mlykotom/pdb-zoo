package model.spatial;

import oracle.spatial.geometry.JGeometry;

import java.awt.geom.Ellipse2D;

/**
 * Created by Tomas Mlynaric on 20.10.2015.
 */
public class SpatialPointModel extends SpatialObjectModel {

	private static final double SPATIAL_POINT_SIZE = 10;

	public SpatialPointModel(long id, SpatialObjectTypeModel type, JGeometry geometry) throws Exception {
		super(id, type, geometry);
	}

	@Override
	public void regenerateShape(){
		double[] ordinates = geometry.getPoint();
		shape = new Ellipse2D.Double(ordinates[0], ordinates[1], SPATIAL_POINT_SIZE, SPATIAL_POINT_SIZE);
	}
}
