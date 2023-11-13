package danki.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import danki.entities.*;
import danki.grafics.Spritesheet;
import danki.main.Game;

public class World {
	
	public static Tile[] tiles; 
	public static int WIDTH;
	public static int HEIGHT;
	public static final int TILE_SIZE = 16;
	
	public World(String path) {
		try {
			BufferedImage map = ImageIO.read(getClass().getResource(path)); 
			//map image path (instantiated in the main Game constructor meth)
			int[] pixels = new int[map.getWidth() * map.getHeight()];
			//pixel array to keep the color values of the map image 
			
			WIDTH = map.getHeight();
			HEIGHT = map.getHeight();
			
			tiles = new Tile[WIDTH * HEIGHT]; 
			//tiles array will have the size of the map
			
			map.getRGB(0, 0, WIDTH, HEIGHT, pixels, 0, WIDTH);
			//get every single pixels colors of the map and keep it in the array 'pixel'
			
			for(int xx = 0; xx < WIDTH; xx++) {
				for(int yy = 0; yy < HEIGHT; yy++) {
					//Double Loop that loops through each pixel in the image
					int currentPixel = pixels[xx + (yy * WIDTH)];
					// save current pixel to check the Tiles
					//(yy * WIDTH) -> vertical iteration 
					tiles[xx + (yy * WIDTH)] = new FloorTile(xx*TILE_SIZE, yy*TILE_SIZE, Tile.TILE_FLOOR);
					//FloorTile as standart cover 
					
					if(currentPixel == 0xFFFFFFFF) { // Wall
						tiles[xx + (yy * WIDTH)] = new WallTile(xx*TILE_SIZE, yy*TILE_SIZE, Tile.TILE_WALL);	
					}else if(currentPixel == 0xFF0094FF) { //Player
						Game.player.setX(xx*TILE_SIZE);
						Game.player.setY(yy*TILE_SIZE);
					}else if(currentPixel == 0xFFFF0000) { //Enemy
						Enemy en = new Enemy(xx*TILE_SIZE, yy*TILE_SIZE, 16, 16, Entity.ENEMY_EN);
						Game.entities.add(en);
						Game.enemies.add(en);
						
					}else if(currentPixel == 0xFF00FFFF) { //Weapon
						Game.entities.add(new Weapon(xx*TILE_SIZE, yy*TILE_SIZE, 16, 16, Entity.WEAPON_EN)); 
						
					}else if(currentPixel == 0xFF00FF21) { //Lifepack
						Game.entities.add(new Lifepack(xx*TILE_SIZE, yy*TILE_SIZE, 16, 16, Entity.LIFEPACK_EN));
						
					}else if(currentPixel == 0xFFFFD800) { //Bullets
						Game.entities.add(new Bullets(xx*TILE_SIZE, yy*TILE_SIZE, 16, 16, Entity.BULLETS_EN));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Collision with Tiles Walls 
	public static boolean isFree(int xnext, int ynext) {
		//Determine the tile coordinates for the player's next position
		//Player's x or y + speed as a parameter of this function 
		int EDGE = 1;
		
		int x1 = xnext / TILE_SIZE;  //x top-left player's hitbox
		int y1 = ynext / TILE_SIZE;  //y top-left player's hitbox
		
		int x2 = (xnext + TILE_SIZE - EDGE) / TILE_SIZE; //x top-right player's hitbox
		int y2 = ynext / TILE_SIZE; //y top-right player's hitbox
		
		int x3 = xnext / TILE_SIZE; //x bottom-left player's hitbox
		int y3 = (ynext + TILE_SIZE  - EDGE) / TILE_SIZE; //y bottom-left player's hitbox
		
		int x4 = (xnext + TILE_SIZE - EDGE) / TILE_SIZE; //x bottom-right player's hitbox
		int y4 = (ynext + TILE_SIZE - EDGE) / TILE_SIZE;  //y bottom-right player's hitbox
		
		return !((tiles[x1 + (y1 * World.WIDTH)] instanceof WallTile)||
				(tiles[x2 + (y2 * World.WIDTH)] instanceof WallTile) ||
				(tiles[x3 + (y3 * World.WIDTH)] instanceof WallTile) ||
				(tiles[x4 + (y4 * World.WIDTH)] instanceof WallTile));
	}
	
	//Reset Game Config
	//Clear entities && enemies List / Init Player and World 
	public static void restartGame(String level) {
		Game.entities.clear();
		Game.enemies.clear();
		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Enemy>();
		Game.spritesheet = new Spritesheet("/spritesheet.png");
		Game.player = new Player(0, 0, 16, 16, Game.spritesheet.getSprite(32, 0, 16, 16));
		Game.entities.add(Game.player);
		Game.world = new World("/"+level);
		return;
	}
	
	//Render titles in the Camera ranger
	public void render(Graphics g) {
		//Camera Position
		//Camera.x/y / TILE_SIZE(16)
		int xstart = Camera.x >> 4; 
		int ystart = Camera.y >> 4;  
		
		//Camera x/y final = x/y start + Nº Tiles in the screen dimension (WIDTH/HEIGHT / TILE_SIZE)
		int xfinal = xstart + (Game.WIDTH >> 4); 
		int yfinal = ystart + (Game.HEIGHT >> 4);
		
		for(int xx = xstart; xx <= xfinal; xx++) {
			for(int yy = ystart; yy <= yfinal; yy++) {
				//Case Camera with negative values
				if(xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT) {
					continue;
				}
				Tile tile = tiles[xx + (yy*WIDTH)];
				tile.render(g);
			}
		}
	}
}
