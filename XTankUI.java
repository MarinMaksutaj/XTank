
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;
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
	private int health = 3;
	private int id;
	private Map<Integer, Integer[]> enemyTanks;
	private int directionX = 0;
	private int directionY = -10;
	private Canvas canvas;
	private Display display;
	private List<Bullet> bulletsList;
	private List<Bullet> enemyBulletsList;
	private Set<Coordinate> filledCoordsMyTank;
	private Set<Coordinate> filledCoordsEnemyTank;
	private Set<Coordinate> filledCoordsBullet;
	
	DataInputStream in; 
	PrintWriter out;
	
	public XTankUI(DataInputStream in, DataOutputStream out)
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
			
		}
		else {
			for(int i =x; i <=x+10; i++) {
				for(int j = y ; j <= y+10; j++) {
				Coordinate toAdd = new Coordinate(i,j);
				filledCoordsBullet.add(toAdd);
			}}	
		}
		
	}
	
	public String isCollision() {
			
		 	boolean enemyCollision = false;
		 	boolean myCollision = false;
		 	
		 	for(Coordinate bulletCoord: filledCoordsBullet) {
		 		for(Coordinate tankCoord: filledCoordsEnemyTank) {
		 			
		 			if(tankCoord.getCoord()[0] == bulletCoord.getCoord()[0] && 
		 					tankCoord.getCoord()[1] == bulletCoord.getCoord()[1])
		 			{enemyCollision = true;}
		 		}
		 	}
		 	
		 	for(Coordinate bulletCoord: filledCoordsBullet) {
		 		for(Coordinate tankCoord: filledCoordsMyTank) {
		 			
		 			if(tankCoord.getCoord()[0] == bulletCoord.getCoord()[0] && 
		 					tankCoord.getCoord()[1] == bulletCoord.getCoord()[1])
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
	

	
	public void start()
	{
		System.out.println("Testingg");
		display = new Display();
		Shell shell = new Shell(display);
		shell.setText("xtank");
//		shell.setLayout(new FillLayout());
		
		GridLayout gridLayout = new GridLayout();
        shell.setLayout( gridLayout);
        
        shell.setSize(800,700);
        
        Text healthText = new Text(shell, SWT.READ_ONLY | SWT.BORDER);
        healthText.setText("Health: " + health);
        healthText.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
        
        Composite upperComp = new Composite(shell, SWT.NO_FOCUS);
        Composite lowerComp = new Composite(shell, SWT.NO_FOCUS);

//		canvas = new Canvas(shell, SWT.NO_BACKGROUND);
		
		
        
        canvas = new Canvas(upperComp, SWT.NONE);
        canvas.setSize(800,500);
        
//        Canvas lowerCanvas = new Canvas(lowerComp, SWT.NONE);
//        canvas.setSize(800,200);
        
        
	

		canvas.addPaintListener(event -> {	
			
			if(health>0) {
				event.gc.fillRectangle(canvas.getBounds());
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
				event.gc.fillRectangle(x, y, 50, 100);
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
				event.gc.fillOval(x, y+25, 50, 50);
				event.gc.setLineWidth(4);
				event.gc.drawLine(x+25, y+25, x+25, y-15);
				
				this.filledCoordsMyTank.clear();
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
				event.gc.drawLine(enemyTank[0]+25, enemyTank[1]+25, enemyTank[0]+25, enemyTank[1]-15);
				
				fillCoords(enemyTank[0], enemyTank[1], "Tank");
			}
			
			if(health>0) {
				
				for (int i = 0; i < bulletsList.size(); i++) {
					
					Bullet bullet = bulletsList.get(i);
					
					this.filledCoordsBullet.clear();
					fillCoords(bullet.getX(), bullet.getY(), "Bullet");

					event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
					event.gc.fillRectangle( bullet.getX(), bullet.getY(), 10, 10);
					
					if(!(isCollision().equals("none"))) {
						bulletsList.remove(i);
					}
					
				}
				
			}
			
			
			for (int i = 0; i < enemyBulletsList.size(); i++) {
				
				Bullet bullet = enemyBulletsList.get(i);
				
				this.filledCoordsBullet.clear();
				fillCoords(bullet.getX(), bullet.getY(), "Bullet");
				
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));
				event.gc.fillRectangle( bullet.getX(), bullet.getY(), 10, 10);
				
				if((isCollision().equals("mine") || isCollision().equals("both") ))  {
					health--;
					healthText.setText("Health: "+health);
					enemyBulletsList.remove(i);
				} else if(!(isCollision().equals("none"))) {
					enemyBulletsList.remove(i);
				}
				
				if(health<=0) {
					healthText.setText("GAME OVER");
					out.println("REMOVE: "+this.id + " X: -100 Y: -100");
					
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
				// update tank location
				
				if(e.keyCode == 32) {
					Bullet bullet = new Bullet(x + 20, y - 30, id); 
					bulletsList.add(bullet);
					
					try {
						out.println("BULLET: "+bullet.getId() + " X: " + bullet.getX() + " Y: " + bullet.getY());
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
				
				else if(e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.ARROW_DOWN || e.keyCode == SWT.ARROW_LEFT || e.keyCode == SWT.ARROW_RIGHT) {
					
					if (e.keyCode == SWT.ARROW_UP) {
						directionX = 0;
						directionY = -10;
					} else if (e.keyCode == SWT.ARROW_DOWN) {
						directionX = 0;
						directionY = 10;
						
					} else if (e.keyCode == SWT.ARROW_LEFT) {
						directionX = -10;
						directionY = 0;
						
					} else if (e.keyCode == SWT.ARROW_RIGHT) {
						directionX = 10;
						directionY = 0;
						
					} 
					
					x += directionX;
					y += directionY;
					
					try {
						
						out.println("ID: " + id + " X: " + x + " Y: " + y);
					}
					catch(Exception ex) {
						System.out.println("The server did not respond (write KL).");
					}
					canvas.redraw();
					

					
				}
				
				
				

				
			}
			public void keyReleased(KeyEvent e) {}
		});

		System.out.println("testing");				
		Runnable runnable = new Runner();
		display.asyncExec(runnable);
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
					// current format: "YOURID: 1 X: 300 Y: 500"
					// or "ENEMYID: 1 X: 300 Y: 500"
					String[] parts = line.split(" ");
					String status = parts[0];
					int tmpid = Integer.parseInt(parts[1]);
					int x = Integer.parseInt(parts[3]);
					int y = Integer.parseInt(parts[5]);
					if (status.equals("YOURID:"))
					{
						id = tmpid;
						XTankUI.this.x = x;
						XTankUI.this.y = y;
						canvas.redraw();
					}
					else if (status.equals("ID:") && id != tmpid)
					{
						enemyTanks.put(tmpid, new Integer[] {x, y});
						System.out.println("Enemy count: " + enemyTanks.size());
						canvas.redraw();
					}
					
					else if (status.equals("BULLET:") && id != tmpid)
					{
						Bullet bullet = new Bullet(x,y,tmpid);
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
			catch(Exception ex) {
				System.out.println("The server did not respond (async).");
			}				
            display.timerExec(1, this);
		}
	};	
	
	
	 class Bullet{
		 
		 private int x;
		 private int y;
		 private int id;
		 
		
		 public Bullet(int x, int y, int id) {
			 this.x = x;
			 this.y =y;
			 this.id = id; 
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
		 
		 public void incrementY() {
			 y=y-10;
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
		 
		
	}
}


