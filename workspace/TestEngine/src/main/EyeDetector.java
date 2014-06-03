package main;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.objdetect.CascadeClassifier;

public class EyeDetector extends PatternDetector {
	
	public EyeDetector(Mat image, CascadeClassifier classifier){
		super(image, classifier);
		
	}
	
	public MatOfRect EyeDetection(){
		detections = new MatOfRect();
		classifier.detectMultiScale(image, this.detections);
		System.out.println(String.format("Detected %s eyes", this.detections.toArray().length));
		return this.detections;
	}
	
	public Mat DrawRectangle(MatOfRect detections, Mat image, int x, int y){
		Mat im = this.image;
		for (Rect rect : detections.toArray()) {
			   Core.rectangle(im, new Point(x + rect.x, y + rect.y), new Point(x + rect.x + rect.width, y + rect.y + rect.height), new Scalar(255, 0, 0));
		}
		return im;
	}

}
