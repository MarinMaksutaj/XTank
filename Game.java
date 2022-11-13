/*
*  Course: CSC335
*  Description: Helper class that keeps track of the game state.
*  It uses the singleton design pattern.
*/
public class Game 
    {        
        String map;
        int maxHealth;
        static Game instance;

        /*
         * Getter for the instance of the game.
         * Author: Marin Maksutaj
         */
        public static Game getInstance() {
            if (instance == null) {
                instance = new Game();
            }
            return instance;
        }

        /*
         * Constructor for the game class.
         * Author: Shyambhavi
         */
        private Game() 
        {            
            int randMap = (int)(Math.random()*500);
			
			if (randMap% 4 == 0) {
				map = "MAP3";
			} else if(randMap% 3 == 0) {
				map = "MAP2";
			}
			else if(randMap% 2 == 0) {
				map = "MAP1";
			}else {
				map = "MAP1";
			}
			
			int randHealth = (int)(Math.random()*500);
			
			if(randHealth% 2 == 0) {
				maxHealth = 5;
			} else {
				maxHealth = 3;
			}
        }
        
        /*
         * Getter for the max health of the tank.
         * Author: Marin Maksutaj
         */
        public int getMaxHealth() 
        {
            return maxHealth;
        }
        
        /*
         * Getter for the map.
         * Author: Shyambhavi
         */
        public String getMap() 
        {
            return map;
        }
    }
