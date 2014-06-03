package main;

import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;

public class TestEngine {
	
	public static String imagesPath = "C:/retinoblastoma/DataSet";
	public static String faceCascadeClassifierPath = "C:/retinoblastoma/workspace/Resources/CascadeClassifiers/FaceDetection";
	public static String eyeCascadeClassifierPath = "C:/retinoblastoma/workspace/Resources/CascadeClassifiers/EyeDetection";
	
	
	  public static void main(String[] args) {
		    System.out.println("Hello, OpenCV");

		    // Load the native library.
		    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		    
		    File faceCascadeDir = new File(faceCascadeClassifierPath);
		    String faceCascadeClassifiers[] = faceCascadeDir.list();
		    for(int i=0; i<faceCascadeClassifiers.length; i++) {
		    	CascadeClassifier faceDetector = new CascadeClassifier(faceCascadeDir.getAbsolutePath() + "\\" + faceCascadeClassifiers[i]);
		    	File eyeCascadeDir = new File(eyeCascadeClassifierPath);
			    String eyeCascadeClassifiers[] = eyeCascadeDir.list();
			    for(int j=0; j<eyeCascadeClassifiers.length; j++) {
			    	CascadeClassifier eyeDetector = new CascadeClassifier(eyeCascadeDir.getAbsolutePath() + "\\" + eyeCascadeClassifiers[j]);
			    	File imagesDir = new File(imagesPath);
			    	String images[] = imagesDir.list();
			    	for(int k=0; k<images.length; k++) {
			    		Mat image = Highgui.imread(imagesDir.getAbsolutePath() + "\\" + images[k]);
			    		FaceDetector face = new FaceDetector(image, faceDetector);
			    		MatOfRect detectedFaces = face.FaceDetection();
			    	}
			    }
		    }
		    
		    
		  }

}
