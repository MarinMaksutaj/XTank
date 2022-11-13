public class Game 
    {        
        String map;
        int maxHealth;
        static Game instance;


        public static Game getInstance() {
            if (instance == null) {
                instance = new Game();
            }
            return instance;
        }

        private Game() 
        {            
            int randMap = (int)(Math.random()*500);
			
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
        
        public int getMaxHealth() 
        {
            return maxHealth;
        }
        
        public String getMap() 
        {
            return map;
        }
    }
