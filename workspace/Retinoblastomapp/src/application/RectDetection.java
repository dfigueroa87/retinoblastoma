/**
 * 
 */
package application;

import org.opencv.core.Rect;

import javafx.scene.image.Image;

/**
 * @author David
 *
 */
public class RectDetection extends Detection {
	private Rect rect;
	
	public RectDetection(Rect r) {
		rect = r;
	}

	public Image draw(Image im) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getX() {
		return rect.x;
	}

	@Override
	public int getY() {
		return rect.y;
	}

	@Override
	public int getWidth() {
		return rect.width;
	}

	@Override
	public int getHeight() {
		return rect.height;
	}

}
