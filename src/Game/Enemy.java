package Game;

public interface Enemy extends Transform {
	public void damage(int damage);
	public void damage(int damage, Transform transform);
	public double getHealth();
	public boolean colliding(double x, double y);
	public void render();
	public void update();
	public Hazard getHazard();
}
