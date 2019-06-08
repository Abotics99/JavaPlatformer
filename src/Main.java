import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Game.Game;
import Game.GlobalSettings;
import Graphics.Screen;
import Graphics.Text;
import kuusisto.tinysound.TinySound;

/*TODO
 * -button.render outta bounds exception protection 
 */

public class Main implements Runnable {

	Screen screen = new Screen();

	// --gameSettings--

	// --gameData--
	long lastUpdateTime = 0;
	long timeSinceLastUpdate = 0;

	// --icon data--
	BufferedImage fontData = null;

	// --other objects--\
	Game game;
	Text fpsCounterText;

	public static void main(String[] args) {
		new Main().start();
	}

	public void run() {
		TinySound.init();
		screen.init();
		GlobalSettings.setScreen(screen);
		init();
		while (GlobalSettings.running) {
			update();
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("bye...");
		TinySound.shutdown();
		System.exit(0);
	}

	public synchronized void start() {
		GlobalSettings.running = true;
		new Thread(this).start();
	}

	public synchronized void stop() {
		GlobalSettings.running = false;
	}

	public void init() {
		try {
			fontData = ImageIO.read(new File(System.getProperty("user.dir"), "/res/Font.png"));
		} catch (IOException e) {
			System.out.println("Could not find fontData image.");
		}
		GlobalSettings.setFontData(fontData);
		// --initialize objects here--
		game = new Game();
		GlobalSettings.setGame(game);
		
		fpsCounterText = new Text(10,170, new Color(255,255,255),1);
		fpsCounterText.isInstant(true);
	}

	public void update() {
		long now = System.nanoTime();
		double nsPerTick = 1;

		nsPerTick = 1000000000 / 60;
		
		timeSinceLastUpdate = now - lastUpdateTime;

		if (timeSinceLastUpdate > nsPerTick) {
			lastUpdateTime = now;
			tick();
			render();
		}
	}

	// -- fun stuff below --

	void render() {
		screen.background(new Color(35,58,71, 255));
		game.render();
		fpsCounterText.render();
		screen.render();
	}

	public void tick() {
		game.update();
		fpsCounterText.setText(Float.toString(1000000000f/timeSinceLastUpdate));
		fpsCounterText.update();
	}
}
