package model.processing;

import org.opencv.core.Mat;


public interface ProcessingManager {
	
	public void CalculateColorsPercentage(Mat img,HistogramHSV histogram);

}
