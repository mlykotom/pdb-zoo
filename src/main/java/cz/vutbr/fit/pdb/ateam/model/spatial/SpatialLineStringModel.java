package cz.vutbr.fit.pdb.ateam.model.spatial;

import oracle.spatial.geometry.JGeometry;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Created by Tomas Mlynaric on 25.10.2015.
 */
public class SpatialLineStringModel extends SpatialObjectModel {
	/**
	 * Setups object and creates shape for graphic representation from jGeometry.
	 * It's protected so that it's not possible to instantiate the class
	 * otherwise than by {@link #createFromType(Long, String, SpatialObjectTypeModel, byte[])}
	 *
	 * @param id
	 * @param name
	 * @param type     association to object type (basket, house, path, ...)
	 * @param geometry spatial data
	 */
	protected SpatialLineStringModel(long id, String name, SpatialObjectTypeModel type, JGeometry geometry) {
		super(id, name, type, geometry);
	}

	@Override
	public Shape createShape() {

		//geometry = JGeometry.createLinearLineString(geometry.getOrdinatesArray(), 0, 0);

		Shape x = geometry.createShape();
		return x;
	}
}
