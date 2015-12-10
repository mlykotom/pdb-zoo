package cz.vutbr.fit.pdb.ateam.model.spatial;

/**
 * Enum for available spatial object shapes
 * @author Tomas Mlynaric
 */
public enum SpatialModelShape {
	POINT("point(s)", 1, -1),
	LINE("line", 2, -1),
	RECTANGLE("rectangle", 2),
	CIRCLE("circle", 2),
	POLYGON("polygon", 2, -1),
	RECTANGLE_WITH_HOLE("rectangle with hole", 2);

	private int pointsToRenderCount;
	private int totalPointsCount;
	private String name;

	SpatialModelShape(String name, int pointsToRenderCount, int totalPointsCount) {
		this.pointsToRenderCount = pointsToRenderCount;
		this.totalPointsCount = totalPointsCount;
		this.name = name;
	}

	SpatialModelShape(String name, int totalPointsCount) {
		this.totalPointsCount = this.pointsToRenderCount = totalPointsCount;
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
