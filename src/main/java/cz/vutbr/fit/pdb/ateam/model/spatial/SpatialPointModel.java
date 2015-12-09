package cz.vutbr.fit.pdb.ateam.model.spatial;

import oracle.spatial.geometry.JGeometry;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Representation of point cz.vutbr.fit.pdb.ateam.model. Renders as circle in Swing (point wouldn't be visible)
 * Created by Tomas Mlynaric on 20.10.2015.
 */
public class SpatialPointModel extends SpatialObjectModel {

	protected static final double SPATIAL_POINT_SIZE = 8;

	public SpatialPointModel(String name, SpatialObjectTypeModel type, JGeometry geometry) {
		super(name, type, geometry);
	}

	@Override
	public Shape createShape() {
		double[] ordinates = geometry.getPoint();
		return new Ellipse2D.Double(ordinates[0], ordinates[1], SPATIAL_POINT_SIZE, SPATIAL_POINT_SIZE);
	}

	/**
	 * Point is not mentioned to scale!
	 *
	 * @param mouseWheelRotation specifies amount of scale
	 */
	@Override
	public boolean scaleOnCanvas(int mouseWheelRotation) {
		return false;
	}
}
