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
}
