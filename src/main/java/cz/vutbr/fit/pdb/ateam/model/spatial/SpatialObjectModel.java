package cz.vutbr.fit.pdb.ateam.model.spatial;

import cz.vutbr.fit.pdb.ateam.exception.ModelException;
import cz.vutbr.fit.pdb.ateam.gui.map.ZooMapCanvas;
import cz.vutbr.fit.pdb.ateam.model.BaseModel;
import cz.vutbr.fit.pdb.ateam.utils.Logger;
import oracle.spatial.geometry.JGeometry;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Abstract representation of object in spatial DB.
 * Created by Tomas Mlynaric on 20.10.2015.
 */
abstract public class SpatialObjectModel extends BaseModel {
	private static final Paint DEFAULT_BORDER_COLOR = Color.BLACK;
	private static final BasicStroke DEFAULT_STROKE = new BasicStroke(1);

	protected JGeometry geometry;
	protected Shape shape;
	protected SpatialObjectTypeModel spatialObjectType;
	protected Paint borderColor;
	protected BasicStroke stroke;

	private enum IsInMapAxis {
		AXIS_Y,
		AXIS_X
	}

	protected BasicStroke getDefaultStroke(){
		return DEFAULT_STROKE;
	}

	protected Paint getDefaultBorderColor(){
		return DEFAULT_BORDER_COLOR;
	}

	/**
	 * Setups object and creates shape for graphic representation from jGeometry.
	 * It's protected so that it's not possible to instantiate the class
	 * otherwise than by {@link #createFromType(Long, String, SpatialObjectTypeModel, byte[])}
	 *  @param id
	 * @param name
	 * @param type     association to object type (basket, house, path, ...)
	 * @param geometry spatial data
	 */
	protected SpatialObjectModel(long id, String name, SpatialObjectTypeModel type, JGeometry geometry) {
		super(id);
		this.name = name;
		this.spatialObjectType = type;
		this.geometry = geometry;
		// children models may override color or width of border
		this.stroke = getDefaultStroke();
		this.borderColor = getDefaultBorderColor();
		regenerateShape();
	}

