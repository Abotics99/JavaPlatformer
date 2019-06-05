package Game;

import java.awt.Color;

import Graphics.AnimatedSprite;
import Graphics.Sprite;

public class Bat implements Enemy, Transform, Hazard {
	double seed = Helpers.Calc.RandomRange(0.9, 1.1);
	double posX = 0;
	double posY = 0;
	double velX = 0;
	double velY = 0;
	double maxSpeed = 1;
	double acceleration = 0.05 * (seed/2);
	double damping = 0.97;
	Transform target;
	AnimatedSprite bat;
	int iFrame = 0;
	double triggerDist = 100;

	int health = 100;
	int damage = 1;

	boolean dead = false;
	boolean idle = true;
	boolean falling = false;
	boolean walled = true;

	int[][] flap_0 = new int[][] { { 264 } };
	int[][] flap_1 = new int[][] { { 265 } };
	int[][] flap_2 = new int[][] { { 266 } };
	int[][] flap_3 = new int[][] { { 267 } };
	int[][] idle_0 = new int[][] { { 268 } };
	int[][] idle_1 = new int[][] { { 269 } };
	int[][] idle_2 = new int[][] { { 270 } };
	int[][] dead_0 = new int[][] { { 271 } };
	int[][][] flappingAnim = new int[][][] { flap_0, flap_1, flap_2, flap_3 };
	int[][][] deadAnim = new int[][][] { dead_0 };
	int[][][] idleAnim = new int[][][] { idle_0, idle_1, idle_2 };
	
	Sound hit;

	public Bat(int x, int y, Transform target, int health, int damage, int triggerDistance) {
		this.posX = x;
		this.posY = y;
		this.target = target;
		this.health = health;
		this.damage = damage;
		this.triggerDist = triggerDistance;
		bat = new AnimatedSprite(idleAnim, Color.WHITE, 1, true, 5, false, false);
		bat.setPause(true);
		hit = new Sound("HardHit");
	}

	public void render() {
		bat.render();
	}

	public void wakeUp() {
		if (idle && !falling) {
			falling = true;
			bat.setPause(false);
			bat.setLooping(false);
			bat.setUpdatesPerFrame(6);
		}
	}

	public void update() {
		if (!dead) {
			double tempX = target.getPosX() - posX;
			double tempY = (target.getPosY() + 10) - posY;
			double dist = Helpers.Calc.dist(posX, posY, target.getPosX(), target.getPosY() + 10);
			if (idle) {
				if (dist < triggerDist && tempY > 0) {
					wakeUp();
				}
				if (falling) {
					velY += 0.1;
					posY += velY;
					if (bat.isPaused()) {
						bat.setFrames(flappingAnim);
						bat.setLooping(true);
						bat.setUpdatesPerFrame(5);
						bat.setPause(false);
						idle = false;
					}
				}
			} else {
				velX += acceleration * (tempX / dist) * Helpers.Calc.RandomRange(-0.1, 1.2);
				if(walled) {
					if(posY > target.getPosY()) {
						velY += (acceleration * (tempY / dist) * (Math.sin((GlobalSettings.getScreen().getFrameCount()/5.0) + (seed*10))+0.5) * 2)-acceleration;
					}else {
						velY += (acceleration * (tempY / dist) * (Math.sin((GlobalSettings.getScreen().getFrameCount()/5.0) + (seed*10))+0.5) * 2)+acceleration;
					}
				}else {
					velY += acceleration * (tempY / dist) * (Math.sin((GlobalSettings.getScreen().getFrameCount()/5.0) + (seed*10))+0.5) * 2;
				}
				velX *= damping;
				velY *= damping;
				walled = false;
				updateCollisions(GlobalSettings.getGame().getCurrentWorld(), 200, -5, -5, 15, 15);
				posX += velX;
				posY += velY;
			}
		} else {
			velY += 0.1;
			velX *= damping;
			updateCollisions(GlobalSettings.getGame().getCurrentWorld(), 200, -4, -2, 14, 12);
			posX += velX;
			posY += velY;
		}
		bat.setPos((int) posX, (int) posY);
		bat.setFlashing(iFrame > 0);
		iFrame--;
	}

	public void updateCollisions(Sprite spr, int colIndex, int xMin, int yMin, int xMax, int yMax) {
		if (velY > 0 && checkCol((int) posX + xMin, (int) posY + yMax + 1, (int) posX + xMax, (int) posY + yMax + 1,
				spr, colIndex)) {
			velY = 0;
		}
		if (velX < 0 && checkCol((int) posX + xMin - 1, (int) posY + yMin, (int) posX + xMin - 1, (int) posY + yMax,
				spr, colIndex)) {
			velX = 0;
			walled = true;
		}
		if (velX > 0 && checkCol((int) posX + xMax + 1, (int) posY + yMin, (int) posX + xMax + 1, (int) posY + yMax,
				spr, colIndex)) {
			velX = 0;
			walled = true;
		}
		if (velY < 0 && checkCol((int) posX + xMin, (int) posY + yMin - 1, (int) posX + xMax, (int) posY + yMin - 1,
				spr, colIndex)) {
			velY = 0;
		}
	}

	boolean checkCol(int x, int y, Sprite spr, int colIndex) {
		return spr.getTile(x, y) == colIndex;
	}

	boolean checkCol(int x1, int y1, int x2, int y2, Sprite spr, int colIndex) {
		double steps = Helpers.Calc.dist(x1, y1, x2, y2) / 10;
		for (int i = 0; i <= steps; i++) {
			if (spr.getTile((int) Helpers.Calc.lerp(x1, x2, i / steps),
					(int) Helpers.Calc.lerp(y1, y2, i / steps)) == colIndex) {
				return true;
			}
		}
		return false;
	}

	public void setTarget(Transform target) {
		this.target = target;
	}

	public double getPosX() {
		return posY;
	}

	public double getPosY() {
		return posX;
	}

	public double getWidth() {
		return bat.getWidth();
	}

	public double getHeight() {
		return bat.getHeight();
	}

	public void damage(int damage) {
		health -= damage;
		if (health <= 0) {
			dead = true;
			velX = 0;
			velY = 0;
		}
	}

	public void damage(int damage, Transform transform) {
		if (iFrame <= 0 && !dead) {
			health -= damage;
			hit.play();
			iFrame = 10;
			if (!idle) {
				velX = transform.getVelX() / 4;
				velY = (transform.getVelY() / 4);
			}
			wakeUp();
			GlobalSettings.getGame().addFreezeFrames(5);
			GlobalSettings.getScreen().shake(2, 2);
		}
		if (health <= 0 && !dead) {
			dead = true;
			iFrame = 0;
			velX = transform.getVelX() / 4;
			velY = (transform.getVelY() / 4) - 2;
			bat.setFrames(new int[][][] { dead_0 });
			bat.setLooping(false);
		}
	}

	public double getHealth() {
		return health;
	}

	public boolean colliding(double x, double y) {
		return x >= posX && x <= posX + getWidth() && y >= posY && y <= posY + getHeight();
	}

	public double getVelX() {
		return velX;
	}

	public double getVelY() {
		return velY;
	}

	public int getDamage() {
		return damage;
	}

	public boolean isHit(double x, double y) {
		return x >= posX && x <= posX + getWidth() && y >= posY && y <= posY + getHeight() && !dead;
	}

	public Hazard getHazard() {
		return this;
	}
}
