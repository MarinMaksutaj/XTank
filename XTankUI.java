
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Scanner;

public class XTankUI
{
	// The location and direction of the "tank"
	private int x = 300;
	private int y = 500;
	private double health = 3;
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
	
	// keep track of the tank direction
	private int tankDirection = 0; // 0 = up, 1 = right, 2 = down, 3 = left
	
	DataInputStream in; 
	PrintWriter out;
	
	public XTankUI(DataInputStream in, DataOutputStream out, String map)
	{
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
		
		if(map.equals("MAP2")) {
			fillCoords(100,100,"Obstacle1");
			fillCoords(300,500,"Obstacle2");
		}
			
	
	
		
	}

	private int getBulletDirection(Bullet bullet) {
		// look at the bullet's id and return the direction of the tank that fired it
		int id = bullet.getId();
		// check the current tank first
		if(id == this.id) {
			return tankDirection;
		}
		// check the other tanks
		for(Integer key : enemyTanks.keySet()) {
			if(key == id) {
				return enemyTanks.get(key)[2];
			}
		}
		return -1;
	}
	
	private void fillCoords(int x, int y, String type) {
		
		if(type.equals("Tank")) {
			for(int i =x; i <=x+50; i++) {
				for(int j = y ; j <= y+100; j++) {
				Coordinate toAdd = new Coordinate(i,j);
				filledCoordsEnemyTank.add(toAdd);
			}}
		} else if(type.equals("My Tank")) {
			for(int i =x; i <=x+50; i++) {
				for(int j = y ; j <= y+100; j++) {
				Coordinate toAdd = new Coordinate(i,j);
				filledCoordsMyTank.add(toAdd);
			}}
			
		} else if(type.equals("Obstacle1")) {
			for(int i =x; i <=x+50; i++) {
				for(int j = y ; j <= y+200; j++) {
				Coordinate toAdd = new Coordinate(i,j);
				filledCoordsObstacles.add(toAdd);
			}}
		}
		else if(type.equals("Obstacle2")) {
			for(int i =x; i <=x+300; i++) {
				for(int j = y ; j <= y+50; j++) {
				Coordinate toAdd = new Coordinate(i,j);
				filledCoordsObstacles.add(toAdd);
			}}
		}
		else {
			for(int i =x; i <=x+10; i++) {
				for(int j = y ; j <= y+10; j++) {
				Coordinate toAdd = new Coordinate(i,j);
				filledCoordsBullet.add(toAdd);
			}}	
		}
		
	}
	
	public String isBulletCollision() {
			
		 	boolean enemyCollision = false;
		 	boolean myCollision = false;
		 	
		 	for(Coordinate bulletCoord: filledCoordsBullet) {
		 		if(filledCoordsEnemyTank.contains(bulletCoord) ) {
		 			{enemyCollision = true;
		 			}
		 		}
		 		if(filledCoordsMyTank.contains(bulletCoord) ) {
		 			{myCollision = true;}
		 		}
		 		
		 	}
		 	
		 	
		 	if(enemyCollision && myCollision) {
		 		return "both";
		 	} else if(enemyCollision) {
		 		return "enemy";
		 	}else if (myCollision){
		 		return "mine";
		 	}else {
		 		return "none";
		 	}
			
			
		}
	
