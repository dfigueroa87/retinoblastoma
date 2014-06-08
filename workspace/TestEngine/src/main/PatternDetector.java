package main;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.objdetect.CascadeClassifier;

public class PatternDetector {
	
	private CascadeClassifier classifier;
	protected Mat image;
	protected MatOfRect detections;
	
	public PatternDetector(Mat image) {
		this.image = image;
		this.detections = new MatOfRect();
	}
	
	public PatternDetector(Mat image, CascadeClassifier classifier) {
		this.classifier = classifier;
		this.image = image;
		this.detections = new MatOfRect();
	}
	
	public PatternDetector(Mat image, CascadeClassifier classifier1, CascadeClassifier classifier2) {
		
	}
	
	public MatOfRect Detection(){
		this.detections = new MatOfRect();
		this.classifier.detectMultiScale(this.image, this.detections);
		return this.detections;
	}
	
	public Mat DrawRectangle(){
		Mat im = this.image;
		for (Rect rect : detections.toArray()) {
			   Core.rectangle(im, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
		}
		return im;
	}
	
	public int DetectedPatterns() {
		return this.detections.toArray().length;
	}

}
