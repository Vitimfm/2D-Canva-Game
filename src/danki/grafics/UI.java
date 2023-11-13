package danki.grafics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import danki.main.Game;

public class UI {
	// LIFE BAR CONFIG
	public void render(Graphics g) {
		//Red Bar behind Green Bar
		g.setColor(Color.red);
		g.fillRect(8,  4, 50, 8);
		
		//Resizeble Green bar 
		g.setColor(Color.green);
		g.fillRect(8, 4, (int)((Game.player.life / Game.player.maxLife) * 50), 8);
		
		//String Life Value on the bars
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 8));
		g.drawString((int)Game.player.life + "/" + (int)Game.player.maxLife,20,  11);
	}
}
