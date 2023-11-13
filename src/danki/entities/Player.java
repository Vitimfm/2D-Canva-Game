package danki.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
//import java.util.ArrayList;

//import danki.grafics.Spritesheet;
import danki.main.Game;
import danki.world.Camera;
import danki.world.World;

public class Player extends Entity{
	// VARIABLES
	//Player Directions  
	public boolean right, up, down, left;
	public double speed = 2;
	public int right_direction = 0;
	public int left_direction = 1;
	public int tempDirection;
	
	//Player Frames 
	private int frame = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved = false;
	
	//Damaged Player & Images variables
	private BufferedImage[] rightPlayer, leftPlayer;
	private BufferedImage playerDamage;
	public boolean isDamaged = false;
	private int damageFrames = 0;
	
	//Gun, Ammor, Life 
	public int ammo = 0;
	private boolean hasGun = false;
	public double life = 100, maxLife = 100;

	//Mouse and Shoot 
	public boolean shoot = false, mouseShoot = false;	
	public int mouse_X, mouse_Y;
	
	//Fake Jump Technique 
	public boolean jump = false, isJumping = false;
	public int z = 0;
	public int jumpHEIGHT = 50, jumpCur = 0;
	public int jumpSpeed = 2;
	public boolean jumpUP = false, jumpDOWN = false;
		
	
	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		//List of BufferedImages for sides animations 
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		
		//Player hited sprite 
		playerDamage = Game.spritesheet.getSprite(0, 16, 16, 16);
		
