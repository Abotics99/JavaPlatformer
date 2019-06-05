package Game;

import java.awt.Color;

import Graphics.AnimatedSprite;

public class CheckPoint {
	int posX;
	int posY;
	Player player;
	AnimatedSprite flag;
	
	int triggerDist = 20;
	
	Sound fanfare;
	
	int[][] flag_1 = new int[][] { { 272 }, { 288 } };
	int[][] flag_2 = new int[][] { { 274 }, { 288 } };
	int[][] flag_3 = new int[][] { { 276 }, { 288 } };
	int[][] flag_4 = new int[][] { { 278 }, { 288 } };
	
	public CheckPoint(int x, int y, Player player) {
		this.posX = x;
		this.posY = y;
		this.player = player;
		flag = new AnimatedSprite(new int[][][] {flag_1,flag_2,flag_3,flag_4}, Color.WHITE, 1, true, 10, false, false);
		flag.setPos(this.posX, this.posY);
		fanfare = new Sound("flagFanfare");
	}
	
	public void update() {
		if(Helpers.Calc.dist(player.getPosX(), player.getPosY(), posX, posY) < triggerDist) {
			if(player.getStartX() != posX && player.getStartY() != posY) {
				player.setRespawn(posX, posY);
				fanfare.play();
				player.heal(10);
			}
		}
	}
	
	public void render() {
		flag.render();
	}
}
