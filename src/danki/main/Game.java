package danki.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import danki.entities.BulletShoot;
import danki.entities.Enemy;
import danki.entities.Entity;
import danki.entities.Player;
import danki.grafics.Spritesheet;
import danki.grafics.UI;
import danki.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener{
	
	private static final long serialVersionUID = 1L;
	
	//Frame Event 
	public static JFrame frame;
	public static final int WIDTH = 240; 
	public static final int HEIGHT = 160;
	public static final int SCALE = 3; //SCALE -> pixel effect while rendering
	
	//Standarts 
	private Thread thread;
	private boolean isRunning = true;
	private BufferedImage image;
	public static Spritesheet spritesheet;
	
	//Level
	private int CURRENT_LEVEL = 1;
	private int MAX_LEVEL = 2;
	
	//Lists
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<BulletShoot> bullets;
	
	//Init other classes
	public static World world;
	public static Player player;
	public static Random rand;
	public Menu menu;
	public UI ui;
	
	//Game State && GameOver
	public static String gameState = "MENU";
	private boolean showMessegeGameOver = true;
	private int framesGameOver = 0;
	private boolean restartGame = false;
	
	
	//Constructor
	public Game() {
		// Listener 
		addKeyListener(this); 
		addMouseListener(this);
		
		// Frame
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		initFrame();
		
		// Create Objects (Right Sequence)
		ui = new UI();
		image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<BulletShoot>();
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0, 0, 16, 16, spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		world = new World("/level1.png");
		rand = new Random();
		menu = new Menu();
	}
	
	//Standart Init Frame
	public void initFrame() {
		frame = new JFrame("SEFIAM KILLER");
		frame.add(this);
		frame.setResizable(false); //not resizable
		frame.pack(); //pack after set size
		frame.setLocationRelativeTo(null); //center window 
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	//THREADS -> Perform <3
	//Thread to execute the method 'run()' in this 'Game' class
	public synchronized void start() { 
		thread = new Thread(this); 
		isRunning = true;
		thread.start();
	}
	
	//Stop Thread
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	//MAIN
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}
	
	
	public void tick() {
		//GAME LOGIC / UPDATE & VERIFICATION -> Tick + Render + Run = Game Loop
		// Organized in Game States 
		
		if(gameState == "NORMAL") {
			
			//In Case press ENTER when its not Game Over
			this.restartGame = false; 
			
			//Start entities tick
			for(int i = 0 ; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.tick();
			}
			//Start bullets tick
			for(int i = 0; i < bullets.size(); i++) {
				bullets.get(i).tick();
			}
			
			//NEXT LEVEL SYSTEM
			if(enemies.size() == 0) { //All enemies gone
				CURRENT_LEVEL++; //Next level
				if(CURRENT_LEVEL > MAX_LEVEL) {
					CURRENT_LEVEL = 1;
				}
				String newWorld = "Level" + CURRENT_LEVEL +".png"; 
				World.restartGame(newWorld);
			}
		
		
		}else if(gameState == "GAME_OVER") {
			
			//Blink Effect
			this.framesGameOver++;
			if(this.framesGameOver == 30) {
				this.framesGameOver = 0;
				if(this.showMessegeGameOver) {
					this.showMessegeGameOver = false;
				}else {
					this.showMessegeGameOver = true;
				}
			}
			// Reset Event
			if(restartGame) {
				this.restartGame = false;
				Game.gameState = "NORMAL";
				CURRENT_LEVEL = 1;
				String newWorld = "Level" + CURRENT_LEVEL +".png"; 
				World.restartGame(newWorld);
			}
	
		}else if(gameState == "MENU") {
			menu.tick();
		}
	}
	
	//Renderization of visual aspects 
	public void render() {
		
		//Standart BufferStrategy (Perform)
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		//Graphics Config
		Graphics g = image.getGraphics();	
		
		//Classes render
		world.render(g);
		ui.render(g);
		
		//Render Entities
		for(int i = 0 ; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		//Render bullets
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).render(g);
		}
		
		//Frame Render Config
		g.dispose(); //clean data in image (performace)
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		
		//Ammor Score
		g.setFont(new Font("arial", Font.BOLD, 20));
		g.setColor(Color.white);
		g.drawString("Ammo: "+ player.ammo, 620, 30);
		
		// GAME OVER SCREEN
		if(gameState == "GAME_OVER") {
			// Dark Screen
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0,0,0,100));
			g2.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
			
			// String Gameover
			g.setFont(new Font("arial", Font.BOLD, 28));
			g.setColor(Color.white);
			g.drawString("Game Over", (WIDTH*SCALE) / 2 - 70, (HEIGHT*SCALE) / 2);
			if(showMessegeGameOver) {
				g.drawString("Press ENTER to restart", (WIDTH*SCALE) / 2 - 140, (HEIGHT*SCALE) / 2 + 40);
			}

		}else if(gameState == "MENU") {
			menu.render(g);
		}
		
		bs.show(); //Show pre-seted Buffered Strategy renderization 
	}
	
	//GAME LOOPING ADVANCED
	public void run() {
	    long lastTime = System.nanoTime(); 
	    //Get the current system time in nanoseconds (precision).
	    double amountOfTicks = 60.0; 
	    //Set the desired game update rate to 60 FPS.
	    double ns = 1000000000 / amountOfTicks; 
	    //Convert 1 second to nanoseconds and divide by the update rate to get the number of nanoseconds between each update.
	    double delta = 0;
	    int frames = 0; //Frame counter.
	    double timer = System.currentTimeMillis(); 
	    //Get the current system time in milliseconds (lower precision).
	    
	    requestFocus();
	    while (isRunning) {
	        long now = System.nanoTime(); 
	        //Get the current time in nanoseconds on each iteration of the loop.
	        delta += (now - lastTime) / ns; 
	        //Calculate how much time has passed since the last update relative to the expected time for the next update.
	        lastTime = now;

	        if (delta >= 1) {
	            tick(); //Call the game's update method.
	            render(); //Call the game's render method.
	            frames++; //Increment the frame counter.

	            delta--; //Decrease delta to prepare for the next update.
	        }

	        if (System.currentTimeMillis() - timer >= 1000) {
	            System.out.println("FPS: " + frames); 
	            //Print the number of frames processed in 1 second.
	            frames = 0; //Reset the frame counter.
	            timer += 1000; //Increment the timer by 1 second.
	        }
	    }
	    stop(); //out of the game looping
	}
	
	//KEY EVENTS
	public void keyTyped(KeyEvent e) {
		
	}
	public void keyPressed(KeyEvent e) {
		
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}
		//diferent 'if's in case douple key pressed
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = true;
			if(gameState == "MENU") {
				menu.up = true;
			}
		}else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;
			if(gameState == "MENU") {
				menu.down = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_R) {
			player.shoot = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.restartGame = true;
			if(gameState == "MENU") {
				menu.enter = true;
			} 
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			gameState = "MENU";
			menu.pause = true;
		}
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			player.jump = true;
		}
	}

	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;
		}else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;
		}
	}

	//MOUSE EVENTS 
	public void mouseClicked(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mouse_X = (e.getX() / 3);
		player.mouse_Y = (e.getY() / 3);
	}

	public void mouseReleased(MouseEvent e) {
		
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}
}