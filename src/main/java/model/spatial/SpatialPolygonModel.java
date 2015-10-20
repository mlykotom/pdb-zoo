package model.spatial;

import exception.DataManagerException;
import oracle.spatial.geometry.JGeometry;

/**
 * Created by Tomas Mlynaric on 20.10.2015.
 */
public class SpatialPolygonModel extends SpatialObjectModel {
	public SpatialPolygonModel(long id, SpatialObjectTypeModel type, JGeometry geometry) throws Exception {
		super(id, type, geometry);
	}

	@Override
	public void regenerateShape(){
		this.shape = geometry.createShape();
	}
}
