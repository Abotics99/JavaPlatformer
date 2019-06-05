package Graphics;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

import Game.GlobalSettings;
import kuusisto.tinysound.TinySound;

public class Screen extends Canvas implements MouseInputListener, KeyListener {
	// --screenSettings--
	public static final int WIDTH = 260;
	public static final int HEIGHT = WIDTH / 12 * 9;
	public static final int SCALE = 3;
	public static final String NAME = "Jumpy Shoot Boy";
	private JFrame frame;
	private static final long serialVersionUID = 1L;

	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	GraphicsDevice gs = ge.getDefaultScreenDevice();
	GraphicsConfiguration gc = gs.getDefaultConfiguration();

	private BufferedImage image = gc.createCompatibleImage(WIDTH, HEIGHT);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

	// --mouse data--
	private int mouseX = 0;
	private int mouseY = 0;
	private boolean mouseDown = false;

	// --Keyboard data--
	private boolean up;
	private boolean down;
	private boolean left;
	private boolean right;
	private boolean jump;
	private boolean fire;
	private boolean escape;

	// camera data
	private double cameraX = 0;
	private double cameraY = 0;
	private double cameraOffsetX = 0;
	private double cameraOffsetY = 0;
	int shakeTimer = 0;
	int shakeAmount;

	// timing
	public double deltaTime;
	public long lastTime;
	public long time;
	private int frameCount;

	public Screen() {
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		time = System.nanoTime();
		lastTime = time;
	}

	public void init() {
		setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));

		frame = new JFrame(NAME);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		frame.add(this, BorderLayout.CENTER);
		frame.pack();

		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.out.println("bye...");
				TinySound.shutdown();
			}
		});
	}

	public void render() {
		if (shakeTimer > 0) {
			setCameraOffset(Helpers.Calc.RandomRange(-shakeAmount, shakeAmount),
					Helpers.Calc.RandomRange(-shakeAmount, shakeAmount));
			shakeTimer--;
		} else {
			setCameraOffset(0, 0);
		}

		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(2);
			return;
		}

		Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);

		g.dispose();
		time = System.nanoTime();
		deltaTime = time - lastTime;
		lastTime = time;
		frameCount++;
		bs.show();

	}

	public int getPixel(int index) {
		return pixels[index];
	}

	public void setPixel(int index, int val) {
		if (index < pixels.length && index >= 0) {
			pixels[index] = val;
		}
	}

	public int getPixelWidth() {
		return WIDTH;
	}

	public int getPixelHeight() {
		return HEIGHT;
	}

	public int getPixelScale() {
		return SCALE;
	}

	public void background(Color col) {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = lerpColor(new Color(pixels[i]), col, col.getAlpha()).hashCode();
		}
	}

	public Color lerpColor(Color col1, Color col2, int alpha) {
		return new Color((int) Helpers.Calc.lerp(col1.getRed(), col2.getRed(), alpha / 255.0),
				(int) Helpers.Calc.lerp(col1.getGreen(), col2.getGreen(), alpha / 255.0),
				(int) Helpers.Calc.lerp(col1.getBlue(), col2.getBlue(), alpha / 255.0));
	}

	// --mouse related info!!!!!!--

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent mouse) {
		mouseDown = true;
	}

	public void mouseReleased(MouseEvent mouse) {
		mouseDown = false;
	}

	public void mouseDragged(MouseEvent mouse) {
		mouseX = mouse.getX();
		mouseY = mouse.getY();
	}

	public void mouseMoved(MouseEvent mouse) {
		mouseX = mouse.getX();
		mouseY = mouse.getY();
	}

	public int getMouseX() {
		return mouseX / SCALE;
	}

	public int getMouseY() {
		return mouseY / SCALE;
	}

	public boolean mouseDown() {
		return mouseDown;
	}

	public boolean upPressed() {
		return up;
	}

	public boolean downPressed() {
		return down;
	}

	public boolean leftPressed() {
		return left;
	}

	public boolean rightPressed() {
		return right;
	}

	public boolean jumpPressed() {
		return jump;
	}

	public boolean firePressed() {
		return fire;
	}

	public boolean escapePressed() {
		return escape;
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == 'w')
			up = true;
		if (e.getKeyChar() == 's')
			down = true;
		if (e.getKeyChar() == 'a')
			left = true;
		if (e.getKeyChar() == 'd')
			right = true;
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
			jump = true;
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
			fire = true;
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			escape = true;
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyChar() == 'w')
			up = false;
		if (e.getKeyChar() == 's')
			down = false;
		if (e.getKeyChar() == 'a')
			left = false;
		if (e.getKeyChar() == 'd')
			right = false;
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
			jump = false;
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
			fire = false;
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			escape = false;
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public double getCamX() {
		return cameraX + cameraOffsetX;
	}

	public double getCamY() {
		return cameraY + cameraOffsetY;
	}

	public void setCameraPos(double x, double y) {
		if (GlobalSettings.getGame() != null) {
			this.cameraX = Helpers.Calc.clamp(x, 0, GlobalSettings.getGame().getCurrentWorld().getWidth()-getPixelWidth());
			this.cameraY = Helpers.Calc.clamp(y, 0, GlobalSettings.getGame().getCurrentWorld().getHeight()-getPixelHeight());
		}else {
			this.cameraX = x;
			this.cameraY = y;
		}
	}

	public void setCameraOffset(double x, double y) {
		this.cameraOffsetX = x;
		this.cameraOffsetY = y;
	}

	public void shake(int length, int amount) {
		this.shakeTimer = length;
		this.shakeAmount = amount;
	}

	public int getFrameCount() {
		return frameCount;
	}
}
