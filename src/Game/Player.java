package Game;

import java.awt.Color;

import Graphics.AnimatedSprite;
import Graphics.Sprite;
import Graphics.Text;

public class Player implements Transform {
	double playerX;
	double playerY;
	Sprite player;
	double playerVelY = 0;
	double playerVelX = 0;
	double maxGravity = 0.9;
	double minGravity = 0.2;
	double gravity;
	double groundedAcceleration = 0.5;
	double airAcceleration = 0.2;
	double acceleration;
	double groundedDampening = 0.750;
	double airDampening = 0.95;
	double dampening;
	double maxSpeed = 2;
	double jumpHeight = 4.5;
	double terminalVelocity = 10;
	boolean grounded = false;
	boolean shootHop = true;
	int animationTimer = 0;
	boolean canJump = false;
	Projectile projectile;
	boolean canShoot = false;
	Sprite world;
	AnimatedSprite spark;
	AnimatedSprite dustPlume;
	int maxHealth = 6;
	int health = 6;
	int iFrames = 0;
	Text healthBar;
	Sound step;
	Sound jump_sound;
	Sound impact;
	int startX;
	int startY;
	boolean dead = false;
	Sound death;

	// animFrames
	int[][] idle_1 = new int[][] { { 179 }, { 195 } };
	int[][] step_1 = new int[][] { { 180 }, { 196 } };
	int[][] step_2 = new int[][] { { 181 }, { 197 } };
	int[][] dead_1 = new int[][] { { 303 }, { 319 } };
	int[][] sprk_1 = new int[][] { { 186 } };
	int[][] sprk_2 = new int[][] { { 187 } };
	int[][] sprk_3 = new int[][] { { 188 } };
	int[][] dust_0 = new int[][] { { 256, 257 } };
	int[][] dust_1 = new int[][] { { 258, 259 } };
	int[][] dust_2 = new int[][] { { 260, 261 } };
	int[][] dust_3 = new int[][] { { 262, 263 } };
	int[][] headlessIdle_1 = new int[][] { { 329 }, { 345 } };
	int[][] headlessStep_1 = new int[][] { { 330 }, { 346 } };
	int[][] headlessStep_2 = new int[][] { { 331 }, { 347 } };
	
	int[][][] runCycle =  new int[][][] {idle_1,step_1,idle_1,step_2};

	public Player(int x, int y, Sprite world) {
		player = new Sprite(step_1, Color.WHITE, 1);
		projectile = new Projectile(world, this);
		this.world = world;
		playerX = x;
		playerY = y;
		startX = x;
		startY = y;
		spark = new AnimatedSprite(new int[][][] { sprk_1, sprk_2, sprk_3 }, Color.WHITE, 1, false, 5, true, true);
		dustPlume = new AnimatedSprite(new int[][][] { dust_0, dust_1, dust_2, dust_3 }, Color.WHITE, 1, false, 4, true,
				true);
		healthBar = new Text(10, 10, Color.RED, 1);
		healthBar.isInstant(true);
		setHealthBar(health);
		step = new Sound("step1");
		jump_sound = new Sound("landing");
		impact = new Sound("playerImpact");
		death = new Sound("death");
	}

	public void setHealthBar(int health) {
		String temp = "";
		double healthTmp = health;
		for (int i = 0; i < maxHealth / 2; i++) {
			if (healthTmp > 1) {
				temp += (char) 182;
			} else if (healthTmp > 0) {
				temp += (char) 183;
			} else {
				temp += (char) 184;
			}
			healthTmp -= 2;
		}
		healthBar.setText(temp);
	}

	public void render() {
		player.draw((int) playerX, (int) playerY);
		projectile.render();
		spark.render();
		dustPlume.render();
		healthBar.render();
	}

