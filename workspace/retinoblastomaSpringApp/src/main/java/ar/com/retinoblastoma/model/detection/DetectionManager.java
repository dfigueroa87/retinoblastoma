package ar.com.retinoblastoma.model.detection;

import java.util.ArrayList;

import org.opencv.core.Mat;

public interface DetectionManager {
	
	public void configureFaceDetection(double scaleFactor, int minNeighbors, int flags, int minSizeRatio);
	
	public void configureEyeDetection(double scaleFactor, int minNeighbors, int flags, int minSizeRatio);
	
	public Mat detect(Mat image);

	public ArrayList<Detection> getFaces();		

	public ArrayList<Detection> getEyes();

	public ArrayList<Detection> getPupils();
	
	
}
