/*
*  Course: CSC335
*  Description: Helper class that represents a single bullet.
*/
public class Bullet {

	private int x;
	private int y;
	private int id;
	private int bulletModel;
	private int direction;

	/*
	 * Constructor for the bullet class. Author: Marin Maksutaj
	 */
	public Bullet(int x, int y, int id, int direction, int bulletModel) {
		this.x = x;
		this.bulletModel = bulletModel;
		this.y = y;
		this.id = id;
		this.direction = direction;
	}

	/*
	 * Getter methods for the bullet class. Author: Shyanmbhavi
	 */
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getId() {
		return id;
	}

	public int getDirection() {
		return direction;
	}

	public int getBulletModel() {
		return bulletModel;
	}

	public double getDamage() {
		return bulletModel == 1 ? 1 : 1.5;
	}

	/*
	 * Setter for the direction of the bullet. Author: Marin Maksutaj
	 */
	public void setDirection(int direction) {
		this.direction = direction;
		if (direction == 0) {
			this.y = this.y - 10;
		} else if (direction == 1) {
			this.y = this.y + 150;
		} else if (direction == 2) {
			this.y = this.y + 75;
			this.x = this.x - 50;
		} else if (direction == 3) {
			this.y = this.y + 75;
			this.x = this.x + 50;
		}
	}

	/*
	 * Increments the x and y coordinates of the bullet. Author: Shyambhavi
	 */
	public void incrementY() {
		if (direction == 0) {
			y -= bulletModel == 1 ? 10 : 5;
		} else if (direction == 1) {
			y += bulletModel == 1 ? 10 : 5;
		} else if (direction == 2) {
			x -= bulletModel == 1 ? 10 : 5;
		} else if (direction == 3) {
			x += bulletModel == 1 ? 10 : 5;
		}
	}
}
