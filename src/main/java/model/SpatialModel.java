package model;

import oracle.spatial.geometry.JGeometry;

import java.awt.*;

/**
 * Created by mlyko on 20.10.2015.
 */
public class SpatialModel extends BaseModel {
	private JGeometry dbGeometry;
	private Shape guiShape;
	private SpatialTypeModel spatialType;

	public SpatialModel(long id) {
		super(id);
	}
}
