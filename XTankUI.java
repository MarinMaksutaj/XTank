
/*
*  Course: CSC335
*  Description: UI class for the XTank game. It is responsible for fetching the game
 				state from the server and displaying it to the user.
*/
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Scanner;

/*
* The XTankUI class is responsible for fetching the messages from 
the server and displaying the game state to the user.
*/
public class XTankUI {
	private int x = 300;
	private int y = 500;
	private double health;
	private int id;
	private String map;
	private Map<Integer, Integer[]> enemyTanks;
	private int directionX = 0;
	private int directionY = -10;
	private int tankModel;
	private Canvas canvas;
	private Display display;
	private List<Bullet> bulletsList;
	private List<Bullet> enemyBulletsList;
	private Set<Coordinate> filledCoordsMyTank;
	private Set<Coordinate> filledCoordsEnemyTank;
	private Set<Coordinate> filledCoordsBullet;
	private Set<Coordinate> filledCoordsObstacles;
	Text healthText;
	private int tankDirection = 0;

	DataInputStream in;
	PrintWriter out;

	/*
	 * Constructor for the XTankUI class.
	 */
	public XTankUI(DataInputStream in, DataOutputStream out, String map) {
		this.in = in;
		this.out = new PrintWriter(out, true);
		this.id = -1;
		this.enemyTanks = new HashMap<>();
		this.bulletsList = new ArrayList<>();
		this.enemyBulletsList = new ArrayList<>();
		this.filledCoordsMyTank = new HashSet<>();
		this.filledCoordsEnemyTank = new HashSet<>();
		this.filledCoordsBullet = new HashSet<>();
		this.filledCoordsObstacles = new HashSet<>();
		this.tankDirection = 0;
		this.tankModel = 1;
		this.map = map;

	}

	/*
	 * Helper function that computes the direction of each bullet based on the
	 * tank's direction. Author: Marin Maksutaj
	 */
	private int getBulletDirection(Bullet bullet) {
		// look at the bullet's id and return the direction of the tank that fired it
		int id = bullet.getId();
		// check the current tank first
		if (id == this.id) {
			return tankDirection;
		}
		// check the other tanks
		for (Integer key : enemyTanks.keySet()) {
			if (key == id) {
				return enemyTanks.get(key)[2];
			}
		}
		return -1;
	}

	/*
	 * Helper function that fills the coordinates of the tanks and obstacles.
	 * Author: Shyambhavi
	 */
	private void fillCoords(int x, int y, String type) {

		if (type.equals("Tank")) {
			for (int i = x; i <= x + 50; i++) {
				for (int j = y; j <= y + 100; j++) {
					Coordinate toAdd = new Coordinate(i, j);
					filledCoordsEnemyTank.add(toAdd);
				}
			}
		} else if (type.equals("My Tank")) {
			for (int i = x; i <= x + 50; i++) {
				for (int j = y; j <= y + 100; j++) {
					Coordinate toAdd = new Coordinate(i, j);
					filledCoordsMyTank.add(toAdd);
				}
			}

		} else if (type.equals("Obstacle1")) {
			for (int i = x; i <= x + 50; i++) {
				for (int j = y; j <= y + 200; j++) {
					Coordinate toAdd = new Coordinate(i, j);
					filledCoordsObstacles.add(toAdd);
				}
			}
		} else if (type.equals("Obstacle2")) {
			for (int i = x; i <= x + 300; i++) {
				for (int j = y; j <= y + 50; j++) {
					Coordinate toAdd = new Coordinate(i, j);
					filledCoordsObstacles.add(toAdd);
				}
			}
		} else if (type.equals("Obstacle3")) {
			for (int i = x; i <= x + 400; i++) {
				for (int j = y; j <= y + 50; j++) {
					Coordinate toAdd = new Coordinate(i, j);
					filledCoordsObstacles.add(toAdd);
				}
			}
		} else if (type.equals("Obstacle4")) {
			for (int i = x; i <= x + 50; i++) {
				for (int j = y; j <= y + 350; j++) {
					Coordinate toAdd = new Coordinate(i, j);
					filledCoordsObstacles.add(toAdd);
				}
			}
		}

		else {
			for (int i = x; i <= x + 10; i++) {
				for (int j = y; j <= y + 10; j++) {
					Coordinate toAdd = new Coordinate(i, j);
					filledCoordsBullet.add(toAdd);
				}
			}
		}

	}

