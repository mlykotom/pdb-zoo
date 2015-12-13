package cz.vutbr.fit.pdb.ateam.model.spatial;

import cz.vutbr.fit.pdb.ateam.model.BaseModel;

import java.awt.*;

/**
 * Type of object in system (basket, path, trash, ...)
 * Created by Tomas Mlynaric on 20.10.2015.
 */
public class SpatialObjectTypeModel extends BaseModel {
	public static SpatialObjectTypeModel UnknownSpatialType = new SpatialObjectTypeModel(NULL_ID, "<< Basic >>", "b8b894");
	private java.awt.Color color;

	public SpatialObjectTypeModel(long id, String name, String colorHex) {
		super(id, name);
		setColor(colorHex);
	}

	public Color getColor() {
		return color;
	}

	/**
	 * Sets color from hex number
	 *
	 * @param colorHex hex format of color without # char!
	 */
	public void setColor(String colorHex) {
		this.color = Color.decode(String.format("#%s", colorHex));
	}

	@Override
	public String getTableName() {
		return "Spatial_Object_Types";
	}

	@Override
	public String toString() {
		return getName();
	}
}
