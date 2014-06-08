package main;

import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class TestEngine {
	
	public static String imagesPath = "C:/retinoblastoma/DataSet";
	public static String faceCascadeClassifierPath = "C:/retinoblastoma/workspace/Resources/CascadeClassifiers/FaceDetection";
	public static String eyeCascadeClassifierPath = "C:/retinoblastoma/workspace/Resources/CascadeClassifiers/EyeDetection";
	
	
	  public static void main(String[] args) {

		    // Load the native library.
		    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		    
		    File faceCascadeDir = new File(faceCascadeClassifierPath);
		    String faceCascadeClassifiers[] = faceCascadeDir.list();
		    for(int i=0; i<faceCascadeClassifiers.length; i++) {
		    	long startTime = System.currentTimeMillis();
		    	CascadeClassifier faceClassifier = new CascadeClassifier(faceCascadeDir.getAbsolutePath() + "\\" + faceCascadeClassifiers[i]);
		    	//File eyeCascadeDir = new File(eyeCascadeClassifierPath);
			    //String eyeCascadeClassifiers[] = eyeCascadeDir.list();
			    //for(int j=0; j<eyeCascadeClassifiers.length; j++) {
			    	//CascadeClassifier eyeClassifier = new CascadeClassifier(eyeCascadeDir.getAbsolutePath() + "\\" + eyeCascadeClassifiers[j]);
			    	File imagesDir = new File(imagesPath);
			    	String images[] = imagesDir.list();
			    	int detections = 0;
			    	for(int k=0; k<images.length; k++) {
			    		Mat image = Highgui.imread(imagesDir.getAbsolutePath() + "\\" + images[k]);
			    		
			    		// Convert to gray scale
			    		Mat gray = new Mat();
			    		Imgproc.cvtColor(image, gray, Imgproc.COLOR_RGB2GRAY);
			    		 
			    		PatternDetector faceDetector = new PatternDetector(gray, faceClassifier);
			    		MatOfRect faceDetections = faceDetector.Detection();
			    		//System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));
			    		
			    		for (Rect rect : faceDetections.toArray()) {
			    			   Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
			    			   detections++;
			    			   //Mat face = new Mat(image, rect);
			    			   //PatternDetector eyeDetector = new PatternDetector(face, eyeClassifier);
			    			   //MatOfRect eyeDetections = eyeDetector.Detection();
			    			   
			    			   //System.out.println(String.format("Detected %s eyes", eyeDetections.toArray().length));
			    			   //for (Rect rect2 : eyeDetections.toArray()) {
			    				   //Core.rectangle(image, new Point(rect.x + rect2.x, rect.y + rect2.y), new Point(rect.x + rect2.x + rect2.width, rect.y + rect2.y + rect2.height), new Scalar(255, 0, 0));
			    			   //}
			    			}
			    		
			    		// Save the visualized detection.
			    		String filename = faceCascadeClassifiers[i] + " - " + images[k] + ".png";
			    		//System.out.println(String.format("Writing %s", filename));
			    		Highgui.imwrite(filename, image);
			    		
			    		
			    		
			    	}
			    	long stopTime = System.currentTimeMillis();
			        long elapsedTime = (stopTime - startTime)*1000;
			    	System.out.println(String.format("Detected %d faces using %s in %d seconds", detections, faceCascadeClassifiers[i], elapsedTime));
			    //}
		    }
		    
		    
		  }

}
