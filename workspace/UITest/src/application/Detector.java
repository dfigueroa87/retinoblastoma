package application;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class Detector {
	
	private String imagePath;
	
	CascadeClassifier faceDetector = new CascadeClassifier("C:/retinoblastoma/workspace/Resources/CascadeClassifiers/FaceDetection/haarcascade_frontalface_alt.xml");
	CascadeClassifier eyeDetector = new CascadeClassifier("C:/retinoblastoma/workspace/Resources/CascadeClassifiers/EyeDetection/haarcascade_eye.xml");

	double scaleFactor = 1.05;
	int minNeighbors = 1;
	int flags = org.opencv.objdetect.Objdetect.CASCADE_DO_CANNY_PRUNING;
	int minSizeRatio = 10;


	



	public Image detect() {
		Mat image = Highgui.imread(imagePath);

		Size minSize = new Size(image.size().width/minSizeRatio, image.size().height/minSizeRatio);
		Size maxSize = image.size();

		// Detect faces in the image.
		MatOfRect faceDetections = new MatOfRect();
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
			   
			   
			   // To gray scale
			   Imgproc.cvtColor(eye, transformedEye, Imgproc.COLOR_BGR2GRAY);
			   file = "Gray " + Double.toString(rand) + ".jpg";
			   Highgui.imwrite(file, transformedEye);
			   
			   Mat circles = new Mat();
			   
			   Imgproc.Canny(transformedEye, transformedEye, 50, 150);
			   file = "Canny " + Double.toString(rand) + ".jpg";
			   Highgui.imwrite(file, transformedEye);
			   
			   Imgproc.HoughCircles(transformedEye, circles, Imgproc.CV_HOUGH_GRADIENT, 1.3, transformedEye.rows()/1, 150, 30, 0, 0);
			   
			   // Draw the circles detected
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
				        Point pt2 = new Point(rect.x + rect2.x + Math.round(vCircle[0]), rect.y + rect2.y + Math.round(vCircle[1]));
				        Core.circle(image, pt2, radius, new Scalar(0,0,255), 2);
				        
				        file = "Final " + Double.toString(rand) + x + ".jpg";
						   Highgui.imwrite(file, eye);
				        }

			   rand++;
		   }
		}

		// Save the visualized detection.
		String filename = "faceDetection.png";
		System.out.println(String.format("Writing %s", filename));
		Highgui.imwrite(filename, image);
		
		Image im = null;
		
		try{
		MatOfByte bytemat = new MatOfByte();
		Highgui.imencode(".jpg", image, bytemat);
		byte[] bytes = bytemat.toArray();
		InputStream in = new ByteArrayInputStream(bytes);
		BufferedImage buffImg = ImageIO.read(in);
		im = SwingFXUtils.toFXImage(buffImg, null);
		
		}
		catch(Exception e){
			//TODO
		}
		return im;
		
	}

	public void setImagePath(String path) {
		imagePath = path;
	}

}
