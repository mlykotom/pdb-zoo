package cz.vutbr.fit.pdb.ateam.model;

import java.util.ArrayList;

/**
 * @author Tomas Mlynaric
 */
public class Coordinate {
	public int x;
	public int y;

	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Makes one dimensional array of coordinates from list
	 *
	 * @param coordinates
	 * @return
	 */
	public static double[] fromListToArray(ArrayList<Coordinate> coordinates) {
		double[] points = new double[coordinates.size() * 2];
		int i = 0;
		for (Coordinate coordinate : coordinates) {
			points[i++] = coordinate.x;
			points[i++] = coordinate.y;
		}
		return points;
	}

	public static Object[] fromListToArrayOfArray(ArrayList<Coordinate> coordinates) {
		Object[] objects = new Object[coordinates.size()];
		int i = 0;
		for (Coordinate coordinate : coordinates) {
			objects[i++] = coordinate.toArray();
		}

		return objects;
	}

	public double[] toArray() {
		return new double[]{x, y};
	}
}
