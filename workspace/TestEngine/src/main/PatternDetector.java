package main;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;

public abstract class PatternDetector {
	
	protected CascadeClassifier classifier;
	protected Mat image;
	protected MatOfRect detections;
	
	public PatternDetector(Mat image, CascadeClassifier classifier) {
		this.classifier = classifier;
		this.image = image;
		this.detections = new MatOfRect();
		
	}

}