	public String isObstacleCollision() {
			
			boolean tankCollision = false;
			boolean bulletCollision = false;
			
	 		for(Coordinate tankCoord: filledCoordsMyTank) {	
	 			if(filledCoordsObstacles.contains(tankCoord))
	 			{
	 			   System.out.println(tankCoord.getCoord()[0] + " " + tankCoord.getCoord()[1]);
	 		       tankCollision = true;
	 		       break;
	 			}
	 		}
	 		
	 		for(Coordinate bulletCoord: filledCoordsBullet) {	
	 			if(filledCoordsObstacles.contains(bulletCoord))
	 			{
	 				bulletCollision = true;
		 		       break;
	 			}
	 		}
	 		
	 	if(tankCollision && bulletCollision) {
	 		return "both";
	 	}else if(tankCollision) {
	 		return "tank";
	 	}else if(bulletCollision) {
	 		return "bullet";
	 	}
	 	return "none";
		
		
	}

	
	public void start()
	{
		System.out.println("Testingg");
		display = new Display();
		Shell shell = new Shell(display);
		shell.setText("xtank");

		
		GridLayout gridLayout = new GridLayout();
        shell.setLayout( gridLayout);
        
        shell.setSize(800,850);
        
        Text healthText = new Text(shell, SWT.READ_ONLY | SWT.BORDER);
        healthText.setText("Health: " + health);
        healthText.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
        
        Composite upperComp = new Composite(shell, SWT.NO_FOCUS);
        Composite lowerComp = new Composite(shell, SWT.NO_FOCUS);


        canvas = new Canvas(upperComp, SWT.NONE);
        canvas.setSize(800,650); 

	

		canvas.addPaintListener(event -> {	
			
			event.gc.fillRectangle(canvas.getBounds());
			this.filledCoordsMyTank.clear();
			
			if(map.equals("MAP2")) {
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				event.gc.fillRectangle(100,100, 50, 200);
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				event.gc.fillRectangle(300,500, 300, 50);
			}
			
			if(health>0) {
				
				event.gc.setBackground(shell.getDisplay().getSystemColor(tankModel == 1 ? SWT.COLOR_DARK_GREEN : SWT.COLOR_BLUE));
				event.gc.fillRectangle(x, y, 50, 100);
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
				event.gc.fillOval(x, y+25, 50, 50);
				event.gc.setLineWidth(4);
				// draw the line based on the direction
				if(tankDirection == 0) {
					event.gc.drawLine(x+25, y+25, x+25, y-25);
				} else if(tankDirection == 1) {
					// down
					event.gc.drawLine(x+25, y+75, x+25, y+125);
				} else if(tankDirection == 2) {
					// draw line to the left
					event.gc.drawLine(x, y + 50, x - 25, y + 50);
				} else if(tankDirection == 3) {
					// draw line to the right
					event.gc.drawLine(x+50, y+50, x+75, y+50);
				}
				
				
				fillCoords(x,y, "My Tank");
				
			}
			
	
			// draw the enemy tanks
			
			this.filledCoordsEnemyTank.clear();
			
			for (Integer[] enemyTank : enemyTanks.values())
			{
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
				event.gc.fillRectangle(enemyTank[0], enemyTank[1], 50, 100);
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
				event.gc.fillOval(enemyTank[0], enemyTank[1]+25, 50, 50);
				event.gc.setLineWidth(4);
				// draw the line based on the direction
				if(enemyTank[2] == 0) {
					event.gc.drawLine(enemyTank[0]+25, enemyTank[1]+25, enemyTank[0]+25, enemyTank[1]-25);
				} else if(enemyTank[2] == 1) {
					// down
					event.gc.drawLine(enemyTank[0]+25, enemyTank[1]+75, enemyTank[0]+25, enemyTank[1]+125);
				} else if(enemyTank[2] == 2) {
					// draw line to the left
					event.gc.drawLine(enemyTank[0], enemyTank[1] + 50, enemyTank[0] - 25, enemyTank[1] + 50);
				} else if(enemyTank[2] == 3) {
					// draw line to the right
					event.gc.drawLine(enemyTank[0]+50, enemyTank[1]+50, enemyTank[0]+75, enemyTank[1]+50);
				}
				
				fillCoords(enemyTank[0], enemyTank[1], "Tank");
			}
			
			if(health>0) {
				
				for (int i = 0; i < bulletsList.size(); i++) {
					
					Bullet bullet = bulletsList.get(i);
					this.filledCoordsBullet.clear();
					fillCoords(bullet.getX(), bullet.getY(), "Bullet");

					// if the bullet is out of bounds, remove it
					if(bullet.getX() < 0 || bullet.getX() > 800 || bullet.getY() < 0 || bullet.getY() > 650) {	
						bulletsList.remove(i);
					}
					
					else if((!(isBulletCollision().equals("none"))) || isObstacleCollision().equals("bullet")  
							|| isObstacleCollision().equals("both")) {
						bulletsList.remove(i);
					} 
					
					else {
						
						

						event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
						event.gc.fillRectangle( bullet.getX(), bullet.getY(), 10, 10);
		
					}
					
					
					
				}
				
			}
			
			
			for (int i = 0; i < enemyBulletsList.size(); i++) {
				
				Bullet bullet = enemyBulletsList.get(i);
				this.filledCoordsBullet.clear();
				fillCoords(bullet.getX(), bullet.getY(), "Bullet");
				// if the bullet is out of bounds, remove it
				if(bullet.getX() < 0 || bullet.getX() > 800 || bullet.getY() < 0 || bullet.getY() > 650) {
					enemyBulletsList.remove(i);
				}
				
				else if((isBulletCollision().equals("mine") || isBulletCollision().equals("both") ))  {
					health -= bullet.getDamage();
					healthText.setText("Health: "+health);
					enemyBulletsList.remove(i);

					if(health<=0) {
						healthText.setText("GAME OVER");
						out.println("REMOVE: "+this.id + " X: -100 Y: -100 D: -1 M: -1");
						
						Button quitButton = new Button(lowerComp, SWT.PUSH);
				        quitButton.setText("Quit");
				        quitButton.setSize(60, 50);
				        quitButton.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
				        quitButton.addListener(SWT.Selection, new Listener() {
				            public void handleEvent(Event e) {
				            	System.exit(0);
				            }
				          });
						
					}
				} 
				
				else if(!(isBulletCollision().equals("none")) || isObstacleCollision().equals("bullet") || 
						isObstacleCollision().equals("both")) {
					enemyBulletsList.remove(i);
				}
				
				else {
					
					
					
					event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));
					event.gc.fillRectangle( bullet.getX(), bullet.getY(), 10, 10);
					
					
					
					if(health<=0) {
						healthText.setText("GAME OVER");
						out.println("REMOVE: "+this.id + " X: -100 Y: -100 D: -1 M: -1");
						
						Button quitButton = new Button(lowerComp, SWT.PUSH);
				        quitButton.setText("Quit");
				        quitButton.setSize(60, 50);
				        quitButton.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
				        quitButton.addListener(SWT.Selection, new Listener() {
				            public void handleEvent(Event e) {
				            	System.exit(0);
				            }
				          });
						
					}
				}
				
				
				
			}
			
			
			
	
		}	
		);	
		

		canvas.addMouseListener(new MouseListener() {
			public void mouseDown(MouseEvent e) {
				System.out.println("mouseDown in canvas");
			} 
			public void mouseUp(MouseEvent e) {} 
			public void mouseDoubleClick(MouseEvent e) {} 
		});

		canvas.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				System.out.println("key " + e.keyCode + " pressed");
				
				if(health > 0) {
					
					if(e.keyCode == 32) {
						
						Bullet bullet = new Bullet(x + 20, y - 30, id, 0, tankModel);
						int bulletDir = getBulletDirection(bullet);
						bullet.setDirection(bulletDir);
						bulletsList.add(bullet);
						
						try {
							out.println("BULLET: "+bullet.getId() + " X: " + bullet.getX() + " Y: " + bullet.getY() + " D: " + bulletDir + " M: " + tankModel);
						}
						catch(Exception ex) {
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
		
										if(bullet.getY() <= -10) {
											bulletsList.remove(bullet);
											timer.cancel();
											canvas.redraw();
										}
		                            }
		                        });
		                    }
		                },0,50);
											
						
						
						
						
					} 
					
					else if(e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.ARROW_DOWN || e.keyCode == SWT.ARROW_LEFT || 
							e.keyCode == SWT.ARROW_RIGHT || e.keyCode == 119 || e.keyCode == 115 || e.keyCode == 97 || 
							e.keyCode == 100) {

						if (e.keyCode == SWT.ARROW_UP) {
							
								directionX = 0;
								directionY = tankModel == 1 ? -15 : -5;
								x += directionX;
								y += directionY;
								
								filledCoordsMyTank.clear();
								fillCoords(x,y, "My Tank");
								
								if(isObstacleCollision().equals("tank") || isObstacleCollision().equals("both")){
									x -= 2 * directionX;
									y -= 2 * directionY;
									
								}
	
							
						} else if (e.keyCode == SWT.ARROW_DOWN) {
							
							directionX = 0;
							directionY = tankModel == 1 ? 15 : 5;
							x += directionX;
							y += directionY;
							
							filledCoordsMyTank.clear();
							fillCoords(x,y, "My Tank");
							
							if(isObstacleCollision().equals("tank") || isObstacleCollision().equals("both")){
								x -= 2 * directionX;
								y -= 2 * directionY;
							}
							
							
						} else if (e.keyCode == SWT.ARROW_LEFT) {
							
							directionX = tankModel == 1 ? -15 : -5;
							directionY = 0;
							x += directionX;
							y += directionY;
							
							filledCoordsMyTank.clear();
							fillCoords(x,y, "My Tank");
							
							if(isObstacleCollision().equals("tank") || isObstacleCollision().equals("both")){
								x = x - (2 * directionX);
							}
							
							
						} else if (e.keyCode == SWT.ARROW_RIGHT) {
					
							directionX = tankModel == 1 ? 15 : 5;
							directionY = 0;
							x += directionX;
							y += directionY;
							
							filledCoordsMyTank.clear();
							fillCoords(x,y, "My Tank");
							
							if(isObstacleCollision().equals("tank") || isObstacleCollision().equals("both")){
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
							
							out.println("ID: " + id + " X: " + x + " Y: " + y + " D: " + tankDirection + " M: " + tankModel);
						}
						catch(Exception ex) {
							System.out.println("The server did not respond (write KL).");
						}
						
						canvas.redraw();}
					
				}
				
				
				

				
			}
			public void keyReleased(KeyEvent e) {}
		});
		
		// create a menu bar
		Menu menuBar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menuBar);

		// create a menu item
		MenuItem modelItem = new MenuItem(menuBar, SWT.CASCADE);
		modelItem.setText("Tank Models");

		// create a menu
		Menu modelMenu = new Menu(shell, SWT.DROP_DOWN);
		modelItem.setMenu(modelMenu);

		// add a model to the menu
		MenuItem model1 = new MenuItem(modelMenu, SWT.PUSH);
		model1.setText("Model 1");

		// add a model to the menu
		MenuItem model2 = new MenuItem(modelMenu, SWT.PUSH);
		model2.setText("Model 2");

		// set the selection listener for the menu items
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
	
	class Runner implements Runnable
	{
		public void run() 
		{
							
			try {
				if (in.available() > 0) {
					Scanner sin = new Scanner(in);
					String line = sin.nextLine();
					if (line == "") {
						System.out.println("The server did not respond (read KL).");
						return;
					}
					System.out.println(line);
					
					
					// update tank location
					// current format: "YOURID: 1 X: 300 Y: 500 D: 0"
					// or "ENEMYID: 1 X: 300 Y: 500 D: 0"
					String[] parts = line.split(" ");
					String status = parts[0];
					int tmpid = Integer.parseInt(parts[1]);
					int x = Integer.parseInt(parts[3]);
					int y = Integer.parseInt(parts[5]);
					int d = Integer.parseInt(parts[7]);
					int m = Integer.parseInt(parts[9]);
					if (status.equals("YOURID:"))
					{
						id = tmpid;
						XTankUI.this.x = x;
						XTankUI.this.y = y;
						tankDirection = d;
						
						filledCoordsMyTank.clear();
						fillCoords(x,y, "My Tank");
						
						while(isObstacleCollision().equals("tank") || 
								isObstacleCollision().equals("both")) {
							
							XTankUI.this.x = (int)(Math.random()*500);
							XTankUI.this.y = (int)(Math.random()*500);
							
							filledCoordsMyTank.clear();
							fillCoords(XTankUI.this.x, XTankUI.this.y, "My Tank");
						}
						
						out.println("ID: " + id + " X: " + XTankUI.this.x + " Y: " + XTankUI.this.y + " D: " + tankDirection);
						canvas.redraw();
						
					}
					else if (status.equals("ID:") && id != tmpid && (!enemyTanks.containsKey(tmpid)))
					{
						out.println("ID: " + id + " X: " + XTankUI.this.x + " Y: " + XTankUI.this.y + " D: " + tankDirection);	
						enemyTanks.put(tmpid, new Integer [] {x, y, d});
						canvas.redraw();
						
					}	
					else if (status.equals("ID:") && id != tmpid)
					{
						enemyTanks.put(tmpid, new Integer [] {x, y, d});		
						
						canvas.redraw();
					}	
					
					 
					else if (status.equals("BULLET:") && id != tmpid)
					{
						Bullet bullet = new Bullet(x,y,tmpid, d, m);
						enemyBulletsList.add(bullet);
						
						Timer timer = new Timer();
						timer.scheduleAtFixedRate(new TimerTask() {
		                    @Override
		                    public void run() {
		                        Display.getDefault().asyncExec(new Runnable() {
		                            public void run() {
		                            	
		                            	bullet.incrementY();
										canvas.redraw();
								
										if(bullet.getY() <= -10) {
											enemyBulletsList.remove(bullet);
											timer.cancel();
											canvas.redraw();
										}
		                            }
		                        });
		                    }
		                },0,50);
						
						canvas.redraw();
					}
					
					else if(status.equals("REMOVE:") && id != tmpid ) {
						
						enemyTanks.remove(tmpid);
						canvas.redraw();
					}
					
				}
			}
			catch(IOException ex) {
				System.out.println(ex);
			}				
            display.timerExec(1, this);
//			display.asyncExec(this);
		}
	};	
	
	
	 class Bullet{
		 
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
	 
	class Coordinate{
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
}


