import java.util.Arrays;

/*
*  Course: CSC335
*  Description: Helper class that represents coordinates for objects in the game.
*/
public class Coordinate {
	private int coordinates[] = new int[2];

	/*
	 * Constructor for the coordinate class. Author: Marin Maksutaj
	 */
	public Coordinate(int x, int y) {
		this.coordinates[0] = x;
		this.coordinates[1] = y;
	}

	/*
	 * Getter for the x coordinate. Author: Shyambhavi
	 */
	public int[] getCoord() {
		return coordinates;
	}

	/*
	 * Setter for the x coordinate. Author: Marin Maksutaj
	 */
	public void setCoord(int x, int y) {
		this.coordinates[0] = x;
		this.coordinates[1] = y;
	}

	@Override
	/*
	 * hashCode method for the coordinate class. Author: Shyambhavi
	 */
	public int hashCode() {

		int result = Arrays.hashCode(coordinates);
		return result;
	}

	@Override
	/*
	 * equals method for the coordinate class. Author: Marin Maksutaj
	 */
	public boolean equals(Object o) {

		if (!(o instanceof Coordinate)) {
			return false;
		}

		Coordinate x = (Coordinate) o;

		if (this.coordinates[0] == x.getCoord()[0] && this.coordinates[1] == x.getCoord()[1]) {
			return true;
		}

		return false;
	}
}
