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
		pupilDetector = new HoughCircleDetector();
		
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
				
				pupilDetector.detect(eyeMat);

			}
		}
	}
	
}
