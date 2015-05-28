package model.detection;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;


public interface Detection {
	
	public void draw(Mat im, Scalar sc);
	
}
