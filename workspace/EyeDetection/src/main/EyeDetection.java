package main;

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

//
//Detects faces in an image, draws boxes around them, and writes the results
//to "faceDetection.png".
//
class DetectFaceDemo {
public void run() {
System.out.println("\nRunning DetectFaceDemo");

// Create a face detector from the cascade file in the resources
// directory.
CascadeClassifier faceDetector = new CascadeClassifier("C:/retinoblastoma/workspace/Resources/CascadeClassifiers/FaceDetection/haarcascade_frontalface_alt.xml");
CascadeClassifier eyeDetector = new CascadeClassifier("C:/retinoblastoma/workspace/Resources/CascadeClassifiers/EyeDetection/haarcascade_eye.xml");

//CascadeClassifier profileFaceDetector = new CascadeClassifier("C:/retinoblastoma/workspace/FaceDetection/resources/lbpcascade_profileface.xml");
Mat image = Highgui.imread("C:/retinoblastoma/DataSet/Ejemplo6.jpg");
//Mat gray = new Mat();
//Imgproc.cvtColor(image, gray, Imgproc.COLOR_RGB2GRAY);

// Detect faces in the image.
// MatOfRect is a special container class for Rect.
MatOfRect faceDetections = new MatOfRect();
//faceDetector.detectMultiScale(image, faceDetections);
faceDetector.detectMultiScale(image, faceDetections);

System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

// Draw a bounding box around each face.
for (Rect rect : faceDetections.toArray()) {
   Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
   MatOfRect eyeDetections = new MatOfRect();
   Mat face = new Mat(image, rect);
   eyeDetector.detectMultiScale(face, eyeDetections);
   System.out.println(String.format("Detected %s eyes", eyeDetections.toArray().length));
   for (Rect rect2 : eyeDetections.toArray()) {
	   Core.rectangle(image, new Point(rect.x + rect2.x, rect.y + rect2.y), new Point(rect.x + rect2.x + rect2.width, rect.y + rect2.y + rect2.height), new Scalar(255, 0, 0));
	   
	   Mat eye = new Mat(image, rect2);
	   
	   // Invert eye
	   Mat invEye = new Mat(eye.rows(),eye.cols(), eye.type(), new Scalar(255,255,255));
	   Core.subtract(invEye, eye, eye);
	   
	   // To gray scale
	   Imgproc.cvtColor(eye, eye, Imgproc.COLOR_BGR2GRAY);
	   
	   // Convert to binary image by thresholding it
	   Imgproc.threshold(eye, eye, 220, 255, Imgproc.THRESH_BINARY);
	   
	   // Find all contours
	   List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	   Imgproc.findContours(eye, contours, new Mat(), Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_NONE);
	   
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
	           Core.circle(image, new Point(rect.x + rect2.x + rectangle.x + radius, rect.y + rect2.y + rectangle.y + radius), radius, new Scalar(255,0,0), 2);
	       }
	   }
   }
}

// Save the visualized detection.
String filename = "faceDetection.png";
System.out.println(String.format("Writing %s", filename));
Highgui.imwrite(filename, image);
}
}


public class EyeDetection {
	  public static void main(String[] args) {
		    System.out.println("Hello, OpenCV");

		    // Load the native library.
		    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		    new DetectFaceDemo().run();
		  }
}
