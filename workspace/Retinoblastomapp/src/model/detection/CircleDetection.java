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
public class CircleDetection extends Detection {
	private Rect circleRect;
	
	public CircleDetection(Rect r) {
		circleRect = r;
	}
	
	@Override
	public Image draw(Image im) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getX() {
		// TODO Auto-generated method stub
		return circleRect.x;
	}

	@Override
	public int getY() {
		// TODO Auto-generated method stub
		return circleRect.y;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return circleRect.width;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return circleRect.height;
	}

}
