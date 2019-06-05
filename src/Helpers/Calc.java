package Helpers;

public class Calc {

	public static float map(float value, float istart, float istop, float ostart, float ostop) {
		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	}
	
	public static double RandomRange(double min, double max) {
		return (Math.random() * (max - min)) + min;
		
	}
	
	public static double lerp(double var1, double var2, double alpha) {
		return (var1 + (alpha) * (var2 - var1));
	}
	
	public static double dist(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
	
	public static double clamp(double value, double min, double max) {
		return Math.min(max, Math.max(value, min));
	}
}