		//Fullfill side animation List
		for(int i = 0; i < 4; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 0, 16, 16);	
		}
		for(int i = 0; i < 4; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 16, 16, 16);	
		}
	}
	
	public void tick() {
		
		//FAKE JUMP TECHNIQUE
		if(jump) {
			if(isJumping == false) {
				jump = false;
				isJumping = true;
				jumpUP = true;
			}
		}
		if(isJumping == true) {
			if(jumpUP){
				jumpCur += jumpSpeed;
			}else if(jumpDOWN) {
				jumpCur -= jumpSpeed;
				if(jumpCur <= 0){
					isJumping = false;
					jumpDOWN = false;
					jumpUP = false;
				}
			}
			z = jumpCur;
			if(jumpCur >= jumpHEIGHT) {
				jumpUP = false;
				jumpDOWN = true;
			}
		}
		
		//MOTION CONFIG
		//Verification using the direction and the next position to set it moving
		moved = false;
		if(right && World.isFree((int)(x+speed),this.getY())) {
			moved = true;
			tempDirection = right_direction;
			x += speed;
		}
		else if (left && World.isFree((int)(x-speed),this.getY())) {
			moved = true;
			tempDirection = left_direction;
			x-= speed;
		}
		if(up && World.isFree(this.getX(), (int)(y-speed))) {
			moved = true;
			y -= speed;
		}
		else if(down && World.isFree(this.getX(), (int)(y+speed))) {
			moved = true;
			y += speed;
		}
		
		//Motion Animation
		if(moved) {
			frame++;
			if(frame == maxFrames) {
				frame = 0;
				index++;
				if(index > maxIndex) {
					index = 0;
				}
			}
		}
		
		//Init Entities Collisions 
		LifePackCollision();
		AmmoCollision();
		GunCollision();
		
		//Player hited Frames animation
		if(isDamaged) {
			this.damageFrames++;
			if(this.damageFrames == 8) {
				this.damageFrames = 0;
				isDamaged = false;
			}
		}
		
		//KEY EVENT Shoot Animation 
		if(shoot) {
			shoot = false;
			if(hasGun && ammo > 0 ) { 
				ammo--;
				
				//Just irection x in this case that shooting is sideways 
				int dx = 0; 
				//Offsets to fix where the bullet is born
				int offsetX = 0;
				int offsetY = 8;
				if(tempDirection == right_direction) {
					offsetX = 15;
					dx = 1; //right -> x increases 
				}else if (tempDirection == left_direction){
					offsetX = -3;
					dx = -1; ///left -> x decreases
				}
			
				BulletShoot bullet = new BulletShoot(this.getX() + offsetX, this.getY() + offsetY, 3, 3, null, dx, 0);
				Game.bullets.add(bullet);
			}
		}
		
		//MOUSE EVENT Shoot animation
		if(mouseShoot) {
			mouseShoot = false;
			if(hasGun && ammo > 0 ) {
				ammo--;
				
				double angle = 0;
				int offsetX = 0;
				int offsetY = 8;
				
				if(tempDirection == right_direction) {
					offsetX = 15;
					angle = Math.atan2(mouse_Y - (this.getY() + offsetY - Camera.y), mouse_X - (this.getX() + offsetX - Camera.x));
				}else if(tempDirection == left_direction){
					offsetX = -3;
					angle = Math.atan2(mouse_Y - (this.getY() + offsetY - Camera.y), mouse_X - (this.getX() + offsetX - Camera.x));
				}
				
				double dx = Math.cos(angle); 
				double dy = Math.sin(angle);
		
				BulletShoot bullet = new BulletShoot(this.getX() + offsetX, this.getY() + offsetY, 3, 3, null, dx, dy);
				Game.bullets.add(bullet);
			}
		}
		
		//Set Game Over State
		if(life <= 0) {
			life = 0;
			Game.gameState = "GAME_OVER";
		}
		
		CameraUpdate();
		
	}
	
	//Function to get Camera x/y during the game tick centralizing on the player
	public void CameraUpdate() {
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH / 2), 0, World.WIDTH * 16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT / 2), 0, World.HEIGHT * 16 - Game.HEIGHT);
	}
	
	//Same logic for every Collision 
	// -> Go through the list and check every instance
	// -> Check if they are collinding 
	// -> Values config
	// -> Destroy Entity
	public void AmmoCollision() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity CurrentEntity = Game.entities.get(i);
			if(CurrentEntity instanceof Bullets) {
				if(Entity.isCollinding(CurrentEntity, this)) {
					ammo += 20;
					if(ammo > 60) {
						ammo = 60;
					}
					Game.entities.remove(CurrentEntity);
				}
			}
		}
	}
	public void LifePackCollision() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity CurrentEntity = Game.entities.get(i);
			if(CurrentEntity instanceof Lifepack) {
				if(Entity.isCollinding(CurrentEntity, this)) {
					life += 30;
					if(life > 100) {
						life = 100;
					}
					Game.entities.remove(CurrentEntity);
				}
			}
		}
	}
	public void GunCollision() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity CurrentEntity = Game.entities.get(i);
			if(CurrentEntity instanceof Weapon) {
				if(Entity.isCollinding(CurrentEntity, this)) {
					hasGun = true; //Player gets the gun
					Game.entities.remove(CurrentEntity);
				}
			}
		}
	}
	
	public void render(Graphics g) {
		if(!isDamaged) { 
			//Player render Motion Animation with and without Gun
			int offsetX = 6;
			int offsetY = 1;
			if(tempDirection == right_direction) {
				g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
				if(hasGun) { //Gun right side
					g.drawImage(Entity.GUN_RIGHT, this.getX() + offsetX - Camera.x, this.getY() + offsetY - Camera.y - z, null);
				}
			}else if(tempDirection == left_direction) {
				g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
				if(hasGun) { //Gun left side
					g.drawImage(Entity.GUN_LEFT, this.getX() - offsetX - Camera.x, this.getY() + offsetY - Camera.y - z, null);
				}
			}
		}else { // Sprite if Player gets hit 
			g.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y - z, null);
		}
		//Shadow when player jumps 
		if(isJumping) {
			g.setColor(Color.black);
			g.fillOval(this.getX() - Camera.x + 5, this.getY() - Camera.y +8 , 8, 8);
		}
	}
}
