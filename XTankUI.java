
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
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Scanner;

public class XTankUI
{
	// The location and direction of the "tank"
	private int x = 300;
	private int y = 500;
	private int id;
	private Map<Integer, Integer[]> enemyTanks;
	private int directionX = 0;
	private int directionY = -10;
	private Canvas canvas;
	private Display display;
	private List<Bullet> bulletsList;
	
	
	DataInputStream in; 
	PrintWriter out;
	
	public XTankUI(DataInputStream in, DataOutputStream out)
	{
		this.in = in;
		this.out = new PrintWriter(out, true);
		this.id = -1;
		this.enemyTanks = new HashMap<>();
		this.bulletsList = new ArrayList<>();
	}
	
	public void start()
	{
		System.out.println("Testingg");
		display = new Display();
		Shell shell = new Shell(display);
		shell.setText("xtank");
		shell.setLayout(new FillLayout());

		canvas = new Canvas(shell, SWT.NO_BACKGROUND);

		canvas.addPaintListener(event -> {
			event.gc.fillRectangle(canvas.getBounds());
			event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
			event.gc.fillRectangle(x, y, 50, 100);
			event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
			event.gc.fillOval(x, y+25, 50, 50);
			event.gc.setLineWidth(4);
			event.gc.drawLine(x+25, y+25, x+25, y-15);

			// draw the enemy tanks
			for (Integer[] enemyTank : enemyTanks.values())
			{
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
				event.gc.fillRectangle(enemyTank[0], enemyTank[1], 50, 100);
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
				event.gc.fillOval(enemyTank[0], enemyTank[1]+25, 50, 50);
				event.gc.setLineWidth(4);
				event.gc.drawLine(enemyTank[0]+25, enemyTank[1]+25, enemyTank[0]+25, enemyTank[1]-15);
			}
			
			for (int i = 0; i < bulletsList.size(); i++) {
				Bullet bullet = bulletsList.get(i);
				event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
				event.gc.fillRectangle( bullet.getX(), bullet.getY(), 10, 10);
				
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
					
					Bullet bullet = new Bullet(x + 20, y - 30); 
					bulletsList.add(bullet);
					
					Timer timer = new Timer();
					timer.scheduleAtFixedRate(new TimerTask() {
	                    @Override
	                    public void run() {
	                        Display.getDefault().asyncExec(new Runnable() {
	                            public void run() {
	                            	
									if(bullet.getY() > 0) {
										bullet.incrementY();
										canvas.redraw();
										
									} else {
										bulletsList.remove(bullet);
										timer.cancel();
									}
	                            }
	                        });
	                    }
	                },0,1000);
										
					
					try {
						out.println("ID: " + id + " X: " + x + " Y: " + y);
					}
					catch(Exception ex) {
						System.out.println("The server did not respond (write KL).");
					}
					canvas.redraw();
					
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
					System.out.println("testing!!");
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
				}
			}
			catch(Exception ex) {
				System.out.println("The server did not respond (async).");
			}				
            display.timerExec(150, this);
		}
	};	
	
	
	 class Bullet{
		 
		 private int x;
		 private int y;
		 
		
		 public Bullet(int x, int y) {
			 this.x = x;
			 this.y =y;
		 }
		 
		 public int getX() {
			 return x; 
		 }
		 
		 public int getY() {
			 return y; 
		 }
		 
		 public void incrementY() {
			 y=y-10;
		 }
	}
}


