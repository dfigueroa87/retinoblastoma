package model.detection;

import java.util.ArrayList;
import java.util.Hashtable;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

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
	public Mat detect(Mat image)  {
		Mat result = image.clone();
		faces = faceDetector.detect(image,0,0);
		
		eyes = new ArrayList<Detection>();
		eyesPerFace = new ArrayList<Integer>();
		pupils = new ArrayList<Detection>();
		pupilsPerEye = new ArrayList<Integer>();
		
		int faceIndex = 0;
		int eyeIndex = 0;
		for (Detection face : faces) {
			// Draw detection
			face.draw(result, new Scalar(0, 255, 0));
			
			// Use only the upper 60% of the detected face to search for eyes
			Rect boundedRect= new Rect(((RectDetection)face).getX(), ((RectDetection)face).getY(), ((RectDetection)face).getWidth(), (int)Math.floor(((RectDetection)face).getHeight() * 0.60));
			Mat topOfFace = new Mat(image, boundedRect);
			
			ArrayList<Detection> eyesDetected = eyeDetector.detect(topOfFace,((RectDetection)face).getX(),((RectDetection)face).getY());
			
			// Save the number of eyes detected per each face
			eyesPerFace.add(faceIndex, eyesDetected.size());
			faceIndex++;
			
			eyes.addAll(eyesDetected);
			
			for (Detection eye : eyesDetected) {
				eye.draw(result, new Scalar(0, 0, 255));
				Rect roi = new Rect(new Point(((RectDetection)eye).getX(), ((RectDetection)eye).getY()), new Point(((RectDetection)eye).getX() + ((RectDetection)eye).getWidth(), ((RectDetection)eye).getY() + ((RectDetection)face).getHeight()));
				Mat eyeMat = new Mat(image.clone(), roi);
				ArrayList<Detection> pupilsDetected = pupilDetector.detect(eyeMat,((RectDetection)eye).getX(),((RectDetection)eye).getY());
				
				pupilsPerEye.add(eyeIndex, pupilsDetected.size());
				eyeIndex++;
				
				pupils.addAll(pupilsDetected);
				
				for (Detection pupil : pupilsDetected) {
					pupil.draw(result, new Scalar(255, 0, 0));
				}
			}
		}
		return result;
	}

	@Override
	public ArrayList<Detection> getFaces() {
		return faces;	
	}

	@Override
	public ArrayList<Detection> getEyes() {
		return eyes;
	}

	@Override
	public ArrayList<Detection> getPupils() {
		return pupils;
	}	
	

}
