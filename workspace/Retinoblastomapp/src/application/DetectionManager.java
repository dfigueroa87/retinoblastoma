/**
 * 
 */
package application;

/**
 * @author David
 *
 */
public class DetectionManager {
	
	private String faceClassifierPath = "C:/retinoblastoma/workspace/Resources/CascadeClassifiers/FaceDetection/haarcascade_frontalface_alt.xml";
	private String eyeClassifierPath = "C:/retinoblastoma/workspace/Resources/CascadeClassifiers/EyeDetection/haarcascade_eye.xml";
	
	private Detector faceDetector;
	private Detector eyeDetector;
	private Detector pupilDetector;
	
	public DetectionManager(String imagePath) {
		
		faceDetector = new CascadeClassifierDetector(imagePath, faceClassifierPath);
		eyeDetector = new CascadeClassifierDetector(imagePath, eyeClassifierPath);
		
	}

}
