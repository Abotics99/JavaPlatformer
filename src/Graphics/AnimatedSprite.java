package Graphics;

import java.awt.Color;
import java.awt.image.BufferedImage;

import Game.GlobalSettings;

public class AnimatedSprite {
	BufferedImage[][] sprite;
	int[][] tiles;
	int[][][] frames;
	BufferedImage fontData;
	Screen screen;
	Color color;
	int scale;
	boolean hidden = false;
	int xPos = 0;
	int yPos = 0;
	boolean flipped = false;
	int frame;
	int updatesPerFrame = 5;
	boolean looping;
	boolean pause = false;
	boolean hideWhenDone = false;
	boolean flashing = false;
	boolean visible = true;
	int flashSpeed = 3;
	boolean drawn = true;

	public AnimatedSprite(int[][][] frames, Color color, int scale, boolean looping, int updatesPerFrame,
			boolean startHidden, boolean hideWhenDone) {
		this.fontData = GlobalSettings.getFontData();
		this.frames = frames;
		this.looping = looping;
		setSpriteTiles(frames[0]);
		this.screen = GlobalSettings.getScreen();
		this.color = color;
		this.scale = scale;
		this.updatesPerFrame = updatesPerFrame;
		this.hidden = startHidden;
		this.hideWhenDone = hideWhenDone;
	}

	public void draw(int x, int y) {
		setPos(x, y);
		render();
	}

	public void setPos(int x, int y) {
		xPos = x;
		yPos = y;
	}

	public int getTile(int x, int y) {
		int temp1 = (y - yPos) / (10 * scale);
		int temp2 = (x - xPos) / (10 * scale);
		if (temp1 >= 0 && temp2 >= 0 && temp1 < tiles.length && temp2 < tiles[0].length) {
			return tiles[temp1][temp2];
		}
		return -1;
	}

	public void setSpriteTiles(int[][] tiles) {
		this.tiles = tiles;
		sprite = new BufferedImage[tiles.length][tiles[0].length];
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				sprite[i][j] = tile(tiles[i][j]);
			}
		}
	}

	public void render() {
		if (!hidden) {
			setSpriteTiles(frames[frame / updatesPerFrame]);
			if (frame % flashSpeed == 0 && flashing)
				visible = !visible;
			if (visible || !flashing) {
				if (flipped) {
					for (int i = 0; i < sprite.length; i++) {
						for (int j = 0; j < sprite[i].length; j++) {
							if (sprite[i][j] != null) {
								drawIcon(xPos + (j * 10 * scale) - (int) screen.getCamX(),
										yPos + (i * 10 * scale) - (int) screen.getCamY(),
										sprite[i][Math.abs(j - sprite[0].length) - 1], 100, scale, false);
							}
						}
					}
				} else {
					for (int i = 0; i < sprite.length; i++) {
						for (int j = 0; j < sprite[i].length; j++) {
							if (sprite[i][j] != null) {
								drawIcon(xPos + (j * 10 * scale) - (int) screen.getCamX(),
										yPos + (i * 10 * scale) - (int) screen.getCamY(), sprite[i][j], 100, scale,
										false);
							}
						}
					}
				}

			}
			if (!pause) {
				frame += 1;

				if (frame >= (frames.length) * updatesPerFrame) {
					if (looping) {
						frame = 0;
					} else {
						if (hideWhenDone) {
							hidden = true;
						} else {
							pause = true;
							frame -= 1;
						}
					}
				}
			}
		}
	}

	public void restartAnim() {
		pause = false;
		hidden = false;
		frame = 0;
	}

	public void setFrameNumber(int frameNum) {
		frame = frameNum * updatesPerFrame;
	}

	private void drawIcon(int xPos, int yPos, BufferedImage tile, int shade, int scale, boolean inverted) {
		int index = 0;
		if (xPos >= -10 * scale && yPos >= -10 * scale && xPos < screen.getPixelWidth()
				&& yPos < screen.getPixelHeight()) {
			if (flipped) {
				for (int x = xPos + 10 * scale - 1; x >= xPos; x -= scale) {
					for (int y = yPos; y < yPos + 10 * scale; y += scale) {
						if (inverted) {
							if (tile.getRGB(index / 10, index % 10) == Color.BLACK.getRGB()) {
								drawSquare(x, y, shade, scale);
							}
						} else {
							if (tile.getRGB(index / 10, index % 10) == Color.WHITE.getRGB()) {
								drawSquare(x, y, shade, scale);
							}
						}
						index++;
					}
				}
			} else {
				for (int x = xPos; x < xPos + 10 * scale; x += scale) {
					for (int y = yPos; y < yPos + 10 * scale; y += scale) {
						if (inverted) {
							if (tile.getRGB(index / 10, index % 10) == Color.BLACK.getRGB()) {
								drawSquare(x, y, shade, scale);
							}
						} else {
							if (tile.getRGB(index / 10, index % 10) == Color.WHITE.getRGB()) {
								drawSquare(x, y, shade, scale);
							}
						}
						index++;
					}
				}
			}
		}
	}

	private void drawSquare(int xPos, int yPos, int shade, int scale) {
		for (int y = yPos; y < yPos + scale; y++) {
			for (int x = xPos; x < xPos + scale; x++) {
				if (x >= 0 && x < screen.getPixelWidth()) {
					screen.setPixel((y * screen.getPixelWidth()) + x, shift(color, shade).hashCode());
				}
			}
		}
	}

	private BufferedImage tile(int index) {
		if (index == -1) {
			return null;
		}
		return (fontData.getSubimage((index * 10) % fontData.getWidth(), (index * 10) / fontData.getWidth() * 10, 10,
				10));
	}

	private Color shift(Color col, int percent) {
		return (new Color(Math.max(0, Math.min(255, (int) (col.getRed() * (percent / 100.0)))),
				Math.max(0, Math.min(255, (int) (col.getGreen() * (percent / 100.0)))),
				Math.max(0, Math.min(255, (int) (col.getBlue() * (percent / 100.0))))));
	}

	public void isHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}

	public boolean isFlipped() {
		return flipped;
	}

	public double getPosX() {
		return xPos;
	}

	public double getPosY() {
		return yPos;
	}

	public double getWidth() {
		return sprite[0].length * 10;
	}

	public double getHeight() {
		return sprite.length * 10;
	}

	public void setFlashSpeed(int flashSpeed) {
		this.flashSpeed = flashSpeed;
	}

	public void setFlashing(boolean flashing) {
		this.flashing = flashing;
	}

	public boolean isFlashing() {
		return flashing;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public boolean isPaused() {
		return pause;
	}

	public void setLooping(boolean looping) {
		this.looping = looping;
	}

	public void setFrames(int[][][] frames) {
		frame = 0;
		this.frames = frames;
	}

	public void setUpdatesPerFrame(int updates) {
		this.updatesPerFrame = updates;
	}

	public int[][][] getFrames() {
		return frames;
	}
}
