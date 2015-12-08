package cz.vutbr.fit.pdb.ateam.model.spatial;

import oracle.spatial.geometry.JGeometry;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 * @author Tomas Mlynaric
 */
public class SpatialLineStringModel extends SpatialObjectModel {
	public static final int HIT_BOX_SIZE = 10;

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
		double[] points =  geometry.getOrdinatesArray();
		// line should have at least 2 points
		if(points.length < 2) geometry.createShape();

		Path2D path = new Path2D.Double();
		path.moveTo(points[0], points[1]);

		double pointX, pointY;

		int i = 2;  // skip first point (2 coords) because its movedTo
		while(i < points.length){
			pointX = points[i++];
			pointY = points[i++];

			path.lineTo(pointX, pointY);
		}

		i-=2;   // skip last point (2 coords) because we are in this point
		while(i > 0){
			pointY = points[--i];
			pointX = points[--i];

			path.lineTo(pointX, pointY);
		}

		return path;
	}

	@Override
	public void render(Graphics2D g2D) {
		Shape shape = getShape();
		g2D.setPaint(getType().getColor());
		g2D.setStroke(stroke);
		g2D.setPaint(borderColor);
		g2D.draw(shape);
	}

	@Override
	public boolean isWithin(int x, int y) {
		return shape.intersects(x - HIT_BOX_SIZE / 2,  y - HIT_BOX_SIZE / 2, HIT_BOX_SIZE, HIT_BOX_SIZE);
	}
}
