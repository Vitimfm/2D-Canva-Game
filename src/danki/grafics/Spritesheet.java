package danki.grafics;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Spritesheet {
	
	private BufferedImage spritesheet;
	
	public Spritesheet(String path) {
		try { //Read spritesheet
			spritesheet = ImageIO.read(getClass().getResource(path));
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	//Function to get Sprite in the spritesheet
	public BufferedImage getSprite(int x, int y, int width, int height) {
		return spritesheet.getSubimage(x, y, width, height);
	}
}
