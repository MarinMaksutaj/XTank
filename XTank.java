
/*
*  Course: CSC335
*  Description: This is the main class for the game. It creates the game
*  and starts the game loop.
*/
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class XTank {
	/*
	 * This is the main method for the game. It creates the game and starts the game
	 * loop. Author: Shyambhavi
	 */
	public static void main(String[] args) throws Exception {
		try (var socket = new Socket("127.0.0.1", 59895)) {
			// set a random tankID
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			var ui = new XTankUI(in, out, "none");
			ui.start();
		}
	}
}
