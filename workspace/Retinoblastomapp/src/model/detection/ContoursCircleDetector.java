package model.detection;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class ContoursCircleDetector extends Detector {

	@Override
	public void configure(Hashtable<String, Object> params) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<Detection> detect(Mat image, int absolutePx, int absolutePy) {
		ArrayList<Detection> detections = new ArrayList<Detection>();

		// Invert eye
		Mat invEye = new Mat(image.rows(),image.cols(), image.type(), new Scalar(255,255,255));
		Core.subtract(invEye, image, image);
		// To gray scale
		Imgproc.cvtColor(invEye, image, Imgproc.COLOR_BGR2GRAY);
		// Convert to binary image by thresholding it
		Imgproc.threshold(image, image, 220, 255, Imgproc.THRESH_BINARY);

		// Find all contours
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(image.clone(), contours, new Mat(), Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_NONE);
		// Fill holes in each contour
		Imgproc.drawContours(image, contours, -1, new Scalar(255,255,255), -1);

		for (int n = 0; n < contours.size(); n++) {
			double area = Imgproc.contourArea(contours.get(n));    // Blob area
			Rect rectangle = Imgproc.boundingRect(contours.get(n)); // Bounding box
			int radius = rectangle.width/2;                     // Approximate radius
			// Look for round shaped blob
			if (area >= 30 && 
					Math.abs(1 - ((double)rectangle.width / (double)rectangle.height)) <= 0.2 &&
					Math.abs(1 - (area / (Math.PI * Math.pow(radius, 2)))) <= 0.2) {
//				Core.circle(image, new Point(rectangle.x + radius, rectangle.y + radius), radius, new Scalar(255,0,0), 2);
				CircleDetection circle = new CircleDetection(new Point(rectangle.x + radius + absolutePx, rectangle.y + radius + absolutePy), radius);
				detections.add(circle);	
				System.out.println("Circle detected");
			}
		}
		return detections;
	}

}
