package Graphics;

import java.awt.Color;
import java.awt.image.BufferedImage;

import Game.GlobalSettings;

public class Sprite {
	BufferedImage[][] sprite;
	int[][] tiles;
	BufferedImage fontData;
	Screen screen;
	Color color;
	int scale;
	boolean hidden = false;
	int xPos = 0;
	int yPos = 0;
	boolean flipped = false;
	boolean flashing = false;
	boolean visible = true;
	int flashSpeed = 3;
	long frameCount = 0;

	public Sprite(int[][] tiles, Color color, int scale) {
		this.fontData = GlobalSettings.getFontData();
		setSpriteTiles(tiles);
		this.screen = GlobalSettings.getScreen();
		this.color = color;
		this.scale = scale;
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
			if (frameCount % flashSpeed == 0 && flashing)
				visible = !visible;
			if (visible || !flashing) {
				for (int i = 0; i < sprite.length; i++) {
					for (int j = 0; j < sprite[i].length; j++) {
						if (sprite[i][j] != null) {
							if (this.color != null) {
								drawIcon(xPos + (j * 10 * scale) - (int) screen.getCamX(),
										yPos + (i * 10 * scale) - (int) screen.getCamY(), sprite[i][j], 100, scale,
										false);
							} else {
								drawTile(xPos + (j * 10 * scale) - (int) screen.getCamX(),
										yPos + (i * 10 * scale) - (int) screen.getCamY(), sprite[i][j], 100, scale,
										false);
							}
						}
					}
				}
			}
			frameCount++;
		}
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
								drawSquare(x, y, shade, this.color, scale);
							}
						} else {
							if (tile.getRGB(index / 10, index % 10) == Color.WHITE.getRGB()) {
								drawSquare(x, y, shade, this.color, scale);
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
								drawSquare(x, y, shade, this.color, scale);
							}
						} else {
							if (tile.getRGB(index / 10, index % 10) == Color.WHITE.getRGB()) {
								drawSquare(x, y, shade, this.color, scale);
							}
						}
						index++;
					}
				}
			}
		}
	}

	private void drawTile(int xPos, int yPos, BufferedImage tile, int shade, int scale, boolean inverted) {
		int index = 0;
		if (xPos >= -10 * scale && yPos >= -10 * scale && xPos < screen.getPixelWidth()
				&& yPos < screen.getPixelHeight()) {
			if (flipped) {
				for (int x = xPos + 10 * scale - 1; x >= xPos; x -= scale) {
					for (int y = yPos; y < yPos + 10 * scale; y += scale) {
						drawSquare(x, y, shade, new Color(tile.getRGB(index / 10, index % 10), true), scale);
						index++;
					}
				}
			} else {
				for (int x = xPos; x < xPos + 10 * scale; x += scale) {
					for (int y = yPos; y < yPos + 10 * scale; y += scale) {
						drawSquare(x, y, shade, new Color(tile.getRGB(index / 10, index % 10), true), scale);
						index++;
					}
				}
			}
		}
	}

	private void drawSquare(int xPos, int yPos, int shade, Color color, int scale) {
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
}
