package Game;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Graphics.Button;
import Graphics.Fader;
import Graphics.Screen;
import Graphics.Sprite;
import Helpers.WorldLoader;

public class Game {

	BufferedImage fontData;
	Screen screen;

	Button play;
	Button exit;
	Button restart;
	Sprite title;
	Sprite deathScreen;
	Sprite world;

	double titleBob = 0;

	int frameCounter = 0;

	boolean paused = false;

	boolean movingPlayer = false;
	Player player;

	int startX = 201;
	int startY = 380;
	int playerNextPosX = startX;
	int playerNextPosY = startY;

	int freezeFrame = 0;

	Fader fader;

	boolean pauseMenu = false;
	
	Music mainTheme;
	ArrayList<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
	ArrayList<Decoration> decorations = new ArrayList<Decoration>();
	
	WorldLoader worldData;
	
	double camOffsetX = 0;
	double camOffsetY = -10;

	public Game() {
		this.fontData = GlobalSettings.getFontData();
		this.screen = GlobalSettings.getScreen();
		play = new Button(5, screen.getPixelHeight() - 85, 40, 40, new Color(30, 121, 141), 16);
		exit = new Button(5, screen.getPixelHeight() - 40, 40, 35, new Color(30, 121, 141), 232);
		restart = new Button(50, screen.getPixelHeight() - 40, 40, 35, new Color(30, 121, 141), 279);
		title = new Sprite(new int[][] { { 233, 234, 235, 236, 237, 238, 239 }, { 249, 250, 251, 252, 253, 254, 255 } },
				Color.WHITE, 3);
		deathScreen = new Sprite(new int[][] { { 320, 321, 322, 323, 324}, { 336, 337, 338, 339, 340} }, Color.WHITE, 3);
		worldData = new WorldLoader("World_1");
		world = new Sprite(worldData.getLayer(), Color.WHITE, 1);
		player = new Player(startX, startY, world);
		setCamera(player.getPosX() + camOffsetX, player.getPosY() + camOffsetY);
		initObjects();
		fader = new Fader(10, new Color(35, 58, 71, 255));
		pauseMenu = true;
		fader.fadeInstant();
		mainTheme = new Music("MainSong");
	}
	
	void initObjects() {
		for(Object obj: worldData.getObjects()) {
			if(obj.getId() == 269) {
				addBat(obj.getPosX(), obj.getPosY()-10);
			}
			if(obj.getId() == 318) {
				addHound(obj.getPosX()-10, obj.getPosY()-20);
			}
			if(obj.getId() == 273) {
				checkPoints.add(new CheckPoint(obj.getPosX(), obj.getPosY()-20, player));
			}
			
			if(obj.getId() == 357) {
				decorations.add(new Grass(obj.getPosX(), obj.getPosY()-10));
			}
		}
	}
	
	void addBat(int x, int y) {
		GlobalSettings.addEnemy(new Bat(x, y, player, 100, 1, 70));
	}
	
	void addHound(int x, int y) {
		GlobalSettings.addEnemy(new BoneHound(x, y, player, 50, 200, 2));
	}

	public void render() {
		world.draw(0, 0);
		player.render();
		renderEnemies();
		renderCheckpoints();
		renderDecorations();
		fader.render();
		if (pauseMenu && fader.isHidden()) {
			play.render();
			exit.render();
			restart.render();
			title.draw((int) (25 + GlobalSettings.getScreen().getCamX()),
					(int) (titleBob + 20 + GlobalSettings.getScreen().getCamY()));
		}
		if(player.isDead() && fader.isHidden()) {
			exit.render();
			restart.render();
			deathScreen.draw((int) (25 + GlobalSettings.getScreen().getCamX()),
					(int) (titleBob + 20 + GlobalSettings.getScreen().getCamY()));
		}
	}

