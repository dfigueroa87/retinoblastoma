package main;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.objdetect.CascadeClassifier;

public class FaceDetector extends PatternDetector {
	
	public FaceDetector(Mat image, CascadeClassifier classifier) {
		super(image, classifier);
	}

	public MatOfRect FaceDetection(){
		detections = new MatOfRect();
		classifier.detectMultiScale(image, detections);
		System.out.println(String.format("Detected %s faces", detections.toArray().length));
		return detections;
	}
	
	public Mat DrawRectangle(){
		Mat im = this.image;
		for (Rect rect : detections.toArray()) {
			   Core.rectangle(im, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
		}
		return im;
	}
	
	public int DetectedFaces() {
		return this.detections.toArray().length;
	}

}
