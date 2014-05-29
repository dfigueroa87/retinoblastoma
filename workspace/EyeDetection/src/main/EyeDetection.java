package main;

import org.opencv.core.Core;
import org.opencv.core.Mat;
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
CascadeClassifier faceDetector = new CascadeClassifier("C:/retinoblastoma/workspace/FaceDetection/resources/haarcascade_frontalface_default.xml");
CascadeClassifier eyeDetector = new CascadeClassifier("C:/retinoblastoma/workspace/FaceDetection/resources/haarcascade_eye.xml");

//CascadeClassifier profileFaceDetector = new CascadeClassifier("C:/retinoblastoma/workspace/FaceDetection/resources/lbpcascade_profileface.xml");
Mat image = Highgui.imread("C:/retinoblastoma/DataSet/ejemplo7.jpg");
Mat gray = new Mat();
Imgproc.cvtColor(image, gray, Imgproc.COLOR_RGB2GRAY);

// Detect faces in the image.
// MatOfRect is a special container class for Rect.
MatOfRect faceDetections = new MatOfRect();
//faceDetector.detectMultiScale(image, faceDetections);
faceDetector.detectMultiScale(gray, faceDetections);

System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

if (faceDetections.toArray().length == 0){
	MatOfRect eyeDetections = new MatOfRect();
	eyeDetector.detectMultiScale(gray, eyeDetections);
	System.out.println(String.format("Detected %s eyes", eyeDetections.toArray().length));
	for (Rect rect2 : eyeDetections.toArray()) {
	Core.rectangle(image, new Point(rect2.x,rect2.y), new Point(rect2.x + rect2.width, rect2.y + rect2.height), new Scalar(255, 0, 0));
	}
	
}

// Draw a bounding box around each face.
for (Rect rect : faceDetections.toArray()) {
   Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
   MatOfRect eyeDetections = new MatOfRect();
   Mat face = new Mat(gray, rect);
   eyeDetector.detectMultiScale(face, eyeDetections);
   System.out.println(String.format("Detected %s eyes", eyeDetections.toArray().length));
   for (Rect rect2 : eyeDetections.toArray()) {
	   Core.rectangle(image, new Point(rect.x + rect2.x, rect.y + rect2.y), new Point(rect.x + rect2.x + rect2.width, rect.y + rect2.y + rect2.height), new Scalar(255, 0, 0));
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
