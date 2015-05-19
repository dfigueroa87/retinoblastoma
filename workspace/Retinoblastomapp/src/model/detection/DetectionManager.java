package model.detection;

import org.opencv.core.Mat;

public interface DetectionManager {
	
	public void configureFaceDetection(double scaleFactor, int minNeighbors, int flags, int minSizeRatio);
	
	public void configureEyeDetection(double scaleFactor, int minNeighbors, int flags, int minSizeRatio);
	
	public void detect(Mat image);
	
}
