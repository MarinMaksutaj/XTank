public class Bullet{
		 
		 private int x;
		 private int y;
		 private int id;
		 private int bulletModel;
		 private int direction;
		 
		
		 public Bullet(int x, int y, int id, int direction, int bulletModel) {
			 this.x = x;
			 this.bulletModel = bulletModel;
			 this.y =y;
			 this.id = id; 
			 this.direction = direction;
		 }
		 
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

		 public void setDirection(int direction) {
			 this.direction = direction;
			 if (direction == 0) {
				 this.y = this.y - 10;
			 }
			 else if (direction == 1) {
				this.y = this.y + 150;
			 }
			 else if (direction == 2) {
				 this.y = this.y + 75;
				 this.x = this.x - 50;
			 }
			 else if (direction == 3) {
				this.y = this.y + 75;
				this.x = this.x + 50;
			 } 
		 }
		 
		 public void incrementY() {
			 // based on direction change x and y	
			 if (direction == 0) {
				 y -= bulletModel == 1 ? 10 : 5;
			 }
			 else if (direction == 1) {
				 y += bulletModel == 1 ? 10 : 5;
			 }
			 else if (direction == 2) {
				 x -= bulletModel == 1 ? 10 : 5;
			 }
			 else if (direction == 3) {
				 x += bulletModel == 1 ? 10 : 5;
			 }

		 }
		 
		 
	}