	public void update() {
		playerVelY += gravity;
		updateCollisions(world, 200);
		playerVelX = Helpers.Calc.clamp(playerVelX, -terminalVelocity, terminalVelocity);
		playerVelY = Helpers.Calc.clamp(playerVelY, -terminalVelocity, terminalVelocity);
		playerY += playerVelY;
		playerX += playerVelX;
		if (grounded) {
			dampening = groundedDampening;
			acceleration = groundedAcceleration;
		} else {
			dampening = airDampening;
			acceleration = airAcceleration;
		}
		playerVelX *= dampening;
		updateAnimations();
		projectile.update();
		if(!projectile.isHidden()) {
			runCycle =  new int[][][] {headlessIdle_1,headlessStep_1,headlessIdle_1,headlessStep_2};
		}else {
			runCycle =  new int[][][] {idle_1,step_1,idle_1,step_2};
		}
		checkHazards();
		healthBar.update();
		if (iFrames > 0) {
			iFrames--;
			player.setFlashing(true);
		} else {
			player.setFlashing(false);
		}
	}

	private void updateAnimations() {
		if (playerVelX > 0) {
			player.setFlipped(false);
		}
		if (playerVelX < 0) {
			player.setFlipped(true);
		}
		if (animationTimer == 0) {
			player.setSpriteTiles(runCycle[0]);
		}
		if (animationTimer == 4) {
			player.setSpriteTiles(runCycle[1]);
			if (grounded) {
				step.play();
			}
		}
		if (animationTimer == 12) {
			player.setSpriteTiles(runCycle[2]);
		}
		if (animationTimer == 16) {
			player.setSpriteTiles(runCycle[3]);
			if (grounded) {
				step.play();
			}
		}

		if (!grounded) {
			animationTimer = 16;
		} else if (Math.abs(playerVelX) < 0.1) {
			animationTimer = 0;
		} else if (Math.abs(playerVelX) > 0.1) {
			animationTimer++;
		}
		if (animationTimer == 28)
			animationTimer = 0;
		
		if(dead) {
			player.setSpriteTiles(dead_1);
		}
	}

	public void updateCollisions(Sprite spr, int colIndex) {
		if (checkCol((int) playerX - 3, (int) playerY + 20, spr, colIndex)
				|| checkCol((int) playerX + 13, (int) playerY + 20, spr, colIndex)
				|| checkCol((int) playerX + 4, (int) playerY + 20, spr, colIndex)) {
			playerVelY = 0;
			playerY = ((int) (playerY / 10) * 10) + ((((int) world.getPosY() % 10) + 10) % 10);
			if (!grounded) {
				dustPlume.setPos((int) playerX - 5, (int) playerY + 6);
				dustPlume.restartAnim();
			}
			grounded = true;
			shootHop = true;
		} else {
			grounded = false;
		}
		if (playerVelX < 0 && (checkCol((int) playerX - 5, (int) playerY + 19, spr, colIndex)
				|| checkCol((int) playerX - 5, (int) playerY + 1, spr, colIndex)
				|| checkCol((int) playerX - 5, (int) playerY + 10, spr, colIndex))) {
			playerVelX = 0;
		}
		if (playerVelX > 0 && (checkCol((int) playerX + 15, (int) playerY + 19, spr, colIndex)
				|| checkCol((int) playerX + 15, (int) playerY + 1, spr, colIndex)
				|| checkCol((int) playerX + 15, (int) playerY + 10, spr, colIndex))) {
			playerVelX = 0;
		}
		if (playerVelY < 0 && (checkCol((int) playerX - 3, (int) playerY, spr, colIndex)
				|| checkCol((int) playerX + 13, (int) playerY, spr, colIndex)
				|| checkCol((int) playerX + 4, (int) playerY, spr, colIndex))) {
			playerVelY = 0;
			playerY = ((int) (playerY / 10) * 10) + 10;
		}
		if (checkCol((int) playerX, (int) playerY, spr, 231)) {
			GlobalSettings.getGame().respawnPlayer(1);
		}
	}

	boolean checkCol(int x, int y, Sprite spr, int colIndex) {
		return spr.getTile(x, y) == colIndex;
	}

	public void checkHazards() {
		for (Enemy enemy : GlobalSettings.getEnemies()) {
			if (enemy.getHazard().isHit(playerX + 5, playerY + 10)) {
				damage(enemy.getHazard().getDamage(), enemy);
			}
		}
	}

