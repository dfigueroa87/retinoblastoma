package main;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

public class PatternDetector {
	
	private CascadeClassifier classifier;
	protected Mat image;
	protected MatOfRect detections;
	
	private double scaleFactor = 1.05;
	private int minNeighbors = 1;
	private int flags = org.opencv.objdetect.Objdetect.CASCADE_DO_CANNY_PRUNING;
	private int minSizeRatio = 10;
	
	private Size minSize;
	private Size maxSize;
	
	public PatternDetector(Mat image, CascadeClassifier classifier) {
		this.classifier = classifier;
		this.image = image;
		this.detections = new MatOfRect();
		this.minSize = new Size(image.size().width/this.minSizeRatio, image.size().height/this.minSizeRatio);
		this.maxSize = image.size();
	}
	
	public MatOfRect Detection(){
		this.detections = new MatOfRect();
		this.classifier.detectMultiScale(this.image, this.detections, scaleFactor, minNeighbors, flags, minSize, maxSize);
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
	
	// Sets and Gets
	public void SetScaleFactor(double factor) {
		this.scaleFactor = factor;
	}
	
	public double GetScaleFactor() {
		return this.scaleFactor;
	}
	
	public void SetMinNeighbors(int value) {
		this.minNeighbors = value;
	}
	
	public int GetMinNeighbors() {
		return this.minNeighbors;
	}
	
	public void SetFlags(int value) {
		this.flags = value;
	}
	
	public int GetFlags() {
		return this.flags;
	}
	
	public void SetMinSize(Size value) {
		this.minSize = value;
	}
	
	public Size GetMinSize() {
		return this.minSize;
	}
	
	public void SetMaxSize(Size value) {
		this.maxSize = value;
	}
	
	public Size GetMaxSize() {
		return this.maxSize;
	}
	
	public void SetMinSizeRatio(int value) {
		this.minSizeRatio = value;
	}
	
	public int GetMinSizeRatio() {
		return this.minSizeRatio;
	}

}
