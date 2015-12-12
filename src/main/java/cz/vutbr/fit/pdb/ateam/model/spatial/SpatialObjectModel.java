package cz.vutbr.fit.pdb.ateam.model.spatial;

import cz.vutbr.fit.pdb.ateam.exception.ModelException;
import cz.vutbr.fit.pdb.ateam.gui.map.ZooMapCanvas;
import cz.vutbr.fit.pdb.ateam.model.BaseModel;
import cz.vutbr.fit.pdb.ateam.model.Coordinate;
import cz.vutbr.fit.pdb.ateam.utils.Logger;
import cz.vutbr.fit.pdb.ateam.utils.Utils;
import oracle.spatial.geometry.JGeometry;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Abstract representation of object in spatial DB.
 *
 * @author Tomas Mlynaric
 */
abstract public class SpatialObjectModel extends BaseModel implements Comparable<SpatialObjectModel> {
	private static final Paint DEFAULT_BORDER_COLOR = Color.BLACK;
	private static final BasicStroke DEFAULT_STROKE = new BasicStroke(1);
	protected static final int INTERSECT_BOX_SIZE = 10;
	public static final int NO_SRID = 0;
	public static final String ADDITIONAL_CLOSEST_DISTANCE = "ClosestDistance";

	protected int zIndex;
	protected JGeometry geometry;
	protected Shape shape;
	protected SpatialObjectTypeModel spatialObjectType;
	protected Paint borderColor;
	protected BasicStroke stroke;
	private boolean isSelected = false;

	/**
	 * Helper enum for checking map boundaries
	 */
	private enum IsInMapAxis {
		AXIS_Y, AXIS_X
	}

	/**
	 * Enum for selection types (specifies color of selection)
	 */
	public enum SelectionType {
		DEFAULT(Color.RED),
		MULTI(Color.ORANGE);

		private Color color;

		SelectionType(String colorHex) {
			this.color = Color.decode(colorHex);
		}

		SelectionType(Color color) {
			this.color = color;
		}

		public Color getColor() {
			return color;
		}
	}

	/**
	 * Setups object and creates shape for graphic representation from jGeometry.
	 * It's protected so that it's not possible to instantiate the class
	 * otherwise than by {@link #loadFromDB(Long, int, String, SpatialObjectTypeModel, byte[])}
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

	/**
	 * Creates specific SpatialObject based on data from DB (raw JGeometry data)
	 *
	 * @param id          specific id
	 * @param zIndex      specifies which object should be above other
	 * @param name        name of spatial object
	 * @param spatialType reference to {@link SpatialObjectTypeModel}
	 * @param rawGeometry data from DB query result
	 * @return new model with ID! (if saved, updates in DB)
	 * @throws ModelException
	 */
	public static SpatialObjectModel loadFromDB(Long id, int zIndex, String name, SpatialObjectTypeModel spatialType, byte[] rawGeometry) throws ModelException {
		JGeometry geometry;
		try {
			geometry = JGeometry.load(rawGeometry);
		} catch (Exception e) {
			throw new ModelException("loadFromDB: JGeometry data problem");
		}
		SpatialObjectModel model = createFromJGeometry(name, spatialType, geometry);
		model.zIndex = zIndex;
		model.id = id; 		// id is set here so that we can create model without Id (new model)
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
			case JGeometry.GTYPE_MULTIPOINT:
				newModel = new SpatialMultiPointModel(name, spatialType, geometry);
				break;

			case JGeometry.GTYPE_MULTIPOLYGON:
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
				throw new ModelException("createFromJGeometry: Not existing type of SpatialObjectModel, specified: " + geometry.getType());
		}

