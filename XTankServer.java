
/*
*  Course: CSC335
*  Description: This is the server class for the game. It creates the game
*  and starts the game loop.
*/
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
 * Class that represents the server for the game.
 */
public class XTankServer 
{
	static ArrayList<DataOutputStream> sq;
	static boolean accept = true; 

    /**
     * Main method for the server.
     * Author: Marin Maksutaj
     */
    public static void main(String[] args) throws Exception 
    {
		System.out.println(InetAddress.getLocalHost());
		sq = new ArrayList<>();
		
        try (var listener = new ServerSocket(59895)) 
        {
            System.out.println("The XTank server is running...");
            var pool = Executors.newFixedThreadPool(20);
            Game game = Game.getInstance();
            
            while (accept) 
            {
            	
            	Socket socket = listener.accept();
                pool.execute(new XTankManager(socket, game));
                
            }
        }
    }

    /**
     * Class that manages the client connections.
     */
    private static class XTankManager implements Runnable 
    {
        private Socket socket;
        private Game game;

        /**
         * Constructor for the XTankManager class.
         * Author: Shyambhavi
         */
        XTankManager(Socket socket, Game game) 
        {
            this.socket = socket;
            this.game = game;
        }

        @Override
        /*
         * Method that runs the server side of the game.
         * Author: Marin Maksutaj
         */
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
                int currid = (int)(Math.random()*1000);
                int randX = (int)(Math.random()*500);
                int randY = (int)(Math.random()*500);
                
                outWriter.println("YOURID: " + currid + " X: " + randX + " Y: " + randY + 
                " D: " + 0 + " M: " + 1 +" " + game.getMaxHealth() + " " + game.getMap());
              
                              
                while (true)
                {
					if (in.available() > 0) {
					     String line = scanner.nextLine();
	                        System.out.println(line);
	                         
	                    if(line.equals("WINNER")) {
	                    	accept = false;
	                    }else {
	                    	for (DataOutputStream o: sq)
		                	{
		                		PrintWriter outWriter2 = new PrintWriter(o, true);
		    					outWriter2.println(line);
		        		
		                    }
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
}