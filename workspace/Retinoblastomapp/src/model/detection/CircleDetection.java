package model.detection;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public class CircleDetection extends Detection {
	private Rect circleRect;
	
	public CircleDetection() {
		circleRect = new Rect();
	}
	
	public CircleDetection(Rect r) {
		circleRect = r;
	}
	
	@Override
	public void draw(Mat im,Scalar sc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getX() {
		return circleRect.x;
	}

	@Override
	public int getY() {
		return circleRect.y;
	}

	@Override
	public int getWidth() {
		return circleRect.width;
	}

	@Override
	public int getHeight() {
		return circleRect.height;
	}

}
