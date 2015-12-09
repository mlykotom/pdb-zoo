package cz.vutbr.fit.pdb.ateam.model.spatial;

import oracle.spatial.geometry.JGeometry;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * Representation of multipoint model. Renders as circle in Swing (point wouldn't be visible)
 * @author Tomas Mlynaric
 */
public class SpatialMultiPointModel extends SpatialPointModel {
	public SpatialMultiPointModel(String name, SpatialObjectTypeModel type, JGeometry geometry) {
		super(name, type, geometry);
	}

	@Override
	public Shape createShape() {
		Point2D[] points = geometry.getJavaPoints();
		GeneralPath p = new GeneralPath();

		for(Point2D point : points){
			Ellipse2D el = new Ellipse2D.Double(point.getX(), point.getY(), SPATIAL_POINT_SIZE, SPATIAL_POINT_SIZE);
			p.append(el, false);
		}

		return p;
	}

	@Override
	public boolean isWithin(int x, int y) {
		return shape.intersects(x - INTERSECT_BOX_SIZE / 2,  y - INTERSECT_BOX_SIZE / 2, INTERSECT_BOX_SIZE, INTERSECT_BOX_SIZE);
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
