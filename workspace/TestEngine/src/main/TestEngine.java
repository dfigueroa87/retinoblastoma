package main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
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
	private static String logPath = "C:/retinoblastoma/workspace/TestEngine/results.txt";
	public static String faceCascadeClassifier = "C:/retinoblastoma/workspace/Resources/CascadeClassifiers/FaceDetection/haarcascade_frontalface_alt.xml";	
	
	  public static void main(String[] args) {

		    // Load the native library.
		    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		    Log log = new Log(logPath);
		    CascadeClassifier faceClassifier = new CascadeClassifier(faceCascadeClassifier);
		    
		    File eyeCascadeDir = new File(eyeCascadeClassifierPath);
		    String eyeCascadeClassifiers[] = eyeCascadeDir.list();
		    for(int i=0; i<eyeCascadeClassifiers.length; i++) {
		    	long startTime = System.currentTimeMillis();
		    	CascadeClassifier eyeClassifier = new CascadeClassifier(eyeCascadeDir.getAbsolutePath() + "\\" + eyeCascadeClassifiers[i]);
		    	//File eyeCascadeDir = new File(eyeCascadeClassifierPath);
			    //String eyeCascadeClassifiers[] = eyeCascadeDir.list();
			    //for(int j=0; j<eyeCascadeClassifiers.length; j++) {
			    	//CascadeClassifier eyeClassifier = new CascadeClassifier(eyeCascadeDir.getAbsolutePath() + "\\" + eyeCascadeClassifiers[j]);
			    	File imagesDir = new File(imagesPath);
			    	String images[] = imagesDir.list();
			    	int facesDetected = 0;
			    	int eyesDetected = 0;
			    	for(int k=0; k<images.length; k++) {
			    		Mat image = Highgui.imread(imagesDir.getAbsolutePath() + "\\" + images[k]);
			    		
			    		// Convert to gray scale
			    		//Mat gray = new Mat();
			    		//Imgproc.cvtColor(image, gray, Imgproc.COLOR_RGB2GRAY);
			    		// Give the image a standard brightness and contrast
			    		//Imgproc.equalizeHist(gray, gray);
			    		 
			    		PatternDetector faceDetector = new PatternDetector(image, faceClassifier);
			    		MatOfRect faceDetections = faceDetector.Detection();
			    		
			    		
			    		
			    		
			    		
			    		//System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));
			    		
			    		for (Rect rect : faceDetections.toArray()) {
			    			   Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
			    			   facesDetected++;
			    			   
			    			   
			    			   Rect boundedRect= new Rect(rect.x,rect.y,rect.width,(int)Math.floor(rect.height*0.60));
			    			   Mat topOfFace = new Mat(image, boundedRect);
			    			   
			    			   Mat topOfFaceGray = new Mat();
			    			   Imgproc.cvtColor(topOfFace, topOfFaceGray, Imgproc.COLOR_RGB2GRAY);
			    			   Imgproc.equalizeHist(topOfFaceGray, topOfFaceGray);
			    			   
			    			   PatternDetector eyeDetector = new PatternDetector(topOfFaceGray, eyeClassifier);
			    			   
			    			   
			    			   
			    			   MatOfRect eyeDetections = eyeDetector.Detection();
			    			   
			    			   if (i==0 && k==0) {
					    			log.Write("Eye detection configuration:");
					    			log.Write("\t Scale factor: " + String.valueOf(eyeDetector.GetScaleFactor()));
					    			log.Write("\t Min neighbors: " + String.valueOf(eyeDetector.GetMinNeighbors()));
					    			log.Write("\t Flags: " + String.valueOf(eyeDetector.GetFlags()));
					    			log.Write("\t Min Size: 1/" + String.valueOf(eyeDetector.GetMinSizeRatio()));
					    		}
			    			   
			    			   for (Rect rect2 : eyeDetections.toArray()) {
			    				   eyesDetected++;
			    				   Core.rectangle(image, new Point(rect.x + rect2.x, rect.y + rect2.y), new Point(rect.x + rect2.x + rect2.width, rect.y + rect2.y + rect2.height), new Scalar(255, 0, 0));
			    				   
			    				   Mat eye = new Mat(image, rect2);
			    				   
			    				   // Invert eye
			    				   Mat invEye = new Mat(eye.rows(),eye.cols(), eye.type(), new Scalar(255,255,255));
			    				   Core.subtract(invEye, eye, eye);
			    				   
			    				   // To gray scale
			    				   Imgproc.cvtColor(invEye, eye, Imgproc.COLOR_BGR2GRAY);
			    				   
			    				   // Convert to binary image by thresholding it
			    				   Imgproc.threshold(eye, eye, 220, 255, Imgproc.THRESH_BINARY);
			    				   
			    				   // Find all contours
			    				   List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			    				   Imgproc.findContours(eye.clone(), contours, new Mat(), Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_NONE);
			    				   
			    				   // Fill holes in each contour
			    				   Imgproc.drawContours(eye, contours, -1, new Scalar(255,255,255), -1);
			    				   
			    				   for (int n = 0; n < contours.size(); n++)
			    				   {
			    				       double area = Imgproc.contourArea(contours.get(n));    // Blob area
			    				       Rect rectangle = Imgproc.boundingRect(contours.get(n)); // Bounding box
			    				       int radius = rectangle.width/2;                     // Approximate radius

			    				       // Look for round shaped blob
			    				       if (area >= 30 && 
			    				           Math.abs(1 - ((double)rectangle.width / (double)rectangle.height)) <= 0.2 &&
			    				           Math.abs(1 - (area / (Math.PI * Math.pow(radius, 2)))) <= 0.2)    
			    				       {
			    				           Core.circle(image, new Point(rectangle.x + radius, rectangle.y + radius), radius, new Scalar(255,0,0), 2);
			    				       }
			    				   }
			    			   }
			    			   
			    			}
			    		
			    		// Save the visualized detection.
			    		String filename = eyeCascadeClassifiers[i] + " - " + images[k] + ".png";
			    		//System.out.println(String.format("Writing %s", filename));
			    		Highgui.imwrite(filename, image);
			    		
			    		
			    		
			    	}
			    	long stopTime = System.currentTimeMillis();
			        long elapsedTime = (stopTime - startTime)/1000;
			    	log.Write(String.format("Detected %d eyes in %d detected faces using %s in %d seconds", eyesDetected, facesDetected, eyeCascadeClassifiers[i], elapsedTime));
			    //}
		    }
		    log.Close();
		    
		    
		  }

}
