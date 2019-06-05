package Game;

import java.io.File;

import kuusisto.tinysound.TinySound;

public class Sound {

	// Each sound effect has its own clip, loaded with its own sound file.
	kuusisto.tinysound.Sound sound;

	float panDistance = 200;
	float volume = 1;

	// Constructor to construct each element of the enum with its own sound file.
	Sound(String name) {
		sound = TinySound.loadSound(new File("res/" + name + ".wav"));
	}

	// Play or Re-play the sound effect from the beginning, by rewinding.
	public void play() {
		sound.play(volume, 0);
	}
	
	public void playAt(int x, int y) {
		int camX = (int) (GlobalSettings.getScreen().getCamX() + (GlobalSettings.getScreen().getPixelWidth()/2));
		float pan = (float) Helpers.Calc.clamp(Helpers.Calc.map(x - camX, -panDistance, panDistance, -1, 1),-1,1);
		sound.play(volume, pan);
	}

	public void stop() {
		sound.stop();
	}
}