package Game;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Graphics.Screen;

public class GlobalSettings {
	private static Screen screen;
	private static BufferedImage fontData;
	
	private static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	
	private static Game game;
	
	public static boolean running = false;
	
	public static Game getGame() {
		return game;
	}

	public static void setGame(Game game) {
		GlobalSettings.game = game;
	}

	public static BufferedImage getFontData() {
		return fontData;
	}

	public static void setFontData(BufferedImage fontData) {
		GlobalSettings.fontData = fontData;
	}

	public static Screen getScreen() {
		return screen;
	}
	
	public static void setScreen(Screen screen) {
		GlobalSettings.screen = screen;
	}
	
	public static void removeEnemy(int index) {
		enemies.remove(index);
	}
	
	public static void removeEnemy(Enemy enemy) {
		enemies.remove(enemy);
	}
	
	public static void addEnemy(Enemy enemy) {
		enemies.add(enemy);
	}
	
	public static ArrayList<Enemy> getEnemies(){
		return enemies;
	}
}
