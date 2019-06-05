package Game;

import java.awt.Color;

import Graphics.AnimatedSprite;
import Graphics.Sprite;

public class BoneHound implements Enemy, Transform, Hazard {
	double posX;
	double posY;
	double velX;
	double velY;
	int triggerDist = 20;
	boolean idle = true;
	boolean grounded = false;
	int health = 100;
	int damage = 1;
	double gravity = 0.1;
	boolean dead = false;
	double acceleration = 0.1;
	double damping = 0.95;
	int iFrame;

	Transform target;
	AnimatedSprite spr;

	int[][] run_0 = new int[][] { { 289, 290 }, { 305, 306 } };
	int[][] run_1 = new int[][] { { 291, 292 }, { 307, 308 } };
	int[][] run_2 = new int[][] { { 293, 294 }, { 309, 310 } };
	int[][] run_3 = new int[][] { { 295, 296 }, { 311, 312 } };
	int[][] jump_1 = new int[][] { { 297, 298 }, { 313, 314 } };
	int[][] idle_1 = new int[][] { { 299, 300 }, { 315, 316 } };
	int[][] dead_1 = new int[][] { { 301, 302 }, { 317, 318 } };

	int[][][] runAnim = new int[][][] { run_0, run_1, run_2, run_3 };
	int[][][] jumpAnim = new int[][][] { jump_1 };
	int[][][] idleAnim = new int[][][] { idle_1 };
	
	Sound scream;
	Sound hit;
	Sound jump;

	public BoneHound(int x, int y, Transform target, int triggerDistance, int health, int damage) {
		posX = x;
		posY = y;
		this.target = target;
		this.health = health;
		this.damage = damage;
		this.triggerDist = triggerDistance;
		spr = new AnimatedSprite(runAnim, Color.WHITE, 1, true, 5, false, false);
		scream = new Sound("deathScream_1");
		hit = new Sound("HardHit");
		jump = new Sound("landing");
	}

	public void render() {
		spr.draw((int) posX, (int) posY+1);
	}

	public void update() {
		// TODO Auto-generated method stub
		velY += gravity;

		if (idle && Helpers.Calc.dist(posX + 10, posY + 10, target.getPosX() + 5,
				target.getPosY() + 10) < triggerDist) {
			idle = false;
		}

		if (!idle && !dead) {
			if (target.getPosX() - posX > 0) {
				velX += acceleration;
			} else {
				velX += -acceleration;
			}
			double dist = Helpers.Calc.dist(posX + 10, posY + 10, target.getPosX() + 5, target.getPosY() + 10);
			if (dist < 50 && dist > 40) {
				if (grounded) {
					velY = -2;
					scream.play();
				}
			}
			
			if(dist > 150) {
				idle = true;
			}
		}

		velX *= damping;
		updateCollisions(GlobalSettings.getGame().getCurrentWorld(), 200, -5, -5, 25, 25);
		posX += velX;
		posY += velY;

		if (!dead) {
			if (Math.abs(velX) > 0.1) {
				if (!spr.getFrames().equals(runAnim)) {
					spr.setFrames(runAnim);
				}
				spr.setFlipped(velX > 0);
			} else {
				spr.setFrames(idleAnim);
			}
			if (!grounded) {
				spr.setFrames(jumpAnim);
			}

			spr.setFlashing(iFrame > 0);
			iFrame--;
		}
	}

	public void updateCollisions(Sprite spr, int colIndex, int xMin, int yMin, int xMax, int yMax) {
		if (velY > 0 && checkCol((int) posX + xMin, (int) posY + yMax + 1, (int) posX + xMax, (int) posY + yMax + 1,
				spr, colIndex)) {
			velY = 0;
			posY = ((int) (posY / 10) * 10) + 4;
			grounded = true;
		} else {
			grounded = false;
		}
		if (velX < 0 && checkCol((int) posX + xMin - 1, (int) posY + yMin, (int) posX + xMin - 1, (int) posY + yMax,
				spr, colIndex)) {
			velX = 0;
			if (grounded) {
				velY = -2;
				jump.play();
			}
		}
		if (velX > 0 && checkCol((int) posX + xMax + 1, (int) posY + yMin, (int) posX + xMax + 1, (int) posY + yMax,
				spr, colIndex)) {
			velX = 0;
			if (grounded) {
				velY = -2;
				jump.play();
			}
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

	public int getDamage() {
		// TODO Auto-generated method stub
		return damage;
	}

	public boolean isHit(double x, double y) {
		return x > posX && x < posX + getWidth() && y > posY && y < posY + getHeight() && !dead;
	}

	public double getWidth() {
		// TODO Auto-generated method stub
		return spr.getWidth();
	}

	public double getHeight() {
		// TODO Auto-generated method stub
		return spr.getHeight();
	}

	public double getPosX() {
		// TODO Auto-generated method stub
		return posX;
	}

	public double getPosY() {
		// TODO Auto-generated method stub
		return posY;
	}

	public double getVelX() {
		// TODO Auto-generated method stub
		return velX / 5;
	}

	public double getVelY() {
		// TODO Auto-generated method stub
		return velY / 5;
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
			iFrame = 50;
			if (!idle) {
				velX = transform.getVelX() / 4;
				velY = (transform.getVelY() / 4);
			}
			idle = false;
			GlobalSettings.getGame().addFreezeFrames(5);
			GlobalSettings.getScreen().shake(2, 2);
		}
		if (health <= 0 && !dead) {
			dead = true;
			iFrame = 0;
			velX = transform.getVelX() / 4;
			velY = (transform.getVelY() / 4) - 2;
			spr.setFrames(new int[][][] { dead_1 });
			spr.setLooping(false);
		}
	}

	public double getHealth() {
		// TODO Auto-generated method stub
		return health;
	}

	public boolean colliding(double x, double y) {
		return x > posX && x < posX + getWidth() && y > posY && y < posY + getHeight();
	}

	public Hazard getHazard() {
		// TODO Auto-generated method stub
		return this;
	}

}