	/**
	 * Creates specific SpatialObject based on type from JGeometry which is served in raw format
	 *
	 * @param id
	 * @param name
	 * @param spatialType association to object type (basket, house, path, ...)
	 * @param rawGeometry data from DB query result
	 * @return
	 * @throws Exception
	 */
	public static SpatialObjectModel createFromType(Long id, String name, SpatialObjectTypeModel spatialType, byte[] rawGeometry) throws Exception {
		JGeometry geometry = JGeometry.load(rawGeometry);
		SpatialObjectModel newModel;

		switch (geometry.getType()) {

			case JGeometry.GTYPE_MULTIPOLYGON:
				// TODO should be different!
			case JGeometry.GTYPE_COLLECTION:
				newModel = new SpatialMultiPolygonModel(id, name, spatialType, geometry);
				break;

			case JGeometry.GTYPE_POLYGON:
				newModel = new SpatialPolygonModel(id, name, spatialType, geometry);
				break;

			case JGeometry.GTYPE_POINT:
				newModel = new SpatialPointModel(id, name, spatialType, geometry);
				break;

			case JGeometry.GTYPE_CURVE:
				newModel = new SpatialLineStringModel(id, name, spatialType, geometry);
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

	public void regenerateShape() {
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
		g2D.setStroke(stroke);
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
			stroke = new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		} else {
			borderColor = getDefaultBorderColor();
			stroke = getDefaultStroke();
		}
	}

	/**
	 * Moves object some pixels defined by parameters
	 *  @param deltaX
	 * @param deltaY
	 */
	public boolean moveOnCanvas(int deltaX, int deltaY) {
		try {
			Rectangle2D boundRect = shape.getBounds2D();
			deltaX = isMovedInMap(boundRect, deltaX, IsInMapAxis.AXIS_X);
			deltaY = isMovedInMap(boundRect, deltaY, IsInMapAxis.AXIS_Y);
			geometry = geometry.affineTransforms(true, deltaX, deltaY, 0, false, null, 0, 0, 0, false, null, null, 0, 0, false, 0, 0, 0, 0, 0, 0, false, null, null, 0, false, new double[]{}, new double[]{});
			regenerateShape();
			setIsChanged(true);
			return true;
		} catch (Exception e) {
			Logger.createLog(Logger.ERROR_LOG, "moveOnCanvas exception" + e.getMessage());
			return false;
		}
	}

	/**
	 * Scales object on canvas
	 *
	 * @param mouseWheelRotation specifies amount of scale
	 */
	public boolean scaleOnCanvas(int mouseWheelRotation) {
		try {
			Rectangle2D boundRect = shape.getBounds2D();
			double delta = 1 + (mouseWheelRotation * 0.05f);
			delta = isScaledInMape(boundRect, delta);
			JGeometry staticPoint = new JGeometry(boundRect.getCenterX(), boundRect.getCenterY(), 0);
			geometry = geometry.affineTransforms(false, 0, 0, 0, true, staticPoint, delta, delta, 0, false, null, null, 0, 0, false, 0, 0, 0, 0, 0, 0, false, null, null, 0, false, new double[]{}, new double[]{});
			setIsChanged(true);
			regenerateShape();
			return true;
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}
	}

	/**
	 * Checks boundaries of shape if is in map base on which axis calculates
	 *
	 * @param boundRect
	 * @param delta     delta X or Y
	 * @param axisType
	 * @return changed delta based on if is in map
	 */
	private int isMovedInMap(Rectangle2D boundRect, double delta, IsInMapAxis axisType) {
		double getOrd;
		double getDimen;
		int maxDimen;
		switch (axisType) {
			case AXIS_Y:
				getOrd = boundRect.getY();
				getDimen = boundRect.getHeight();
				maxDimen = ZooMapCanvas.CANVAS_DEFAULT_HEIGHT;
				break;

			case AXIS_X:
				getOrd = boundRect.getX();
				getDimen = boundRect.getWidth();
				maxDimen = ZooMapCanvas.CANVAS_DEFAULT_WIDTH;
				break;

			default:
				return (int) delta;
		}

		// cares about left or top canvas boundary
		if (getOrd + delta < 0) {
			delta = (int) (-1 * getOrd);
		}

		// cares about right or bottom canvas boundary
		if (getOrd + getDimen + delta > maxDimen) {
			delta = (int) (maxDimen - getOrd - getDimen);
		}

		// needs to return int because canvas ordinates are discrete!
		return (int) delta;
	}

	/**
	 * Recalculates scale amount by objects' boundary rentangle's ordinates and size
	 *
	 * @param boundRect
	 * @param delta     amount which will be scaled
	 * @return changed delta amount
	 */
	private double isScaledInMape(Rectangle2D boundRect, double delta) {
		double centerX = boundRect.getCenterX();
		double halfWidth = boundRect.getWidth() / 2.0;
		double centerY = boundRect.getCenterY();
		double halfHeight = boundRect.getHeight() / 2.0;

		// cares about X-axis (left canvas boundary)
		if ((centerX - halfWidth * delta) < 0) {
			delta = boundRect.getX() / halfWidth + 1;
		}
		// cares about X-axis (right canvas boundary)
		if ((centerX + halfWidth * delta) > ZooMapCanvas.CANVAS_DEFAULT_WIDTH) {
			delta = (ZooMapCanvas.CANVAS_DEFAULT_WIDTH - boundRect.getX() - boundRect.getWidth()) / halfWidth + 1;
		}
		// cares about Y-axis (top canvas boundary)
		if ((centerY - halfHeight * delta) < 0) {
			delta = boundRect.getY() / halfHeight + 1;
		}
		// cares about Y-axis (bottom canvas boundary)
		if ((centerY + halfHeight * delta) > ZooMapCanvas.CANVAS_DEFAULT_HEIGHT) {
			delta = (ZooMapCanvas.CANVAS_DEFAULT_HEIGHT - boundRect.getY() - boundRect.getHeight()) / halfHeight + 1;
		}

		return delta;
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