	/*
	 * Helper function that checks if there is a bullet collision. Author:
	 * Shyambhavi
	 */
	public String isBulletCollision() {

		boolean enemyCollision = false;
		boolean myCollision = false;

		for (Coordinate bulletCoord : filledCoordsBullet) {
			if (filledCoordsEnemyTank.contains(bulletCoord)) {
				{
					enemyCollision = true;
				}
			}
			if (filledCoordsMyTank.contains(bulletCoord)) {
				{
					myCollision = true;
				}
			}

		}

		if (enemyCollision && myCollision) {
			return "both";
		} else if (enemyCollision) {
			return "enemy";
		} else if (myCollision) {
			return "mine";
		} else {
			return "none";
		}

	}

	/*
	 * Helper function that checks if there is an obstacle collision. Author: Marin
	 * Maksutaj
	 */
	public String isObstacleCollision() {

		boolean tankCollision = false;
		boolean bulletCollision = false;

		for (Coordinate tankCoord : filledCoordsMyTank) {
			if (filledCoordsObstacles.contains(tankCoord)) {
				System.out.println(tankCoord.getCoord()[0] + " " + tankCoord.getCoord()[1]);
				tankCollision = true;
				break;
			}
		}

		for (Coordinate bulletCoord : filledCoordsBullet) {
			if (filledCoordsObstacles.contains(bulletCoord)) {
				bulletCollision = true;
				break;
			}
		}

		if (tankCollision && bulletCollision) {
			return "both";
		} else if (tankCollision) {
			return "tank";
		} else if (bulletCollision) {
			return "bullet";
		}
		return "none";

	}

	/*
	 * Start method used to start the game. Author: Shyambhavi
	 */
	public void start() {
		System.out.println("Testingg");
		display = new Display();
		Shell shell = new Shell(display);
		shell.setText("xtank");

		GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);

		shell.setSize(800, 850);

		healthText = new Text(shell, SWT.READ_ONLY | SWT.BORDER);
		healthText.setText("Health: " + health + "     ");
		healthText.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
		
		Text rulesText = new Text(shell, SWT.READ_ONLY | SWT.BORDER);
		rulesText.setText("\n Use WASD keys to rotate. Use spacebar to fire bullets."
				+ " Every time the server is started, a random map and set of rules\n"
				+ " is generated. The maximum health is going to be either 3 or 5"
				+ " based on the set of rules. Tank Model can be switched by using \n"
				+ " the drop down in the menu.\n");
		rulesText.setForeground(display.getSystemColor(SWT.COLOR_WHITE));

		Composite upperComp = new Composite(shell, SWT.NO_FOCUS);
		Composite lowerComp = new Composite(shell, SWT.NO_FOCUS);

		canvas = new Canvas(upperComp, SWT.NONE);
		canvas.setSize(800, 650);

