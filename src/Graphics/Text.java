package Graphics;

import java.awt.Color;
import java.awt.image.BufferedImage;

import Game.GlobalSettings;

public class Text {
	String displayText;
	boolean updateText = false;
	int characterPos = 0;
	int xPos;
	int yPos;
	int scale;
	Color textColor;
	BufferedImage fontData;
	Screen screen;
	private boolean instant = false;

	public Text(int _x, int _y, Color color, int _scale) {
		xPos = _x;
		yPos = _y;
		textColor = color;
		scale = _scale;
		this.fontData = GlobalSettings.getFontData();
		this.screen = GlobalSettings.getScreen();
	}
	
	public boolean isInstant() {
		return instant;
	}
	
	public void isInstant(boolean instant) {
		this.instant = instant;
	}

	public void render() {
		for (int i = 0; i < characterPos; i++) {
			drawIcon((i * 10 * scale) + xPos, yPos, 100, scale, (int) displayText.charAt(i));
		}
	}

	public void update() {
		if (characterPos < displayText.length()) {
			characterPos++;
		}
		if(instant) {
			characterPos = displayText.length();
		}
	}

	public void setText(String input) {
		displayText = input;
		characterPos = 0;
		updateText = true;
		update();
	}

	public void drawIcon(int xPos, int yPos, int shade, int scale, int icon) {
		int index = 0;
		for (int x = xPos; x < xPos + 10 * scale; x += scale) {
			for (int y = yPos; y < yPos + 10 * scale; y += scale) {
				if (tile(icon).getRGB(index / 10, index % 10) == Color.WHITE.getRGB()) {
					drawSquare(x, y, shade, scale);
				}
				index++;
			}
		}
	}

	public void drawSquare(int xPos, int yPos, int shade, int scale) {
		for (int y = yPos; y < yPos + scale; y++) {
			for (int x = xPos; x < xPos + scale; x++) {
				screen.setPixel((y * screen.getPixelWidth()) + x,shift(textColor, shade).hashCode());
			}
		}
	}

	private BufferedImage tile(int index) {
		return (fontData.getSubimage((index * 10) % fontData.getWidth(), (index * 10) / fontData.getWidth() * 10, 10, 10));
	}

	public Color shift(Color col, int percent) {
		return (new Color(Math.max(0, Math.min(255, (int) (col.getRed() * (percent / 100.0)))),
				Math.max(0, Math.min(255, (int) (col.getGreen() * (percent / 100.0)))),
				Math.max(0, Math.min(255, (int) (col.getBlue() * (percent / 100.0))))));
	}
}
