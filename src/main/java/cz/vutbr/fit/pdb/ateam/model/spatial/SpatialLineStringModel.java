package cz.vutbr.fit.pdb.ateam.model.spatial;

import oracle.spatial.geometry.JGeometry;

import java.awt.*;

/**
 * Created by Tomas Mlynaric on 25.10.2015.
 */
public class SpatialLineStringModel extends SpatialObjectModel {
	/**
	 * Setups object and creates shape for graphic representation from jGeometry.
	 * It's protected so that it's not possible to instantiate the class
	 * otherwise than by {@link #loadFromDB(Long, String, SpatialObjectTypeModel, byte[])}
	 *  @param name
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
		return geometry.createShape();
	}

	@Override
	public void render(Graphics2D g2D) {
		Shape shape = getShape();
		g2D.setPaint(getType().getColor());
		g2D.setStroke(stroke);
		g2D.setPaint(borderColor);
		g2D.draw(shape);
	}
}