		Button quitButton = new Button(lowerComp, SWT.PUSH);
		quitButton.setText("Quit");
		quitButton.setSize(60, 50);
		quitButton.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
		quitButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				out.println("REMOVE: " + id + " X: -100 Y: -100 D: -1 M: -1");
				System.exit(0);
			}
		});

		canvas.addPaintListener(event -> {

			event.gc.fillRectangle(canvas.getBounds());
			this.filledCoordsMyTank.clear();
			// check which map has been set by the server and draw the map
			if (map.equals("MAP1")) {
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				event.gc.fillRectangle(100, 100, 50, 200);
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				event.gc.fillRectangle(300, 500, 300, 50);
			}

			if (map.equals("MAP2")) {
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				event.gc.fillRectangle(200, 600, 400, 50);
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				event.gc.fillRectangle(400, 100, 50, 350);
			}

			if (map.equals("MAP3")) {
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				event.gc.fillRectangle(650, 100, 50, 200);
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				event.gc.fillRectangle(200, 500, 300, 50);
			}

			if (health > 0) {
				event.gc.setBackground(
						shell.getDisplay().getSystemColor(tankModel == 1 ? SWT.COLOR_DARK_GREEN : SWT.COLOR_BLUE));
				event.gc.fillRectangle(x, y, 50, 100);
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
				event.gc.fillOval(x, y + 25, 50, 50);
				event.gc.setLineWidth(4);
				// draw the line based on the direction
				if (tankDirection == 0) {
					event.gc.drawLine(x + 25, y + 25, x + 25, y - 25);
				} else if (tankDirection == 1) {
					event.gc.drawLine(x + 25, y + 75, x + 25, y + 125);
				} else if (tankDirection == 2) {
					event.gc.drawLine(x, y + 50, x - 25, y + 50);
				} else if (tankDirection == 3) {
					event.gc.drawLine(x + 50, y + 50, x + 75, y + 50);
				}

				fillCoords(x, y, "My Tank");

			}
			this.filledCoordsEnemyTank.clear();

			for (Integer[] enemyTank : enemyTanks.values()) {
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
				event.gc.fillRectangle(enemyTank[0], enemyTank[1], 50, 100);
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
				event.gc.fillOval(enemyTank[0], enemyTank[1] + 25, 50, 50);
				event.gc.setLineWidth(4);
				// draw the line based on the direction
				if (enemyTank[2] == 0) {
					event.gc.drawLine(enemyTank[0] + 25, enemyTank[1] + 25, enemyTank[0] + 25, enemyTank[1] - 25);
				} else if (enemyTank[2] == 1) {
					event.gc.drawLine(enemyTank[0] + 25, enemyTank[1] + 75, enemyTank[0] + 25, enemyTank[1] + 125);
				} else if (enemyTank[2] == 2) {
					event.gc.drawLine(enemyTank[0], enemyTank[1] + 50, enemyTank[0] - 25, enemyTank[1] + 50);
				} else if (enemyTank[2] == 3) {
					event.gc.drawLine(enemyTank[0] + 50, enemyTank[1] + 50, enemyTank[0] + 75, enemyTank[1] + 50);
				}

				fillCoords(enemyTank[0], enemyTank[1], "Tank");
			}

			if (health > 0) {

				for (int i = 0; i < bulletsList.size(); i++) {

					Bullet bullet = bulletsList.get(i);
					this.filledCoordsBullet.clear();
					fillCoords(bullet.getX(), bullet.getY(), "Bullet");

					// if the bullet is out of bounds, remove it
					if (bullet.getX() < 0 || bullet.getX() > 800 || bullet.getY() < 0 || bullet.getY() > 650) {
						bulletsList.remove(i);
					}

					else if ((!(isBulletCollision().equals("none"))) || isObstacleCollision().equals("bullet")
							|| isObstacleCollision().equals("both")) {
						bulletsList.remove(i);
					}

					else {
						event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
						event.gc.fillRectangle(bullet.getX(), bullet.getY(), 10, 10);
					}
				}
			}

			for (int i = 0; i < enemyBulletsList.size(); i++) {

				Bullet bullet = enemyBulletsList.get(i);
				this.filledCoordsBullet.clear();
				fillCoords(bullet.getX(), bullet.getY(), "Bullet");
				// if the bullet is out of bounds, remove it
				if (bullet.getX() < 0 || bullet.getX() > 800 || bullet.getY() < 0 || bullet.getY() > 650) {
					enemyBulletsList.remove(i);
				}

				else if ((isBulletCollision().equals("mine") || isBulletCollision().equals("both"))) {
					health -= bullet.getDamage();
					healthText.setText("Health: " + health + "     ");
					enemyBulletsList.remove(i);

					if (health <= 0) {
						healthText.setText("GAME OVER");
						out.println("REMOVE: " + this.id + " X: -100 Y: -100 D: -1 M: -1");

					}
				}

				else if (!(isBulletCollision().equals("none")) || isObstacleCollision().equals("bullet")
						|| isObstacleCollision().equals("both")) {
					enemyBulletsList.remove(i);
				}

				else {
					event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));
					event.gc.fillRectangle(bullet.getX(), bullet.getY(), 10, 10);
					if (health <= 0) {
						healthText.setText("GAME OVER");
						out.println("REMOVE: " + this.id + " X: -100 Y: -100 D: -1 M: -1");

					}
				}

			}
		});

		canvas.addMouseListener(new MouseListener() {
			public void mouseDown(MouseEvent e) {
				System.out.println("mouseDown in canvas");
			}

			public void mouseUp(MouseEvent e) {
			}

			public void mouseDoubleClick(MouseEvent e) {
			}
		});

		canvas.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				System.out.println("key " + e.keyCode + " pressed");

				if (health > 0) {

					if (e.keyCode == 32) {

						Bullet bullet = new Bullet(x + 20, y - 30, id, 0, tankModel);
						int bulletDir = getBulletDirection(bullet);
						bullet.setDirection(bulletDir);
						bulletsList.add(bullet);

						try {
							out.println("BULLET: " + bullet.getId() + " X: " + bullet.getX() + " Y: " + bullet.getY()
									+ " D: " + bulletDir + " M: " + tankModel);
						} catch (Exception ex) {
							System.out.println("The server did not respond (write KL).");
						}

						Timer timer = new Timer();
						timer.scheduleAtFixedRate(new TimerTask() {
							@Override
							public void run() {
								Display.getDefault().asyncExec(new Runnable() {
									public void run() {

										bullet.incrementY();
										canvas.redraw();

										if (bullet.getY() <= -10) {
											bulletsList.remove(bullet);
											timer.cancel();
											canvas.redraw();
										}
									}
								});
							}
						}, 0, 50);
					}

					else if (e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.ARROW_DOWN || e.keyCode == SWT.ARROW_LEFT
							|| e.keyCode == SWT.ARROW_RIGHT || e.keyCode == 119 || e.keyCode == 115 || e.keyCode == 97
							|| e.keyCode == 100) {

						if (e.keyCode == SWT.ARROW_UP) {

							directionX = 0;
							directionY = tankModel == 1 ? -15 : -5;
							x += directionX;
							y += directionY;

							filledCoordsMyTank.clear();
							fillCoords(x, y, "My Tank");

							if (isObstacleCollision().equals("tank") || isObstacleCollision().equals("both")) {
								x -= 2 * directionX;
								y -= 2 * directionY;

							}

						} else if (e.keyCode == SWT.ARROW_DOWN) {

							directionX = 0;
							directionY = tankModel == 1 ? 15 : 5;
							x += directionX;
							y += directionY;

							filledCoordsMyTank.clear();
							fillCoords(x, y, "My Tank");

							if (isObstacleCollision().equals("tank") || isObstacleCollision().equals("both")) {
								x -= 2 * directionX;
								y -= 2 * directionY;
							}
						} else if (e.keyCode == SWT.ARROW_LEFT) {

							directionX = tankModel == 1 ? -15 : -5;
							directionY = 0;
							x += directionX;
							y += directionY;

							filledCoordsMyTank.clear();
							fillCoords(x, y, "My Tank");

							if (isObstacleCollision().equals("tank") || isObstacleCollision().equals("both")) {
								x = x - (2 * directionX);
							}

						} else if (e.keyCode == SWT.ARROW_RIGHT) {

							directionX = tankModel == 1 ? 15 : 5;
							directionY = 0;
							x += directionX;
							y += directionY;

							filledCoordsMyTank.clear();
							fillCoords(x, y, "My Tank");

							if (isObstacleCollision().equals("tank") || isObstacleCollision().equals("both")) {
								x -= 2 * directionX;
								y -= 2 * directionY;
							}

						}
						// check if keys are wasd, and if so, change direction
						else if (e.keyCode == 119) {
							System.out.println("W was pressed");
							tankDirection = 0;
						} else if (e.keyCode == 115) {
							tankDirection = 1;

						} else if (e.keyCode == 97) {
							tankDirection = 2;

						} else if (e.keyCode == 100) {
							tankDirection = 3;
						}

						try {

							out.println("ID: " + id + " X: " + x + " Y: " + y + " D: " + tankDirection + " M: "
									+ tankModel);
						} catch (Exception ex) {
							System.out.println("The server did not respond (write KL).");
						}

						canvas.redraw();
					}

				}
			}

			public void keyReleased(KeyEvent e) {
			}
		});

		// create a menu bar
		Menu menuBar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menuBar);

		MenuItem modelItem = new MenuItem(menuBar, SWT.CASCADE);
		modelItem.setText("Tank Models");

		Menu modelMenu = new Menu(shell, SWT.DROP_DOWN);
		modelItem.setMenu(modelMenu);

		MenuItem model1 = new MenuItem(modelMenu, SWT.PUSH);
		model1.setText("Model 1");

		MenuItem model2 = new MenuItem(modelMenu, SWT.PUSH);
		model2.setText("Model 2");

		model1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tankModel = 1;
				canvas.redraw();
			}
		});

		model2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tankModel = 2;
				canvas.redraw();
			}
		});

		System.out.println("testing");
		Runnable runnable = new Runner();
		display.timerExec(1, runnable);
		shell.open();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();

		display.dispose();
	}

	/*
	 * Runner class that is used to communicate with the server and read the
	 * messages coming from the server.
	 */
	class Runner implements Runnable {

		/*
		 * Run method implemented from the Runnable interface. This method is called by
		 * the display.timerExec() method. Author: Marin Maksutaj
		 */
		public void run() {
			try {
				if (in.available() > 0) {
					Scanner sin = new Scanner(in);
					
					String line = sin.nextLine();
					if (line == "") {
						System.out.println("The server did not respond (read KL).");
						return;
					}
					System.out.println(line);

					String[] parts = line.split(" ");
					String status = parts[0];
					int tmpid = Integer.parseInt(parts[1]);
					int x = Integer.parseInt(parts[3]);
					int y = Integer.parseInt(parts[5]);
					int d = Integer.parseInt(parts[7]);
					int m = Integer.parseInt(parts[9]);

					if (status.equals("YOURID:")) {
						id = tmpid;
						XTankUI.this.x = x;
						XTankUI.this.y = y;
						tankDirection = d;
						map = parts[parts.length - 1];
						health = Integer.parseInt(parts[parts.length - 2]);
						int playersNum = Integer.parseInt(parts[parts.length - 3]);
						healthText.setText("Health: " + health + "     ");

						filledCoordsMyTank.clear();
						fillCoords(x, y, "My Tank");

						if (map.equals("MAP1")) {

							fillCoords(100, 100, "Obstacle1");
							fillCoords(300, 500, "Obstacle2");
						}

						if (map.equals("MAP2")) {

							fillCoords(200, 600, "Obstacle3");
							fillCoords(400, 100, "Obstacle4");

						}

						if (map.equals("MAP3")) {

							fillCoords(650, 100, "Obstacle1");
							fillCoords(200, 500, "Obstacle2");

						}

						while (isObstacleCollision().equals("tank") || isObstacleCollision().equals("both")) {

							XTankUI.this.x = (int) (Math.random() * 500);
							XTankUI.this.y = (int) (Math.random() * 500);

							filledCoordsMyTank.clear();
							fillCoords(XTankUI.this.x, XTankUI.this.y, "My Tank");
						}
						
						System.out.println("num "+playersNum);
						for(int i =0; i < playersNum; i ++) {
							String l = sin.nextLine();
							
							String[] partsl = l.split(" ");
							String statusl = partsl[0];
							int tmpidl = Integer.parseInt(partsl[1]);
							int xl = Integer.parseInt(partsl[3]);
							int yl = Integer.parseInt(partsl[5]);
							int dl = Integer.parseInt(partsl[7]);
							
							System.out.println("input "+l);
							
							
							if (statusl.equals("ID:") && id != tmpidl)
							{
								System.out.println("in: " + tmpidl);
								enemyTanks.put(tmpidl, new Integer[] {xl, yl, dl});
								
							}	
		
						}
						System.out.println("Enemy count 1: " + enemyTanks.size());
						
						out.println("ID: " + id + " X: " + XTankUI.this.x + " Y: " +
						 XTankUI.this.y + " D: " + tankDirection+ " M: " + 1);
	
						canvas.redraw();

					}

					else if (status.equals("ID:") && id != tmpid)
					{
						enemyTanks.put(tmpid, new Integer[] {x, y, d});
						System.out.println("Enemy count: " + enemyTanks.size());
						canvas.redraw();
					}

					else if (status.equals("BULLET:") && id != tmpid) {
						Bullet bullet = new Bullet(x, y, tmpid, d, m);
						enemyBulletsList.add(bullet);

						Timer timer = new Timer();
						timer.scheduleAtFixedRate(new TimerTask() {
							@Override
							public void run() {
								Display.getDefault().asyncExec(new Runnable() {
									public void run() {

										bullet.incrementY();
										canvas.redraw();

										if (bullet.getY() <= -10) {
											enemyBulletsList.remove(bullet);
											timer.cancel();
											canvas.redraw();
										}
									}
								});
							}
						}, 0, 50);

						canvas.redraw();
					}

					else if (status.equals("REMOVE:") && id != tmpid) {

						enemyTanks.remove(tmpid);
						if (enemyTanks.size() == 0) {
							healthText.setText("WINNER");
							out.println("WINNER");
						}

						canvas.redraw();
					}

				}
			} catch (IOException ex) {
				System.out.println(ex);
			}
			display.timerExec(1, this);
		}
	};
}
