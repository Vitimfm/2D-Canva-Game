 package danki.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Menu {
	
	//Menu Options
	public String[] options = {"New Game", "Load Game", "Exit"};
	public int currentOption = 0;
	public int maxOption = options.length - 1;
	
	//Menu Navigation
	public boolean up, down, enter;
	public boolean pause = false;
	
	public void tick() {
		//Menu Navigation Loop
		if(up) {
			up = false;
			currentOption--;
			if(currentOption < 0) {
				currentOption = maxOption;
			}
		}
		if(down) {
			down = false;
			currentOption++;
			if(currentOption > maxOption) {
				currentOption = 0;
			}
		}
		
		//Confirmed Options 
		if(enter) {
			enter = false;
			if(options[currentOption] == "New Game" || options[currentOption] == "Continue") {
				Game.gameState = "NORMAL";
				pause = false;
			}else if(options[currentOption] == "Exit") {
				System.exit(1);
			}
		}
	}
	
	public void render(Graphics g) {
		//BACKGROUND
		g.setColor(Color.black);
		g.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);
		//TITLE
		g.setColor(Color.red);
		g.setFont(new Font("arial", Font.BOLD, 40));
		g.drawString("SEFIAM KILLER", (Game.WIDTH * Game.SCALE) / 2 - 150, (Game.HEIGHT * Game.SCALE) / 2 - 140);
		
		//OPTIONS
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 24));
		
		if(pause == false) { //Menu change in diferents Game States (Check Key Events)
			g.drawString("New Game", (Game.WIDTH * Game.SCALE) / 2 - 70, (Game.HEIGHT * Game.SCALE) / 2 - 40);
		}else {
			g.drawString("Resume", (Game.WIDTH * Game.SCALE) / 2 - 60, (Game.HEIGHT * Game.SCALE) / 2 - 40);
		}
		g.drawString("Load Game", (Game.WIDTH * Game.SCALE) / 2 - 75, (Game.HEIGHT * Game.SCALE) / 2);
		g.drawString("Exit", (Game.WIDTH * Game.SCALE) / 2 - 30, (Game.HEIGHT * Game.SCALE) / 2 + 40);
		
		// Arrow Menu Navigation
		if(options[currentOption] == "New Game") {
			g.drawString(">", (Game.WIDTH * Game.SCALE) / 2 - 90, (Game.HEIGHT * Game.SCALE) / 2 - 40);
		}else if(options[currentOption] == "Load Game") {
			g.drawString(">", (Game.WIDTH * Game.SCALE) / 2 - 95, (Game.HEIGHT * Game.SCALE) / 2);
		}else {
			g.drawString(">", (Game.WIDTH * Game.SCALE) / 2 - 50, (Game.HEIGHT * Game.SCALE) / 2 + 40);
		}
	}
}
