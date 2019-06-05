package Game;

import java.awt.Color;

import Graphics.AnimatedSprite;

public class Grass implements Decoration {
	int posX;
	int posY;

	AnimatedSprite grass;
	int[][][] frames = new int[][][] {{{352}},{{353}},{{354}},{{355}},{{356}},{{357}},{{358}},{{359}},{{360}}};

	public Grass(int x, int y) {
		posX = x;
		posY = y;
		grass = new AnimatedSprite(frames, Color.WHITE, 1, true, 10, false, false);
		grass.setPause(true);
		grass.setFlipped(Helpers.Calc.RandomRange(0, 1) > 0.5);
	}

	public void render() {
		grass.draw(posX,  posY);
	}

	public void update() {
		float x = (float) (GlobalSettings.getScreen().getFrameCount()/50.0) + posX/20;
		float wind = (float) ((Math.sin(x) + Math.sin(2.2*x+5.52) + Math.sin(2.9*x+0.93) + Math.sin(4.6*x+8.94)) / 4.0);
		grass.setFrameNumber((int) Helpers.Calc.map(wind, -1, 1, 0, 8));
	}
}
