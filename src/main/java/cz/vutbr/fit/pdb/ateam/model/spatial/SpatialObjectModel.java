package cz.vutbr.fit.pdb.ateam.model.spatial;

import cz.vutbr.fit.pdb.ateam.exception.ModelException;
import cz.vutbr.fit.pdb.ateam.gui.map.ZooMapCanvas;
import cz.vutbr.fit.pdb.ateam.model.BaseModel;
import cz.vutbr.fit.pdb.ateam.utils.Logger;
import cz.vutbr.fit.pdb.ateam.utils.Utils;
import oracle.spatial.geometry.JGeometry;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

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
	private boolean isSelected = false;

	private enum IsInMapAxis {
		AXIS_Y,
		AXIS_X
	}

	protected BasicStroke getDefaultStroke() {
		return DEFAULT_STROKE;
	}

	protected Paint getDefaultBorderColor() {
		return DEFAULT_BORDER_COLOR;
	}

	/**
	 * Setups object and creates shape for graphic representation from jGeometry.
	 * It's protected so that it's not possible to instantiate the class
	 * otherwise than by {@link #loadFromDB(Long, String, SpatialObjectTypeModel, byte[])}
	 *
	 * @param name     name of spatial object
	 * @param type     association to object type (basket, house, path, ...)
	 * @param geometry spatial data
	 */
	protected SpatialObjectModel(String name, SpatialObjectTypeModel type, JGeometry geometry) {
		super();
		this.name = name;
		setSpatialObjectType(type);
		this.geometry = geometry;
		// children models may override color or width of border
		this.stroke = getDefaultStroke();
		this.borderColor = getDefaultBorderColor();
		regenerateShape();
	}

	public enum ModelShape {
		// TODO might have properties for showing in "ComboBox"
		POINT("point", 1, 1),
		POLYGON("rectangle", 2, 2),
		LINE("line", 2, -1), // TODO should have infinite
		CIRCLE("circle", 2, 2);

		private int pointsToRenderCount;
		private int totalPointsCount;
		private String name;

		ModelShape(String name, int pointsToRenderCount, int totalPointsCount) {
			this.pointsToRenderCount = pointsToRenderCount;
			this.totalPointsCount = totalPointsCount;
			this.name = name;
		}

		public int getTotalPointsCount() {
			return totalPointsCount;
		}

		public int getPointsToRenderCount() {
			return pointsToRenderCount;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return getName();
		}
	}


	/**
	 * Creates specific SpatialObject based on data from DB (raw JGeometry data)
	 *
	 * @param id          specific id
	 * @param name        name of spatial object
	 * @param spatialType reference to {@link SpatialObjectTypeModel}
	 * @param rawGeometry data from DB query result
	 * @return new model with ID! (if saved, updates in DB)
	 * @throws ModelException
	 */
	public static SpatialObjectModel loadFromDB(Long id, String name, SpatialObjectTypeModel spatialType, byte[] rawGeometry) throws ModelException {
		JGeometry geometry = null;
		try {
			geometry = JGeometry.load(rawGeometry);
		} catch (Exception e) {
			throw new ModelException("loadFromDB: JGeometry data problem");
		}
		SpatialObjectModel model = createFromJGeometry(name, spatialType, geometry);
		// id is set here so that we can create model without Id (new model)
		model.id = id;
		return model;
	}

	/**
	 * Creates specific SpatialObject based on JGeometry
	 *
	 * @param name        name of spatial object
	 * @param spatialType reference to {@link SpatialObjectTypeModel}
	 * @param geometry    based on this, new model will be created
	 * @return new model without ID! (if saved, creates in DB)
	 * @throws ModelException
	 */
	public static SpatialObjectModel createFromJGeometry(String name, SpatialObjectTypeModel spatialType, JGeometry geometry) throws ModelException {
		SpatialObjectModel newModel;

		switch (geometry.getType()) {
			case JGeometry.GTYPE_MULTIPOLYGON:
				// TODO should be different!
			case JGeometry.GTYPE_COLLECTION:
				newModel = new SpatialMultiPolygonModel(name, spatialType, geometry);
				break;

			case JGeometry.GTYPE_POLYGON:
				newModel = new SpatialPolygonModel(name, spatialType, geometry);
				break;

			case JGeometry.GTYPE_POINT:
				newModel = new SpatialPointModel(name, spatialType, geometry);
				break;

			case JGeometry.GTYPE_CURVE:
				newModel = new SpatialLineStringModel(name, spatialType, geometry);
				break;

			default:
				throw new ModelException("createFromJGeometry: Not existing type of SpatialObjectModel");
		}

		return newModel;
	}

	/**
	 * Creates JGeometry from {@link ModelShape} so that we can make Model from it and render on canvas
	 *
	 * @param type
	 * @param pressedX
	 * @param pressedY
	 * @param oldPressedX
	 * @param oldPressedY
	 * @return model's geometry
	 * @throws ModelException
	 */
