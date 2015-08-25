package ar.com.retinoblastoma.model.processing;

import org.opencv.core.Mat;


public interface ProcessingManager {
	
	public void CalculateColorsPercentageHSV(Mat img,HistogramHSV histogram);
	
	public void CalculateColorsPercentageHSL(Mat img, HistogramHSL histogram);

}
