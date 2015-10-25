package cz.vutbr.fit.pdb.ateam.model.spatial;

import cz.vutbr.fit.pdb.ateam.exception.ModelException;
import cz.vutbr.fit.pdb.ateam.gui.map.ZooMapCanvas;
import cz.vutbr.fit.pdb.ateam.model.BaseModel;
import oracle.spatial.geometry.JGeometry;
import org.w3c.dom.css.Rect;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Abstract representation of object in spatial DB.
 * Created by Tomas Mlynaric on 20.10.2015.
 */
abstract public class SpatialObjectModel extends BaseModel {
	private static final Paint DEFAULT_BORDER_COLOR = Color.BLACK;
	private static final BasicStroke DEFAULT_STROKE_SIZE = new BasicStroke(1);

	protected JGeometry geometry;
	protected Shape shape;
	protected SpatialObjectTypeModel spatialObjectType;
	protected Paint borderColor = DEFAULT_BORDER_COLOR;
	protected BasicStroke strokeSize = DEFAULT_STROKE_SIZE;

	/**
	 * Setups object and creates shape for graphic representation from jGeometry.
	 * It's protected so that it's not possible to instantiate the class
	 * otherwise than by {@link #createFromType(Long, SpatialObjectTypeModel, byte[])}
	 *
	 * @param id
	 * @param type     association to object type (basket, house, path, ...)
	 * @param geometry spatial data
	 */
	protected SpatialObjectModel(long id, SpatialObjectTypeModel type, JGeometry geometry) {
		super(id);
		this.spatialObjectType = type;
		this.geometry = geometry;
		regenerateShape();
	}

	/**
	 * Creates specific SpatialObject based on type from JGeometry which is served in raw format
	 *
	 * @param id
	 * @param spatialType association to object type (basket, house, path, ...)
	 * @param rawGeometry data from DB query result
	 * @return
	 * @throws Exception
	 */
	public static SpatialObjectModel createFromType(Long id, SpatialObjectTypeModel spatialType, byte[] rawGeometry) throws Exception {
		JGeometry geometry = JGeometry.load(rawGeometry);
		SpatialObjectModel newModel;
		switch (geometry.getType()) {

			case JGeometry.GTYPE_POLYGON:
				newModel = new SpatialPolygonModel(id, spatialType, geometry);
				break;


			case JGeometry.GTYPE_POINT:
				newModel = new SpatialPointModel(id, spatialType, geometry);
				break;

			default:
				throw new ModelException("createFromType: Not existing type of SpatialObjectModel");
		}

		return newModel;
	}

	/**
	 * Creates new shape based on class type
	 */
	abstract public Shape createShape();

	public void regenerateShape(){
		shape = createShape();
	}

	/**
	 * Renders spatial object into canvas
	 *
	 * @param g2D reference to canvas object
	 */
	public void render(Graphics2D g2D) {
		Shape shape = getShape();
		g2D.setPaint(getType().getColor());
		g2D.fill(shape);
		g2D.setStroke(strokeSize);
		g2D.setPaint(borderColor);
		g2D.draw(shape);
	}

	/**
	 * Determines if spatial object is selected based on his shape in canvas
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isWithin(int x, int y) {
		return getShape().contains(x, y);
	}

	/**
	 * Determines graphics while is selected or not
	 *
	 * @param isSelected
	 */
	public void selectOnCanvas(boolean isSelected) {
		if (isSelected) {
			borderColor = Color.decode("#4F6CB2");
			strokeSize = new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		} else {
			borderColor = DEFAULT_BORDER_COLOR;
			strokeSize = DEFAULT_STROKE_SIZE;
		}
	}

	/**
	 * Moves object some pixels defined by parameters
	 *
	 * @param deltaX
	 * @param deltaY
	 */
	public void moveOnCanvas(int deltaX, int deltaY) {
		try {
			Rectangle2D boundRect = shape.getBounds2D();

			if(boundRect.getY() + deltaY < 0) deltaY = (int) (-1 * boundRect.getY());
			if(boundRect.getY() + boundRect.getHeight() + deltaY > ZooMapCanvas.CANVAS_DEFAULT_HEIGHT) deltaY = (int) (ZooMapCanvas.CANVAS_DEFAULT_HEIGHT - boundRect.getY() - boundRect.getHeight());

			if(boundRect.getX() + deltaX < 0) deltaX = (int) (-1 * boundRect.getX());
			if(boundRect.getX() + boundRect.getWidth() + deltaX > ZooMapCanvas.CANVAS_DEFAULT_WIDTH) deltaX = (int) (ZooMapCanvas.CANVAS_DEFAULT_WIDTH - boundRect.getX() - boundRect.getWidth());

			geometry = geometry.affineTransforms(true, deltaX, deltaY, 0, false, null, 0, 0, 0, false, null, null, 0, 0, false, 0, 0, 0, 0, 0, 0, false, null, null, 0, false, new double[]{}, new double[]{});
			regenerateShape();
			setIsChanged(true);
		} catch (Exception e1) {
			e1.printStackTrace();
			// TODO should show some kind of error!!
		}
	}

	/**
	 * Scales object on canvas
	 *
	 * @param mouseWheelRotation specifies amount of scale
	 */
	public void scaleOnCanvas(int mouseWheelRotation) {
		double[] firstPoint = geometry.getFirstPoint();
		JGeometry staticPoint = new JGeometry(firstPoint[0], firstPoint[1], 0);
		float amount = 1 + (mouseWheelRotation * 0.05f);

		try {
			geometry = geometry.affineTransforms(false, 0, 0, 0, true, staticPoint, amount, amount, 0, false, null, null, 0, 0, false, 0, 0, 0, 0, 0, 0, false, null, null, 0, false, new double[]{}, new double[]{});
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}

		setIsChanged(true);
		regenerateShape();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SpatialObjectModel that = (SpatialObjectModel) o;
		return !(this.getId() != null ? !getId().equals(that.getId()) : that.getId() != null) && !(getType() != null ? !getType().equals(that.getType()) : that.getType() != null) && !(shape != null ? !shape.equals(that.shape) : that.shape != null);
	}

	@Override
	public int hashCode() {
		int result = getId() != null ? getId().hashCode() : 0;
		result = 31 * result + (getType() != null ? getType().hashCode() : 0);
		result = 31 * result + (shape != null ? shape.hashCode() : 0);
		return result;
	}

	// ---- GETTERS && SETTERS ---- //


	@Override
	public String getTableName() {
		return "Spatial_Objects";
	}

	public Shape getShape() {
		return shape;
	}

	public SpatialObjectTypeModel getType() {
		return spatialObjectType;
	}

	public JGeometry getGeometry() {
		return geometry;
	}

	public void setGeometry(JGeometry geometry) {
		this.geometry = geometry;
	}
}
