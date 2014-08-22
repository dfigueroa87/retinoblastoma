package application;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
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

double scaleFactor = 1.05;
int minNeighbors = 1;
int flags = org.opencv.objdetect.Objdetect.CASCADE_DO_CANNY_PRUNING;
int minSizeRatio = 10;


//CascadeClassifier profileFaceDetector = new CascadeClassifier("C:/retinoblastoma/workspace/FaceDetection/resources/lbpcascade_profileface.xml");
Mat image = Highgui.imread("C:/retinoblastoma/DataSet/Ejemplo6.jpg");
//Mat gray = new Mat();
//Imgproc.cvtColor(image, gray, Imgproc.COLOR_RGB2GRAY);

Size minSize = new Size(image.size().width/minSizeRatio, image.size().height/minSizeRatio);
Size maxSize = image.size();

// Detect faces in the image.
// MatOfRect is a special container class for Rect.
MatOfRect faceDetections = new MatOfRect();
//faceDetector.detectMultiScale(image, faceDetections);
faceDetector.detectMultiScale(image, faceDetections, scaleFactor, minNeighbors, flags, minSize, maxSize);

System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

// Draw a bounding box around each face.
for (Rect rect : faceDetections.toArray()) {
   Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
   MatOfRect eyeDetections = new MatOfRect();
   
   Rect boundedRect= new Rect(rect.x,rect.y,rect.width,(int)Math.floor(rect.height*0.60));
   Mat topOfFace = new Mat(image, boundedRect);
   
   minSize = new Size(topOfFace.size().width/minSizeRatio, topOfFace.size().height/minSizeRatio);
   maxSize = topOfFace.size();
   eyeDetector.detectMultiScale(topOfFace, eyeDetections, scaleFactor, minNeighbors, flags, minSize, maxSize);
   System.out.println(String.format("Detected %s eyes", eyeDetections.toArray().length));
   double rand = 1;
   for (Rect rect2 : eyeDetections.toArray()) {
	   Core.rectangle(image, new Point(rect.x + rect2.x, rect.y + rect2.y), new Point(rect.x + rect2.x + rect2.width, rect.y + rect2.y + rect2.height), new Scalar(255, 0, 0));
	   
	   Rect roi = new Rect(new Point(rect.x + rect2.x, rect.y + rect2.y), new Point(rect.x + rect2.x + rect2.width, rect.y + rect2.y + rect2.height));
	   
	   Mat eye = new Mat(image.clone(), roi);
	   Mat transformedEye = new Mat();
	   
	   String file = "Eye " + Double.toString(rand) + ".jpg";
	   Highgui.imwrite(file, eye);
	   
	   // Invert eye
	   //Mat invEye = new Mat(eye.rows(),eye.cols(), eye.type(), new Scalar(255,255,255));
	   //Core.subtract(invEye, eye, transformedEye);
	   
	   //Double rand = Math.random();
	   
	   //file = "Inverted " + Double.toString(rand) + ".jpg";
	   //Highgui.imwrite(file, transformedEye);
	   
	   // To gray scale
	   Imgproc.cvtColor(eye, transformedEye, Imgproc.COLOR_BGR2GRAY);
	   file = "Gray " + Double.toString(rand) + ".jpg";
	   Highgui.imwrite(file, transformedEye);
	   
	   // Reduce the noise to avoid false circle detection
	   //Imgproc.GaussianBlur(transformedEye, transformedEye, new Size(9, 9), 1, 1);
	   //file = "Blur " + Double.toString(rand) + ".jpg";
	   //Highgui.imwrite(file, transformedEye);
	   
	   Mat circles = new Mat();
	   
	   Imgproc.Canny(transformedEye, transformedEye, 50, 150);
	   file = "Canny " + Double.toString(rand) + ".jpg";
	   Highgui.imwrite(file, transformedEye);
	   
	   Imgproc.HoughCircles(transformedEye, circles, Imgproc.CV_HOUGH_GRADIENT, 1.3, transformedEye.rows()/1, 150, 30, 0, 0);
	   
	 /// Draw the circles detected
	   if (circles.cols() > 0)
		    for (int x = 0; x < circles.cols(); x++) 
		        {
		    	System.out.println("Detected circle");
		        double vCircle[] = circles.get(0,x);

		        if (vCircle == null)
		            break;

		        Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
		        int radius = (int)Math.round(vCircle[2]);

		        // draw the found circle
		        Core.circle(eye, pt, radius, new Scalar(0,255,0), 2);
		        //Core.circle(eye, pt, 3, new Scalar(0,0,255), 3, 8, 0);
		        
		        file = "Final " + Double.toString(rand) + x + ".jpg";
				   Highgui.imwrite(file, eye);
		        }
	       

	   
	   
	   rand++;
	   /*// Convert to binary image by thresholding it
	   Imgproc.threshold(eye, eye, 220, 255, Imgproc.THRESH_BINARY);
	   file = "Threshold " + Double.toString(rand) + ".jpg";
	   Highgui.imwrite(file, eye);
	   
	   // Find all contours
	   List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	   Imgproc.findContours(eye, contours, new Mat(), Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_NONE);
	   
	   // Fill holes in each contour
	   Imgproc.drawContours(eye, contours, -1, new Scalar(255,255,255), -1);
	   file = "Contours " + Double.toString(rand) + ".jpg";
	   Highgui.imwrite(file, eye);
	   
	   for (int n = 0; n < contours.size(); n++)
	   {
	       double area = Imgproc.contourArea(contours.get(n));    // Blob area
	       Rect rectangle = Imgproc.boundingRect(contours.get(n)); // Bounding box
	       int radius = rectangle.width/2;                     // Approximate radius

	       // Look for round shaped blob
	       if (area >= 50 && 
	           //Math.abs(1 - ((double)rectangle.width / (double)rectangle.height)) <= 0.3 &&
	           Math.abs(1 - (area / (Math.PI * Math.pow(radius, 2)))) <= 0.3)    
	       {
	    	   System.out.println("Pupil found");
	           Core.circle(image, new Point(rect.x + rect2.x + rectangle.x + radius, rect.y + rect2.y + rectangle.y + radius), radius, new Scalar(255,0,0), 2);
	       }
	   }*/

	   
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
