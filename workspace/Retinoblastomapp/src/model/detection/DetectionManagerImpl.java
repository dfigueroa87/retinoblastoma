package model.detection;

import java.util.ArrayList;
import java.util.Hashtable;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;

public class DetectionManagerImpl implements DetectionManager {
	
	private static String FACE_CLASSIFIER_PATH = "C:/retinoblastoma/workspace/Resources/CascadeClassifiers/FaceDetection/haarcascade_frontalface_alt.xml";
	private static String EYE_CLASSIFIER_PATH = "C:/retinoblastoma/workspace/Resources/CascadeClassifiers/EyeDetection/haarcascade_eye.xml";
	
	private Detector faceDetector;
	private Detector eyeDetector;
	private Detector pupilDetector;
	
	private ArrayList<Detection> faces;
	private ArrayList<Detection> eyes;
	private ArrayList<Detection> pupils;
	
	private ArrayList<Integer> eyesPerFace;
	private ArrayList<Integer> pupilsPerEye;
	
	public DetectionManagerImpl() {
		faceDetector = new CascadeClassifierDetector(FACE_CLASSIFIER_PATH);
		eyeDetector = new CascadeClassifierDetector(EYE_CLASSIFIER_PATH);
		pupilDetector = new HoughCircleDetector();
	}

	@Override
	public void configureFaceDetection(double scaleFactor, int minNeighbors,
			int flags, int minSizeRatio) {
		Hashtable<String, Object> values = new Hashtable<>();
		values.put("scaleFactor", scaleFactor);
		values.put("minNeighbors", minNeighbors);
		values.put("flags", flags);
		values.put("minSizeRatio", minSizeRatio);
		faceDetector.configure(values);

	}

	@Override
	public void configureEyeDetection(double scaleFactor, int minNeighbors,
			int flags, int minSizeRatio) {
		Hashtable<String, Object> values = new Hashtable<>();
		values.put("scaleFactor", scaleFactor);
		values.put("minNeighbors", minNeighbors);
		values.put("flags", flags);
		values.put("minSizeRatio", minSizeRatio);
		eyeDetector.configure(values);
	}

	@Override
	public void detect(Mat image)  {
		faces = faceDetector.detect(image);
		
		eyes = new ArrayList<Detection>();
		eyesPerFace = new ArrayList<Integer>();
		pupils = new ArrayList<Detection>();
		pupilsPerEye = new ArrayList<Integer>();
		
		int faceIndex = 0;
		int eyeIndex = 0;
		for (Detection face : faces) {
			// Use only the upper 60% of the detected face to search for eyes
			Rect boundedRect= new Rect(face.getX(), face.getY(), face.getWidth(), (int)Math.floor(face.getHeight() * 0.60));
			Mat topOfFace = new Mat(image, boundedRect);
			
			ArrayList<Detection> eyesDetected = eyeDetector.detect(topOfFace);
			
			// Save the number of eyes detected per each face
			eyesPerFace.add(faceIndex, eyesDetected.size());
			faceIndex++;
			
			eyes.addAll(eyesDetected);
			
			for (Detection eye : eyesDetected) {
				Rect roi = new Rect(new Point(face.getX() + eye.getX(), face.getY() + eye.getY()), new Point(face.getX() + eye.getX() + eye.getWidth(), face.getY() + eye.getY() + face.getHeight()));
				Mat eyeMat = new Mat(image.clone(), roi);
				ArrayList<Detection> pupilsDetected = pupilDetector.detect(eyeMat);
				
				pupilsPerEye.add(eyeIndex, pupilsDetected.size());
				eyeIndex++;
				
				pupils.addAll(pupilsDetected);
			}
		}
	}

}
