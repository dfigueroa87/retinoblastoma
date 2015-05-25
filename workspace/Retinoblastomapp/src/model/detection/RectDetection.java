package model.detection;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public class RectDetection implements Detection {
	private Rect rect;
	
	public RectDetection() {
		rect = new Rect();
	}
	
	public RectDetection(Rect r) {
		rect = r;
	}

	public void draw(Mat im, Scalar sc, int originX, int originY) {
		//new Scalar(0, 255, 0)
		Core.rectangle(im, new Point(originX + rect.x, originY + rect.y), new Point(originX + rect.x + rect.width, originY + rect.y + rect.height), sc);
	}

	
	public int getX() {
		if (rect!=null)
			return rect.x;
		else
			return 0;
	}

	
	public int getY() {
		if (rect!=null)
			return rect.y;
		else
			return 0;
	}

	
	public int getWidth() {
		if (rect!=null)
			return rect.width;
		else
			return 0;
	}

	
	public int getHeight() {
		if (rect!=null)
			return rect.height;
		else
			return 0;
	}
	
	public void setRect(Rect rect) {
		this.rect = rect;
	}
	

}
