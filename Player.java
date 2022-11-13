
public class Player {
	
	int id;
	int x;
	int y;
	int d;
	double health;
	
	public Player(int id, int x, int y, int d, int health) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.health = health;
		this.d =d;
	}
	
	public int getId() {
		return id;
	}
	
	public int getD() {
		return d;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public double getHealth() {
		return health;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	public void setD(int d) {
		this.d = d;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setHealth(double x) {
		health = x;
	}

}
