package model;

/**
 * Created by mlyko on 20.10.2015.
 */
public class SpatialObjectTypeModel extends BaseModel {
	public java.awt.Color color;

	public SpatialObjectTypeModel(long id, String name) {
		super(id, name);
		// TODO will have constructor with color string (DB representation)
		//this.color = Color.getColor() // is it HEX?
	}
}
