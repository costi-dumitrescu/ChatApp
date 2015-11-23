package assistant.frame;

import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.JFrame;

import assistant.view.View;

/**
 * Base frame.
 * 
 * @author costi.dumitrescu
 */
public class Frame extends JFrame {

	/**
	 * Default serial version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The view that will be presented on the frame.
	 */
	private View view;
	
	/**
	 * Constructor.
	 */
	public Frame(View view) {
		
		// Set the view
		this.view = view;
		
		// Initialize the frame.
		this.initialize();
	}

	/**
	 * @see assistant.view.Initializable.initialize()
	 */
	private void initialize() {

		// Set the size of the JFrame.
		this.setSize();

		// Set the title of the JFrame.
		this.setTitle("ChatApp");
		
		// Set the location of the frame on the screen.
		this.setLocation();
		
		// Do not allow the JFrame to be resize.
		this.setResizable(true);

		// Set a Layout Manager.
		this.getContentPane().setLayout(new BorderLayout());

		// X
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set the contents of the JFrame.
		this.setContent();
	}

	/**
	 * Sets the size of the frame based on the current monitor.
	 */
	private void setSize() {
		Rectangle rectangle = this.getRectangle();
		int width = ((int) rectangle.getMaxX()) / 2;
		int height = ((int) rectangle.getMaxY()) / 2;

		// Half of the screen.
		this.setSize(width, height);
	}

	/**
	 * Set the JFrame on a specific location on the screen.
	 */
	private void setLocation() {
		Rectangle rectangle = this.getRectangle();
		int x = ((int) rectangle.getMaxX()) / 4;
		int y = ((int) rectangle.getMaxY()) / 4;

		// Middle of the screen.
		this.setLocation(x, y);
	}

	/**
	 * Returns the rectangle which is the screen area.
	 * 
	 * @return The rectangle. 
	 */
	private Rectangle getRectangle() {
		// Get the screen area to place the JFrame.
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
		Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
		return rect;
	}

	/**
	 * Set the content of the frame.
	 */
	private void setContent() {
		// Set the view on the frame.
		this.getContentPane().add(this.view, BorderLayout.CENTER);
	}
}
