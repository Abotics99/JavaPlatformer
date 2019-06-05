package Graphics;

import java.awt.Color;

import Game.GlobalSettings;

public class Fader {
	int fadeSpeed = 1;
	boolean hidden = false;
	boolean totallyVisible = false;
	boolean fade = false;
	Sprite grad_1;
	Sprite grad_2;
	Sprite grad_3;
	Sprite grad_4;
	double fadePosX = 0;
	double fadePosY = 0;
	Color color;

	public Fader(int fadeSpeed, Color color) {
		this.fadeSpeed = fadeSpeed;
		this.color = color;
		grad_1 = new Sprite(fillArray(GlobalSettings.getScreen().getPixelWidth() / 10,
				(GlobalSettings.getScreen().getPixelHeight() / 10) + 25, 176), color, 1);
		grad_2 = new Sprite(fillArray(GlobalSettings.getScreen().getPixelWidth() / 10,
				(GlobalSettings.getScreen().getPixelHeight() / 10) + 17, 177), color, 1);
		grad_3 = new Sprite(fillArray(GlobalSettings.getScreen().getPixelWidth() / 10,
				(GlobalSettings.getScreen().getPixelHeight() / 10) + 9, 178), color, 1);
		grad_4 = new Sprite(fillArray(GlobalSettings.getScreen().getPixelWidth() / 10,
				(GlobalSettings.getScreen().getPixelHeight() / 10) + 1, 241), color, 1);
		fade();
	}

	public void render() {
		if (fade) {
			if (fadePosY < 120) {
				fadePosY += fadeSpeed;
				hidden = false;
				totallyVisible = false;
			}
			if(fadePosY >= 120) {
				fadePosY = 120;
				hidden = true;
			}
		} else {
			if (fadePosY < grad_4.getHeight()+240) {
				fadePosY += fadeSpeed;
				totallyVisible = false;
				hidden = false;
			}
			if(fadePosY >= grad_4.getHeight()+240) {
				fadePosY = grad_4.getHeight()+241;
				totallyVisible = true;
			}
		}
		grad_1.setPos((int) (GlobalSettings.getScreen().getCamX() - fadePosX),
				(int) (GlobalSettings.getScreen().getCamY() - fadePosY));
		grad_2.setPos((int) (GlobalSettings.getScreen().getCamX() - fadePosX),
				(int) (GlobalSettings.getScreen().getCamY() - fadePosY + 40));
		grad_3.setPos((int) (GlobalSettings.getScreen().getCamX() - fadePosX),
				(int) (GlobalSettings.getScreen().getCamY() - fadePosY + 80));
		grad_4.setPos((int) (GlobalSettings.getScreen().getCamX() - fadePosX),
				(int) (GlobalSettings.getScreen().getCamY() - fadePosY + 120));
		grad_1.render();
		grad_2.render();
		grad_3.render();
		grad_4.render();
	}

	public void fade() {
		fade = true;
		fadePosY = -grad_4.getHeight();
	}
	
	public void fadeInstant() {
		fade = true;
		fadePosY = 120;
	}

	public void show() {
		fade = false;
		fadePosY = 120;
	}

	int[][] fillArray(int width, int height, int index) {
		int[][] temp = new int[height][width];
		for (int i = 0; i < temp.length; i++) {
			for (int j = 0; j < temp[0].length; j++) {
				temp[i][j] = index;
			}
		}
		return temp;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isTotallyVisible() {
		return totallyVisible;
	}

	public void setTotallyVisible(boolean totallyVisible) {
		this.totallyVisible = totallyVisible;
	}

	public boolean isFade() {
		return fade;
	}

	public void setFade(boolean fade) {
		this.fade = fade;
	}

}