	public void setPos(int x, int y) {
		playerX = x;
		playerY = y;
		playerVelX = 0;
		playerVelY = 0;
	}

	public void moveLeft() {
		if (!dead) {
			if (playerVelX > -0.01 && grounded) {
				spark.setPos((int) playerX, (int) playerY + 6);
				spark.restartAnim();
				spark.setFlipped(false);
			}
			playerVelX += -acceleration;
			if (playerVelX < -maxSpeed)
				playerVelX = -maxSpeed;
		}
	}

	public void moveRight() {
		if (!dead) {
			if (playerVelX < 0.01 && grounded) {
				spark.setPos((int) playerX, (int) playerY + 6);
				spark.restartAnim();
				spark.setFlipped(true);
			}
			playerVelX += acceleration;
			if (playerVelX > maxSpeed)
				playerVelX = maxSpeed;
		}
	}

	public void jump(boolean jump) {
		if (!dead) {
			if (jump) {
				if (grounded && canJump) {
					playerVelY = -jumpHeight;
					playerY--;
					dustPlume.setPos((int) playerX - 5, (int) playerY + 6);
					dustPlume.restartAnim();
					jump_sound.play();
					canJump = false;
				}
				gravity = minGravity;
			} else {
				canJump = true;
				if (playerVelY < 0) {
					gravity = maxGravity;
				} else {
					gravity = minGravity;
				}
			}
		}
	}

	public void shoot(boolean shoot, double x, double y) {
		if (!dead) {
			if (shoot) {
				if (canShoot) {
					if (x == 0 & y == 0) {
						if (player.isFlipped()) {
							projectile.shoot(playerX, playerY, -1, 0);
						} else {
							projectile.shoot(playerX, playerY, 1, 0);
						}
					} else {
						if (projectile.isHidden() && shootHop) {
							playerVelY = -y * 4;
							shootHop = false;
						}
						projectile.shoot(playerX, playerY, x, y);

					}
					canShoot = false;
				}
			} else {
				canShoot = true;
			}
		}
	}

	public double getPosX() {
		return playerX;
	}

	public double getPosY() {
		return playerY;
	}

	public double getWidth() {
		return player.getWidth();
	}

	public double getHeight() {
		return player.getHeight();
	}

	public double getVelX() {
		return playerVelX;
	}

	public double getVelY() {
		return playerVelY;
	}

	public void damage(int damage, Transform transform) {
		if (iFrames <= 0 && !dead) {
			impact.play();
			health -= damage;
			setHealthBar(health);
			iFrames = 100;
			GlobalSettings.getScreen().shake(4, 4);
			GlobalSettings.getGame().addFreezeFrames(10);
			playerVelX += Helpers.Calc.clamp(transform.getVelX() * 10,-1,1);
			playerVelY += Helpers.Calc.clamp((transform.getVelY() * 10) - 2,-1,1);
		}
		if (health <= 0 && !dead) {
			dead = true;
			iFrames = 0;
			death.play();
		}
	}

	public void damage(int damage) {
		if (iFrames <= 0 && !dead) {
			impact.play();
			health -= damage;
			setHealthBar(health);
			iFrames = 100;
			GlobalSettings.getScreen().shake(4, 4);
			GlobalSettings.getGame().addFreezeFrames(10);
		}
		if (health <= 0 && !dead) {
			dead = true;
			iFrames = 0;
			death.play();
		}
	}

	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getStartY() {
		return startY;
	}

	public void setStartY(int startY) {
		this.startY = startY;
	}

	public void setRespawn(int x, int y) {
		startX = x;
		startY = y;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public void setDead(boolean dead) {
		if(dead) {
			health = 0;
		}else {
			health = maxHealth;
			setHealthBar(health);
			this.dead = false;
		}
	}
	
	public void heal(int healage) {
		this.health+=healage;
		if(health>maxHealth) {
			health = maxHealth;
		}
		setHealthBar(health);
	}
}