		return newModel;
	}

	/**
	 * Creates SpatialObject from {@link SpatialModelShape} based on rules in ModelShape
	 *
	 * @param shapeType
	 * @param pressedCoordinates
	 * @return
	 * @throws ModelException
	 */
	public static SpatialObjectModel create(SpatialModelShape shapeType, ArrayList<Coordinate> pressedCoordinates) throws ModelException {
		JGeometry geom;
		switch (shapeType) {
			case POINT:
				// creating collection of points
				if (pressedCoordinates.size() > shapeType.getPointsToRenderCount()) {
					Object[] arrayOfArrayDoubles = Coordinate.fromListToArrayOfArray(pressedCoordinates);
					geom = JGeometry.createMultiPoint(arrayOfArrayDoubles, 2, NO_SRID);
				} else {
					Coordinate firstPoint = pressedCoordinates.get(0);
					geom = new JGeometry(firstPoint.x, firstPoint.y, NO_SRID);
				}
				break;

			case RECTANGLE:
				geom = createRectangleGeometry(pressedCoordinates.get(0), pressedCoordinates.get(1));
				break;

			case CIRCLE:
				geom = createCircleGeometry(pressedCoordinates.get(0), pressedCoordinates.get(1));
				break;

			case POLYGON:
				geom = JGeometry.createLinearPolygon(Coordinate.fromListToArray(pressedCoordinates), 2, 0);
				break;

			case RECTANGLE_WITH_HOLE:
				JGeometry rectangle = createRectangleGeometry(pressedCoordinates.get(0), pressedCoordinates.get(1));
				double[] rectangleOrdinates = rectangle.getOrdinatesArray();

				double rectangleWidth = rectangleOrdinates[2] - rectangleOrdinates[0];
				double rectangleHeight = rectangleOrdinates[3] - rectangleOrdinates[1];

				int deltaX, deltaY;
				if (rectangleWidth < rectangleHeight) {
					deltaX = (int) ((rectangleWidth / 2) * 0.6);
					deltaY = 0;
				} else {
					deltaX = 0;
					deltaY = (int) ((rectangleHeight / 2) * 0.6);
				}

				int centerX = (int) ((rectangleOrdinates[2] + rectangleOrdinates[0]) / 2);
				int centerY = (int) ((rectangleOrdinates[3] + rectangleOrdinates[1]) / 2);
				JGeometry circle = createCircleGeometry(new Coordinate(centerX, centerY), new Coordinate(centerX + deltaX, centerY + deltaY));

				geom = new JGeometry(
						JGeometry.GTYPE_MULTIPOLYGON,
						NO_SRID,
						new int[]{
								1, 1003, 3, // rectangle
								rectangleOrdinates.length + 1, 2003, 4 // circle
						},
						ArrayUtils.addAll(rectangleOrdinates, circle.getOrdinatesArray())
				);
				break;

			case LINE:
				double[] points = Coordinate.fromListToArray(pressedCoordinates);
				geom = JGeometry.createLinearLineString(points, 2, NO_SRID);

				break;

			default:
				throw new ModelException("create(): Not existing model type");
		}

		return SpatialObjectModel.createFromJGeometry(BaseModel.NEW_MODEL_NAME, SpatialObjectTypeModel.UnknownSpatialType, geom);
	}

	/**
	 * Helper method for creating rectangle JGeometry from 2 points
	 *
	 * @param minCoordinate coordinates for first point
	 * @param maxCoordinate coordinates for last point
	 * @return rectangle's JGeometry
	 */
	private static JGeometry createRectangleGeometry(Coordinate minCoordinate, Coordinate maxCoordinate) {
		return new JGeometry(
				Utils.getMin(minCoordinate.x, maxCoordinate.x),
				Utils.getMin(minCoordinate.y, maxCoordinate.y),
				Utils.getMax(minCoordinate.x, maxCoordinate.x),
				Utils.getMax(minCoordinate.y, maxCoordinate.y),
				NO_SRID);
	}

	/**
	 * Helper method for creating circle JGeometry from 2 points
	 *
	 * @param centerPoint coordinates of center point of circle
	 * @param borderPoint coordinates of on circles border
	 * @return circle's JGeometry
	 */
	private static JGeometry createCircleGeometry(Coordinate centerPoint, Coordinate borderPoint) {
		return JGeometry.createCircle(
				centerPoint.x,
				centerPoint.y,
				Math.sqrt(Math.pow(borderPoint.x - centerPoint.x, 2) + Math.pow(borderPoint.y - centerPoint.y, 2)),
				NO_SRID
		);
	}

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
	 * @param isSelected whether this model is selected or not
	 */
	public void selectOnCanvas(boolean isSelected) {
		selectOnCanvas(isSelected, SelectionType.DEFAULT);
	}

	/**
	 * Selects this model on canvas with specified type
	 *
	 * @param isSelected whether is selected or not
	 * @param type       selection type (color)
	 */
	public void selectOnCanvas(boolean isSelected, SelectionType type) {
		if (isSelected) {
			this.isSelected = true;
			borderColor = type.getColor();
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
	 * @param deltaX amount how much was moved on axis X
	 * @param deltaY amount how much was moved on axis Y
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
	 * @param boundRect boundary rectangle
	 * @param delta     delta X or Y
	 * @param axisType  checking in which axis direction
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
	 * @param boundRect boundary rectangle which is countet for general shapes
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

	/**
	 * Gets stroke type
	 *
	 * @return DEFAULT_STROKE or can be overwritten in children class
	 */
	protected BasicStroke getDefaultStroke() {
		return DEFAULT_STROKE;
	}


	/**
	 * Gets border type
	 *
	 * @return DEFAULT_BORDER_COLOR or can be overwritten in children class
	 */
	protected Paint getDefaultBorderColor() {
		return DEFAULT_BORDER_COLOR;
	}

	@Override
	public String getTableName() {
		return "Spatial_Objects";
	}

	public boolean isSelected() {
		return isSelected;
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

	@Override
	public String toString() {
		return "#" + id + " " + getType().getName() + ": " + name;
	}

	public int getzIndex() {
		return zIndex;
	}

	public void setzIndex(int zIndex) {
		this.zIndex = zIndex;
	}

	@Override
	public int compareTo(SpatialObjectModel o) {
		if (zIndex > o.getzIndex())
			return 1;
		else if (zIndex < o.getzIndex())
			return -1;
		else if (zIndex == o.getzIndex() && getId() > o.getId())
			return 1;
		else if (zIndex == o.getzIndex() && getId() < o.getId())
			return -1;

		return 0;
	}
}
