package cz.vutbr.fit.pdb.ateam.model.spatial;

import oracle.spatial.geometry.JGeometry;

import java.awt.*;
import java.awt.geom.Path2D;

/**
 * @author Tomas Mlynaric
 */
public class SpatialLineStringModel extends SpatialObjectModel {
	/**
	 * Setups object and creates shape for graphic representation from jGeometry.
	 * It's protected so that it's not possible to instantiate the class
	 * otherwise than by {@link #loadFromDB(Long, String, SpatialObjectTypeModel, byte[])}
	 *
	 * @param name
	 * @param type     association to object type (basket, house, path, ...)
	 * @param geometry spatial data
	 */
	public SpatialLineStringModel(String name, SpatialObjectTypeModel type, JGeometry geometry) {
		super(name, type, geometry);
	}

	@Override
	protected BasicStroke getDefaultStroke() {
		return new BasicStroke(4);
	}

	@Override
	protected Paint getDefaultBorderColor() {
		return getType().getColor();
	}

	@Override
	public Shape createShape() {
		double[] points = geometry.getOrdinatesArray();
		// line should have at least 2 points
		if (points.length < 2) geometry.createShape();

		Path2D path = new Path2D.Double();
		path.moveTo(points[0], points[1]);

		double pointX, pointY;

		int i = 2;  // skip first point (2 coords) because its movedTo
		while (i < points.length) {
			pointX = points[i++];
			pointY = points[i++];

			path.lineTo(pointX, pointY);
		}

		i -= 2;   // skip last point (2 coords) because we are in this point
		while (i > 0) {
			pointY = points[--i];
			pointX = points[--i];

			path.lineTo(pointX, pointY);
		}

		return path;
	}

	@Override
	public boolean isWithin(int x, int y) {
		return shape.intersects(x - INTERSECT_BOX_SIZE / 2, y - INTERSECT_BOX_SIZE / 2, INTERSECT_BOX_SIZE, INTERSECT_BOX_SIZE);
	}
}
