package danki.entities;

//import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import danki.main.Game;
import danki.world.Camera;
//import danki.world.Camera;
import danki.world.World;

public class Enemy extends Entity{
	
	//Standarts 
	private double speed = 1;
	private BufferedImage[] Enemy_sprite;
	private int life = 5;
	
	//Masks
	private int mask_X = 2;
	private int mask_Y = 2;
	private int maskWidth = 14;
	private int maskHeight = 14;
	
	//Frames
	private int frame = 0;
	private int maxFrames = 20;
	private int index = 0;
	private int maxIndex = 3;
	
	//isDamaged
	private boolean isDamaged = false;
	private int damageFrames = 10, damageCurrent = 0;

	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		//Fullfill enemy sprite list
		Enemy_sprite = new BufferedImage[4];
		for(int i = 0; i < 4; i++) {
			Enemy_sprite[i] = Game.spritesheet.getSprite(96 + (i*16), 16, 16, 16);
		}
	}
	
	public void tick() {
		if(isCollindingWithPlayer() == false) {
			// Checks Player position to follow it while checking Collision with walls (Word.isFree)
			// and also checks if they are colliding with each other (isCollinding)
			if((int)x < Game.player.getX() && World.isFree((int)(x+speed), this.getY()) 
					&& !isCollinding((int)(x+speed), this.getY())) {
				x+=speed;
			}else if((int)x > Game.player.getX() && World.isFree((int)(x-speed), this.getY())
					&& !isCollinding((int)(x-speed), this.getY())) {
				x-=speed;
			}
			if((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(y+speed))
					&& !isCollinding(this.getX(), (int)(y+speed))) {
				y+=speed;
			}else if((int)y > Game.player.getY() && World.isFree(this.getX(), (int)(y-speed))
					&& !isCollinding(this.getX(), (int)(y-speed))) {
				y-=speed;
			}
		}else {
			// Collinding with player 
			if(Game.rand.nextInt(100) < 10) {
				Game.player.life --;
				Game.player.isDamaged = true;
			}
		}
		
		//Enemy Frames Animation
		frame++;
		if(frame == maxFrames) {
			frame = 0;
			index++;
			if(index > maxIndex) {
				index = 0;
			}
		}
		
		//Destroy enemies 
		isCollidingWithBullet();
		if(life <= 0) {
			destroySelf();
			return;
		}
		
		//Damaged Enemy Frames 
		if(isDamaged) {
			this.damageCurrent++;
			if(this.damageCurrent == this.damageFrames) {
				this.damageCurrent = 0;
				this.isDamaged = false;
			}
		}
			
	}
	
	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}
	
	//Same Collision Login in Player Class 
	public void isCollidingWithBullet() {
		for(int i = 0; i < Game.bullets.size(); i++) {
			Entity e = Game.bullets.get(i);
			if(e instanceof BulletShoot) {
				// Check if Bullet Shooted is collinding Enemy
				if(Entity.isCollinding(this, e)) {
					isDamaged = true;
					life--;
					Game.bullets.remove(i);
					return;
				}
			}
		}
	}
	
	//Function to check colliding with Player using Collision masks
	public boolean isCollindingWithPlayer(){
		Rectangle CurrentEnemy = new Rectangle(this.getX() + mask_X, this.getY() + mask_Y, maskWidth, maskHeight);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);
		return CurrentEnemy.intersects(player);
	}
	
	//Function to check if the enemies are colliding with each other using Collision masks 
	public boolean isCollinding(int xnext, int ynext) {
		Rectangle CurrentEnemy = new Rectangle(xnext + mask_X, ynext + mask_Y, maskWidth, maskHeight);
		
		for (int i = 0; i < Game.enemies.size(); i++) {
			Enemy e = Game.enemies.get(i);
			if(e == this) { //if its the same enemy checking 
				continue;	
			}
			Rectangle targetEnemy = new Rectangle(e.getX() + mask_X, e.getY() + mask_Y, maskWidth, maskHeight);
			if(CurrentEnemy.intersects(targetEnemy)) {
				return true;
			}
		}
		return false;
	}
	
	public void render(Graphics g) {
		if(!isDamaged) { //render Enemy animation 
			g.drawImage(Enemy_sprite[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		}else { //render Enemy feedback
			g.drawImage(Entity.ENEMY_FEEDBACK, this.getX() - Camera.x, this.getY() - Camera.y, null);
		}	
		// Enable Collision Mask: 
		//g.setColor(Color.blue);
		//g.fillRect(this.getX() + mask_X - Camera.x, this.getY() + mask_Y - Camera.y, maskWidth, maskHeight);
	}

}
