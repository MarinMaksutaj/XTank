import java.util.Arrays;

public class Coordinate{
		private int coordinates [] = new int [2];
		
		public Coordinate(int x, int y) {
			 this.coordinates[0] = x;
			 this.coordinates[1] = y;
		 }
		
		 
		 public int[] getCoord() {
			 return coordinates; 
		 }
		 
		 public void setCoord(int x, int y) {
			 this.coordinates[0] = x;
			 this.coordinates[1] = y;
		 }
		 
		 @Override
		 public int hashCode() {
			 
			    int result = Arrays.hashCode(coordinates);
			    return result;
		 }
		 
		 @Override 
		 public boolean equals(Object o) {
			 
			 
			 if(!(o instanceof Coordinate)) {
				 return false;
			 }
			 
			 Coordinate x = (Coordinate) o;
		
			 if(this.coordinates[0] == x.getCoord()[0] && 
					 this.coordinates[1] == x.getCoord()[1]) {
				 return true;
			 }
			
			 return false;
		 }
		 
		
	}
