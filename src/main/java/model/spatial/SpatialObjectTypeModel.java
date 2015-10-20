package model.spatial;

import model.BaseModel;

import java.awt.*;

/**
 * Type of object in system (basket, path, trash, ...)
 * Created by Tomas Mlynaric on 20.10.2015.
 */
public class SpatialObjectTypeModel extends BaseModel {
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
}
