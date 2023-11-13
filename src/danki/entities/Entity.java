package danki.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import danki.main.Game;
import danki.world.Camera;

public class Entity {
	
	//Sprite for each Entity
	public static BufferedImage LIFEPACK_EN = Game.spritesheet.getSprite(6*16, 0, 16, 16);
	public static BufferedImage WEAPON_EN = Game.spritesheet.getSprite(7*16, 0, 16, 16);
	public static BufferedImage BULLETS_EN = Game.spritesheet.getSprite(8*16, 0, 16, 16);
	public static BufferedImage ENEMY_EN = Game.spritesheet.getSprite(6*16, 16, 16, 16);
	public static BufferedImage GUN_LEFT = Game.spritesheet.getSprite(16, 16, 16, 16);
	public static BufferedImage GUN_RIGHT = Game.spritesheet.getSprite(144, 0, 16, 16);
	public static BufferedImage ENEMY_FEEDBACK = Game.spritesheet.getSprite(96, 32, 16, 16);
	private BufferedImage sprite;
	
	//Standarts 
	protected double x, y;
	protected int z;
	protected int width, height;
	
	//Masks values
	private int maskx, masky, mwidth, mheight;
	
	public Entity(int x, int y, int width, int height, BufferedImage sprite) {
		//Pre-set new Entity
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;
		
		this.maskx = 0;
		this.masky = 0;
		this.mwidth = width;
		this.mheight = height;
	}
	
	public void setMask(int maskx, int masky, int mwidth, int mheight) {
		this.maskx = maskx;
		this.masky = masky;
		this.mwidth = mwidth;
		this.mheight = mheight;
	}
	
	// Information/Set Functions 
	public void setX(int  newX) {
		this.x = newX;
	}
	public void setY(int newY) {
		this.y = newY;
	}
	public int getX() {
		return (int)this.x;
	}
	public int getY() {
		return (int)this.y;
	}
	public int getWidth() {
		return this.width;
	}
	public int getHeight() {
		return this.height;
	}
	
	public void tick() {
		
	}
	
	//Same Collision Logic in Enemy Class (but with z axis)
	public static boolean isCollinding(Entity e1, Entity e2){
		Rectangle e1Mask = new Rectangle(e1.getX() + e1.maskx, e1.getY() + e1.masky, e1.mwidth, e1.mheight);
		Rectangle e2Mask = new Rectangle(e2.getX() + e2.maskx, e2.getY() + e2.masky, e2.mwidth, e2.mheight);
		if(e1Mask.intersects(e2Mask) && e1.z == e2.z) {
			return true;
		}
		return false;
	}
	
	public void render(Graphics g) {
		//Render entities with camera correction
		g.drawImage(sprite,this.getX() - Camera.x, this.getY() - Camera.y, null);
	}
} 
