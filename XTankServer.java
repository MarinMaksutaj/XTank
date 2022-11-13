import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.net.InetAddress;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * When a client connects, a new thread is started to handle it.
 */
public class XTankServer 
{
	static ArrayList<DataOutputStream> sq;
	
    public static void main(String[] args) throws Exception 
    {
		System.out.println(InetAddress.getLocalHost());
		sq = new ArrayList<>();
		
        try (var listener = new ServerSocket(59895)) 
        {
            System.out.println("The XTank server is running...");
            var pool = Executors.newFixedThreadPool(20);
            Game game = new Game();
            while (true) 
            {
            	
            	Socket socket = listener.accept();
                Player player = new Player("Test", 0, (int)(Math.random()*1000));
                game.addPlayer(player);
                
                pool.execute(new XTankManager(socket, player, game));

            }
        }
    }

    private static class XTankManager implements Runnable 
    {
        private Socket socket;
        private Player currentPlayer;
        private Game game;

        XTankManager(Socket socket, Player player, Game game) 
        {
            this.socket = socket;
            this.currentPlayer = player;
            this.game = game;
        }

        @Override
        public void run() 
        {
            System.out.println("Connected: " + socket);
            try 
            {
            	DataInputStream in = new DataInputStream(socket.getInputStream());
            	DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                Scanner scanner = new Scanner(in);
                PrintWriter outWriter = new PrintWriter(socket.getOutputStream(), true);
                sq.add(out);
                int currid = currentPlayer.getID();
                int randX = (int)(Math.random()*500);
                int randY = (int)(Math.random()*500);
                currentPlayer.setX(randX);
                currentPlayer.setY(randY);
                outWriter.println("YOURID: " + currid + " X: " + randX + " Y: " + randY + " D: " + 0 + " M: " + 1);
                while (true)
                {
					if (in.available() > 0) {
                        String line = scanner.nextLine();
                        System.out.println(line);
                         
                	for (DataOutputStream o: sq)
                	{
                		PrintWriter outWriter2 = new PrintWriter(o, true);
    					outWriter2.println(line);
        		
                    }
                }
            } }
            catch (Exception e) 
            {
                System.out.println("Error:" + socket);
            } 
            finally 
            {
                try { socket.close(); } 
                catch (IOException e) {}
                System.out.println("Closed: " + socket);
            }
        }
    }

    // implement the Player class here

    private static class Player 
    {
        private String name;
        private int score;
        private int id;
        private int x;
        private int y;

        public Player(String name, int score, int id) 
        {
            this.name = name;
            this.score = score;
            this.id = id;
        }

        public String getName() { return name; }
        public int getScore() { return score; }
        public int getID() { return id; }
        public int getX() { return x; }
        public int getY() { return y; }
        public void setX(int x) { this.x = x; }
        public void setY(int y) { this.y = y; }
    }

    private static class Game 
    {
        private List<Player> players;

        public Game() 
        {
            players = new ArrayList<>();
        }

        public void addPlayer(Player player) 
        {
            players.add(player);
        }

        public void removePlayer(Player player) 
        {
            players.remove(player);
        }

        public List<Player> getPlayers() 
        {
            return players;
        }
    }



    
}