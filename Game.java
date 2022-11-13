import java.util.*;

public class Game 
    {        
        String map;
        int maxHealth;
        static Game instance;
        static List<Player> players;

        public static Game getInstance() {
            if (instance == null) {
                instance = new Game();
            }
            return instance;
        }

        private Game() 
        {            
            int randMap = (int)(Math.random()*500);
            
            players = new ArrayList<>();
			
			if(randMap% 4 == 0) {
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
        
        public List<Player> getPlayers() {
        	return players;
        }
        
        public Player getPlayerById(int x) {
        	for(int i =0; i<players.size();i++) {
        		
        		Player player = players.get(i);
        		
        		if(player.getId()==x) {
        			return player;
        		}
        	}
        	
        	return null;
        }
        
        
        public void addPlayer(Player p) {
        	players.add(p);
        }
        
        public void removePlayer(Player p) {
        	players.remove(p);
        }
        
        public int getMaxHealth() 
        {
            return maxHealth;
        }
        
        public String getMap() 
        {
            return map;
        }
    }