//	public static JGeometry createJGeometryFromModelType(ModelShape type, ArrayList<Integer> pressedCoordsX, ArrayList<Integer> pressedCoordsY) throws ModelException {
//		JGeometry geom;
//		switch (type) {
//			case POLYGON:
//				geom = new JGeometry(Utils.getMin(oldPressedX, pressedX), Utils.getMin(oldPressedY, pressedY), Utils.getMax(oldPressedX, pressedX), Utils.getMax(oldPressedY, pressedY), 0);
//				break;
//
//			case CIRCLE:
//				geom = JGeometry.createCircle(oldPressedX, oldPressedY, Math.sqrt(Math.pow(pressedX - oldPressedX, 2) + Math.pow(pressedY - oldPressedY, 2)), 0);
//				break;
//
//			case POINT:
//				geom = new JGeometry(pressedX, pressedY, 0);
//				break;
//
//			case LINE:
//				geom = JGeometry.createLinearLineString(new double[]{oldPressedX, oldPressedY, pressedX, pressedY}, 2, 0);
//				break;
//
//			default:
//				throw new ModelException("createJGeometryFromModelType(): Not existing model type");
//		}
//		return geom;
//	}

	/**
	 * Creates new shape based on class type
	 */
	abstract public Shape createShape();

	/**
	 * Regenerates shape based on internal properties which were set earlier
	 */
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
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return this model is within coordinates
	 */
	public boolean isWithin(int x, int y) {
		return getShape().contains(x, y);
	}

	/**
	 * Determines graphics while is selected or not
	 *
	 * @param selected whether this model is selected or not
	 */
	public void selectOnCanvas(boolean selected) {
		if (selected) {
			this.isSelected = true;
			borderColor = Color.decode("#4F6CB2");
			stroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		} else {
			this.isSelected = false;
			borderColor = getDefaultBorderColor();
			stroke = getDefaultStroke();
		}
	}

	/**
	 * Moves object some pixels defined by parameters
	 *
	 * @param deltaX
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
			delta = isScaledInMap(boundRect, delta);
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
	private double isScaledInMap(Rectangle2D boundRect, double delta) {
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

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}

	public Shape getShape() {
		return shape;
	}

	/**
	 * Returns reference to {@link SpatialObjectTypeModel}.
	 *
	 * @return spatial object type
	 */
	public SpatialObjectTypeModel getType() {
		return spatialObjectType;
	}

	/**
	 * Sets spatial object type
	 *
	 * @param spatialObjectType if null, sets unknown type
	 */
	public void setSpatialObjectType(SpatialObjectTypeModel spatialObjectType) {
		this.spatialObjectType = spatialObjectType == null ? SpatialObjectTypeModel.UnknownSpatialType : spatialObjectType;
	}

	public JGeometry getGeometry() {
		return geometry;
	}

	public void setGeometry(JGeometry geometry) {
		this.geometry = geometry;
		this.regenerateShape();
	}

	@Override
	public String toString() {
		return "#"+ id + " " + getType().getName() + ": " + name;
	}
}
