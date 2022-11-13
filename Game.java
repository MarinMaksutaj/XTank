import java.util.*;

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
        static List<Player> players;


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
            
            players = new ArrayList<>();
			
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
         * getting list of players.
         * Author: Marin Maksutaj
         */
        public List<Player> getPlayers() {
        	return players;
        }
        
        /*
         * getting player by id
         * Author: Shyambhavi
         */
        public Player getPlayerById(int x) {
        	for(int i =0; i<players.size();i++) {
        		
        		Player player = players.get(i);
        		
        		if(player.getId()==x) {
        			return player;
        		}
        	}
        	
        	return null;
        }
        
        /*
         * adding new player to list
         * Author: Marin Maksutaj
         */
        public void addPlayer(Player p) {
        	players.add(p);
        }
        
        /*
         * removing player from list
         * Author: Shyambhavi
         */
        public void removePlayer(Player p) {
        	players.remove(p);
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
