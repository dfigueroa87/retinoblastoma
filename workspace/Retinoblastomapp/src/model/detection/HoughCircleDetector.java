/**
 * 
 */
package application;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

/**
 * @author David
 *
 */
public class HoughCircleDetector extends Detector {

	public HoughCircleDetector() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public ArrayList<Detection> detect(Mat image) {
		ArrayList<Detection> detections = new ArrayList<Detection>();
		Mat transformedEye = new Mat();

		// To gray scale
		// Imgproc.cvtColor(image, transformedEye, Imgproc.COLOR_BGR2GRAY);	   
		//			   
		// Imgproc.Canny(transformedEye, transformedEye, 50, 150);

		Imgproc.cvtColor(image, transformedEye, Imgproc.COLOR_BGR2HLS);

		List<Mat> hlsPlanes = new LinkedList<Mat>();
		Core.split(transformedEye, hlsPlanes);
		String filenameS = "saturation.png";
		Highgui.imwrite(filenameS, hlsPlanes.get(2));

		Mat circles = new Mat();
		Imgproc.HoughCircles(hlsPlanes.get(2), circles, Imgproc.CV_HOUGH_GRADIENT, 1.3, hlsPlanes.get(2).rows()/1, 150, 30, 0, (int)(image.width()/2));
		//pupilsPerEye.add(circles.cols());

		// Draw the circles detected
		if (circles.cols() > 0)
			for (int x = 0; x < circles.cols(); x++) {
				System.out.println("Detected circle");
				double vCircle[] = circles.get(0,x);

				if (vCircle == null)
					break;

				Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
				int radius = (int)Math.round(vCircle[2]);

				// draw the found circle
				//Point pt2 = new Point(face.getX() + eye.getX() + Math.round(vCircle[0]), face.getY() + eye.getY() + Math.round(vCircle[1]));
				//Core.circle(image, pt2, radius, new Scalar(0,0,255), 2);

				Rect pupilRect = new Rect((int)(pt.x - (radius*Math.sqrt(2)/2)) , (int)(pt.y - (radius*Math.sqrt(2)/2)), (int)(radius*Math.sqrt(2)), (int)(radius*Math.sqrt(2)));
				//Mat pupilMat = new Mat(image, pupilRect);
				detections.add(new CircleDetection(pupilRect));

				//Core.rectangle(eyeMat, new Point((pt.x - (radius*Math.sqrt(2)/2)), (pt.y - (radius*Math.sqrt(2)/2))), new Point((pt.x + (radius*Math.sqrt(2)/2)), (pt.y + (radius*Math.sqrt(2)/2))), new Scalar(255,0,0));
				//Core.circle(eyeMat, pt, radius, new Scalar(0,255,0), 2);
//				String filename = "pupil.png";
//				Highgui.imwrite(filename, image);
//				filename = "pupilMat.png";
//				Highgui.imwrite(filename, pupilMat);
			}
		
		return detections;
	}

	@Override
	public void configure(Hashtable<String, Object> params) {
		// TODO Auto-generated method stub
		
	}

}