	public void update() {
		if (freezeFrame-- <= 0) {
			if (!paused && fader.isTotallyVisible()) {
				updateCameraOffset();
				lerpCamera(player.getPosX() + camOffsetX, player.getPosY() + camOffsetY, 0.8);
				titleBob = Math.sin(frameCounter * 0.05) * 5;
				if (screen.escapePressed() && !player.isDead()) {
					pauseMenu = true;
					fader.fade();
				}
				if (screen.rightPressed()) {
					player.moveRight();
				}
				if (screen.leftPressed()) {
					player.moveLeft();
				}
				player.jump(screen.jumpPressed());
				if (screen.firePressed()) {
					int x = 0;
					int y = 0;
					if (screen.leftPressed())
						x--;
					if (screen.rightPressed())
						x++;
					if (screen.downPressed())
						y = 1;
					if (screen.upPressed())
						y = -1;
					player.shoot(true, x, y);
				} else {
					player.shoot(false, 0, 0);
				}
				player.update();
				updateEnemies();
				updateCheckpoints();
				updateDecorations();
				if(player.isDead() && Math.abs(player.playerVelX)<0.1) {
					fader.fade();
				}
			}
			frameCounter++;
		}

		if (fader.isHidden() && movingPlayer) {
			player.setPos(playerNextPosX, playerNextPosY);
			setCamera(player.getPosX(), player.getPosY());
			fader.show();
			movingPlayer = false;
		}

		if (pauseMenu && fader.isHidden()) {
			if (play.isPressed() || screen.escapePressed()) {
				fader.show();
				pauseMenu = false;
			}
			if (exit.isPressed()) {
				GlobalSettings.running = false;
			}
			if (restart.isPressed()) {
				pauseMenu = false;
				respawnPlayer(0);
			}
		}
		
		if(player.isDead() && fader.isHidden()) {
			if (restart.isPressed()) {
				pauseMenu = false;
				player.setRespawn(startX, startY);
				player.setDead(false);
				respawnPlayer(0);
			}
			if (exit.isPressed()) {
				GlobalSettings.running = false;
			}
		}
		
		if(fader.isTotallyVisible()) {
			if(!mainTheme.isPlaying()) {
				mainTheme.play();
			}
		}else {
			if(mainTheme.isPlaying()) {
				mainTheme.stop();
			}
		}
	}
	
	private void updateCameraOffset() {
		if(player.getVelX()>=0.1 && camOffsetX < 30) {
			camOffsetX+= 1;
		}
		if(player.getVelX()<=-0.1 && camOffsetX > -30) {
			camOffsetX+= -1;
		}
		if(Math.abs(player.getVelX()) < 0.1) {
			camOffsetX*=0.95;
		}
	}

	public void setCamera(double x, double y) {
		screen.setCameraPos(x - screen.getPixelWidth() / 2, y - screen.getPixelHeight() / 2);
	}

	public void lerpCamera(double x, double y, double speed) {
		screen.setCameraPos(Helpers.Calc.lerp(x - screen.getPixelWidth() / 2, screen.getCamX(), speed),
				Helpers.Calc.lerp(y - screen.getPixelHeight() / 2, screen.getCamY(), speed));
	}

	public void movePlayer(int x, int y) {
		playerNextPosX = x;
		playerNextPosY = y;
		movingPlayer = true;
		fader.fade();
	}

	private void updateEnemies() {
		for (Enemy enemy : GlobalSettings.getEnemies()) {
			enemy.update();
		}
	}

	private void renderEnemies() {
		for (Enemy enemy : GlobalSettings.getEnemies()) {
			enemy.render();
		}
	}
	
	private void updateCheckpoints() {
		for (CheckPoint cp : checkPoints) {
			cp.update();
		}
	}

	private void renderCheckpoints() {
		for (CheckPoint cp : checkPoints) {
			cp.render();
		}
	}
	
	private void updateDecorations() {
		for (Decoration decor : decorations) {
			decor.update();
		}
	}

	private void renderDecorations() {
		for (Decoration decor : decorations) {
			decor.render();
		}
	}

	public void addFreezeFrames(int freezeFrame) {
		this.freezeFrame = freezeFrame;
	}

	public Sprite getCurrentWorld() {
		return world;
	}

	public void respawnPlayer(int damage) {
		if (damage > 0) {
			player.damage(damage);
		}
		movePlayer(player.getStartX(), player.getStartY());
	}
}
