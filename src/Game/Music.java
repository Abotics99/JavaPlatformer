package Game;

import java.io.File;

import kuusisto.tinysound.TinySound;

public class Music {

	// Each sound effect has its own clip, loaded with its own sound file.
	kuusisto.tinysound.Music music;
	
	int posX = 0;
	int posY = 0;
	float panDistance = 1000;
	float volume = 1;

	// Constructor to construct each element of the enum with its own sound file.
	Music(String name) {
		music = TinySound.loadMusic(new File("res/" + name + ".wav"));
	}

	// Play or Re-play the sound effect from the beginning, by rewinding.
	public void play() {
		if(music.playing()) {
			music.stop();
		}
		music.rewind();
		music.play(true);
	}
	
	public void update() {
		int camX = (int) (GlobalSettings.getScreen().getCamX() + (GlobalSettings.getScreen().getPixelWidth()/2));
		int camY = (int) (GlobalSettings.getScreen().getCamY() + (GlobalSettings.getScreen().getPixelHeight()/2));
		double distance = Helpers.Calc.dist(posX, posY, camX, camY);
		music.setPan(Helpers.Calc.clamp(Helpers.Calc.map(posX - camX, -panDistance, panDistance, -1, 1),-1,1));
		music.setVolume(Helpers.Calc.clamp(Helpers.Calc.map((float) distance, 0, panDistance, volume, 0),0,volume));
	}
	
	public void playAt(int x, int y) {
		posX = x;
		posY = y;
		play();
	}

	public void stop() {
		music.stop();
	}
	
	public boolean isPlaying() {
		return music.playing();
	}
}