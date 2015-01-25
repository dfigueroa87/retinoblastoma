/**
 * 
 */
package application;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

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
		
		for (Detection det : faces) {
			
		}
	}
	
}
