package ar.com.retinoblastoma.model.detection;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public class RectDetection implements Detection {
	private Rect rect;
	private ArrayList<Detection> innerDetections;
	
	public RectDetection() {
		rect = new Rect();
	}
	
	public RectDetection(Rect r) {
		rect = r;
	}

	public void draw(Mat im, Scalar sc) {
		//new Scalar(0, 255, 0)
		Core.rectangle(im, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), sc);
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
	
	public void setInnerDetections(ArrayList<Detection> dets) {
		innerDetections = dets;
	}
	
	public ArrayList<Detection> getInnerDetections() {
		return innerDetections;
	}

}
