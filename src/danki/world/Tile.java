package danki.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import danki.main.Game;

public class Tile {
	//Sprites Floor and Wall from spritesheet
	public static BufferedImage TILE_FLOOR = Game.spritesheet.getSprite(0, 0, 16, 16);
	public static BufferedImage TILE_WALL = Game.spritesheet.getSprite(16, 0, 16, 16);
	//Static for easy acess and Perform
	
	private BufferedImage sprite;
	private int x, y;
	
	public Tile(int x, int y, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.sprite = sprite;
	}
	
	public void render(Graphics g) {
		g.drawImage(sprite, x - Camera.x, y - Camera.y, null);
		// - Camera x/y because the camera is centralizing on the player
		// But the Camera actually start at (0,0) or top-left
		// so then, it needs this offset to fix the renderization 
		
	}
	
}
