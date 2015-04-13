/**
 * 
 */
package application;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

/**
 * @author David
 *
 */
public class DetectionManager {
	
	private String faceClassifierPath = "C:/retinoblastoma/workspace/Resources/CascadeClassifiers/FaceDetection/haarcascade_frontalface_alt.xml";
	private String eyeClassifierPath = "C:/retinoblastoma/workspace/Resources/CascadeClassifiers/EyeDetection/haarcascade_eye.xml";
	
	private CascadeClassifierDetector faceDetector;
	private CascadeClassifierDetector eyeDetector;
	private Detector pupilDetector;
	
	private ArrayList<Detection> faces;
	private ArrayList<Detection> eyes;
	private ArrayList<Detection> pupils;
	
	private ArrayList<Integer> eyesPerFace;
	private ArrayList<Integer> pupilsPerEye;
	
	public DetectionManager() {
		
		faceDetector = new CascadeClassifierDetector(faceClassifierPath);
		eyeDetector = new CascadeClassifierDetector(eyeClassifierPath);
		
	}
	
	public void configureFaceDetection(double scaleFactor, int minNeighbors, int flags, int minSizeRatio) {
		faceDetector.setScaleFactor(scaleFactor);
		faceDetector.setMinNeighbors(minNeighbors);
		faceDetector.setFlags(flags);
		faceDetector.setMinSizeRatio(minSizeRatio);
	}
	
	public void configureEyeDetection(double scaleFactor, int minNeighbors, int flags, int minSizeRatio) {
		eyeDetector.setScaleFactor(scaleFactor);
		eyeDetector.setMinNeighbors(minNeighbors);
		eyeDetector.setFlags(flags);
		eyeDetector.setMinSizeRatio(minSizeRatio);
	}
	
	public void detect(Mat image) {
		faces = faceDetector.detect(image);
		eyes = new ArrayList<Detection>();
		eyesPerFace = new ArrayList<Integer>();
		int index = 0;
		for (Detection face : faces) {
			// Use only the upper 60% of the detected face to search for eyes
			Rect boundedRect= new Rect(face.getX(), face.getY(), face.getWidth(), (int)Math.floor(face.getHeight() * 0.60));
			Mat topOfFace = new Mat(image, boundedRect);
			
			ArrayList<Detection> eyesDetected = eyeDetector.detect(topOfFace);
			
			// Save the number of eyes detected per each face
			eyesPerFace.add(index, eyesDetected.size());
			
			eyes.addAll(eyesDetected);
			index++;
			
			for (Detection eye : eyesDetected) {
				Rect roi = new Rect(new Point(face.getX() + eye.getX(), face.getY() + eye.getY()), new Point(face.getX() + eye.getX() + eye.getWidth(), face.getY() + eye.getY() + face.getHeight()));

				Mat eyeMat = new Mat(image.clone(), roi);

				Mat transformedEye = new Mat();

				// To gray scale
				// Imgproc.cvtColor(eye, transformedEye, Imgproc.COLOR_BGR2GRAY);	   
				//			   
				// Imgproc.Canny(transformedEye, transformedEye, 50, 150);

				Imgproc.cvtColor(eyeMat, transformedEye, Imgproc.COLOR_BGR2HLS);

				List<Mat> hlsPlanes = new LinkedList<Mat>();
				Core.split(transformedEye, hlsPlanes);
				String filenameS = "saturation.png";
				Highgui.imwrite(filenameS, hlsPlanes.get(2));

				Mat circles = new Mat();
				Imgproc.HoughCircles(hlsPlanes.get(2), circles, Imgproc.CV_HOUGH_GRADIENT, 1.3, hlsPlanes.get(2).rows()/1, 150, 30, 0, (int)(eye.getWidth()/2));
				pupilsPerEye.add(circles.cols());

				// Draw the circles detected
				if (circles.cols() > 0)
					for (int x = 0; x < circles.cols(); x++) {
						System.out.println("Detected circle");
						double vCircle[] = circles.get(0,x);

						if (vCircle == null)
							break;

						//Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
						int radius = (int)Math.round(vCircle[2]);

						// draw the found circle

						Point pt2 = new Point(face.getX() + eye.getX() + Math.round(vCircle[0]), face.getY() + eye.getY() + Math.round(vCircle[1]));
						Core.circle(image, pt2, radius, new Scalar(0,0,255), 2);

						Rect pupilRect = new Rect((int)(pt2.x - (radius*Math.sqrt(2)/2)) , (int)(pt2.y - (radius*Math.sqrt(2)/2)), (int)(radius*Math.sqrt(2)), (int)(radius*Math.sqrt(2)));
						Mat pupilMat = new Mat(image, pupilRect);
						//pupils.add(pupilRect);

						//Core.rectangle(eyeMat, new Point((pt.x - (radius*Math.sqrt(2)/2)), (pt.y - (radius*Math.sqrt(2)/2))), new Point((pt.x + (radius*Math.sqrt(2)/2)), (pt.y + (radius*Math.sqrt(2)/2))), new Scalar(255,0,0));
						//Core.circle(eyeMat, pt, radius, new Scalar(0,255,0), 2);
						String filename = "pupil.png";
						Highgui.imwrite(filename, eyeMat);
						filename = "pupilMat.png";
						Highgui.imwrite(filename, pupilMat);
					}
			}
		}
	}
	
}
