package application;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import utils.Utils;

public class Detector {
	
	private String imagePath;
	private Mat originalImage;
	
	private CascadeClassifier faceDetector = new CascadeClassifier("C:/retinoblastoma/workspace/Resources/CascadeClassifiers/FaceDetection/haarcascade_frontalface_alt.xml");
	private CascadeClassifier eyeDetector = new CascadeClassifier("C:/retinoblastoma/workspace/Resources/CascadeClassifiers/EyeDetection/haarcascade_eye.xml");

	private double scaleFactor = 1.05;
	private int minNeighbors = 1;
	private int flags = org.opencv.objdetect.Objdetect.CASCADE_DO_CANNY_PRUNING;
	private int minSizeRatio = 10;
	
	private Scalar mColorsBGR[] = new Scalar[] { new Scalar(255, 0, 0), new Scalar(0, 255, 0), new Scalar(0, 0, 255) };
	
	private ArrayList<Mat> detectedFaces = new ArrayList<Mat>();
	private ArrayList<Mat> detectedEyes = new ArrayList<Mat>();
	private ArrayList<Mat> detectedPupils = new ArrayList<Mat>();
	private ArrayList<Mat> pupilHistograms = new ArrayList<Mat>();
	
	private int facesDetected = 0;
	private int eyesDetected = 0;
	private int pupilsDetected = 0;
	
	private ArrayList<Integer> eyesPerFace = new ArrayList<Integer>();
	private ArrayList<Integer> pupilsPerEye = new ArrayList<Integer>();
	
	public Detector(String path) {
		imagePath = path;
		originalImage = Highgui.imread(imagePath);
	}
	
	public Image getOriginalImage() {
		return Utils.ConvertMatToImage(originalImage);
	}
	
	public Image getHistogram(int i) {
		return Utils.ConvertMatToImage(pupilHistograms.get(i));
	}
	
	public ArrayList<Mat> getDetectedEyes() {
		return detectedEyes;
	}
	
	public void setFaceClassifier(String path) {
		faceDetector = new CascadeClassifier(path);
	}
	
	public void setEyeClassifier(String path) {
		eyeDetector = new CascadeClassifier(path);
	}
	
	public void setScaleFactor(double value) {
		scaleFactor = value;
	}
	
	public double getScaleFactor() {
		return scaleFactor;
	}
	
	public void setMinNeighbors(int value) {
		minNeighbors = value;
	}
	
	public int getMinNeighbors() {
		return minNeighbors;
	}
	
	public void setFlags(int value) {
		flags = value;
	}
	
	public int getFlags() {
		return flags;
	}
	
	public void setMinSizeRatio(int value) {
		minSizeRatio = value;
	}
	
	public int getMinSizeRatio() {
		return minSizeRatio;
	}

	public Image detect() {
		Mat image = originalImage.clone();

		Size minSize = new Size(image.size().width/minSizeRatio, image.size().height/minSizeRatio);
		Size maxSize = image.size();

		// Detect faces in the image.
		MatOfRect faceDetections = new MatOfRect();
		faceDetector.detectMultiScale(image, faceDetections, scaleFactor, minNeighbors, flags, minSize, maxSize);
		
		facesDetected = faceDetections.toArray().length;

		System.out.println(String.format("Detected %s faces", facesDetected));

		// Draw a bounding box around each face.
		for (Rect rect : faceDetections.toArray()) {
		   Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
		   detectedFaces.add(new Mat(image, rect));
		   
		   MatOfRect eyeDetections = new MatOfRect();
		   
		   Rect boundedRect= new Rect(rect.x,rect.y,rect.width,(int)Math.floor(rect.height*0.60));
		   Mat topOfFace = new Mat(image, boundedRect);
		   
		   minSize = new Size(topOfFace.size().width/minSizeRatio, topOfFace.size().height/minSizeRatio);
		   maxSize = topOfFace.size();
		   eyeDetector.detectMultiScale(topOfFace, eyeDetections, scaleFactor, minNeighbors, flags, minSize, maxSize);
		   
		   eyesPerFace.add(eyeDetections.toArray().length);
		   eyesDetected += eyeDetections.toArray().length;
		   System.out.println(String.format("Detected %s eyes", eyeDetections.toArray().length));
		   
		   // Each detected eye
		   for (Rect rect2 : eyeDetections.toArray()) {
			   Core.rectangle(image, new Point(rect.x + rect2.x, rect.y + rect2.y), new Point(rect.x + rect2.x + rect2.width, rect.y + rect2.y + rect2.height), new Scalar(255, 0, 0));
			   
			   Rect roi = new Rect(new Point(rect.x + rect2.x, rect.y + rect2.y), new Point(rect.x + rect2.x + rect2.width, rect.y + rect2.y + rect2.height));
			   
			   Mat eye = new Mat(image.clone(), roi);
			   
			   detectedEyes.add(eye.clone());
			   Mat transformedEye = new Mat();
			   
			   
			   // To gray scale
			   Imgproc.cvtColor(eye, transformedEye, Imgproc.COLOR_BGR2GRAY);
			   
			   Mat circles = new Mat();
			   
			   Imgproc.Canny(transformedEye, transformedEye, 50, 150);
			   
			   Imgproc.HoughCircles(transformedEye, circles, Imgproc.CV_HOUGH_GRADIENT, 1.3, transformedEye.rows()/1, 150, 30, 0, 0);
			   
			   pupilsPerEye.add(circles.cols());
			   pupilsDetected += circles.cols();
			   
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
				        
				        CalculateHistogram(eye);
				        

				        }
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
	
	public void CalculateHistogram(Mat img) {
		//Calculate histogram
		java.util.List<Mat> bgrPlanes = new LinkedList<Mat>();

	    Core.split(img, bgrPlanes);

	    MatOfInt histSize = new MatOfInt(256);

	    final MatOfFloat histRange = new MatOfFloat(0f, 256f);

	    boolean accumulate = false;

	    Mat b_hist = new  Mat();
	    
	    int hist_w = 512;
	    int hist_h = 256;
	    long bin_w;
	    bin_w = Math.round((double) (hist_w / 256));

	    Mat histImage = new Mat(hist_h, hist_w, CvType.CV_8UC3, new Scalar(0,0,0));
	    
	    for (int c=0;c<3;c++){

	    Imgproc.calcHist(bgrPlanes, new MatOfInt(c),new Mat(), b_hist, histSize, histRange, accumulate);

	    Core.normalize(b_hist, b_hist, 3, histImage.rows(), Core.NORM_MINMAX);

	    for (int i = 1; i < 256; i++) {         


	        Core.line(histImage, new Point(bin_w * (i - 1),hist_h- Math.round(b_hist.get( i-1,0)[0])), 
	                new Point(bin_w * (i), hist_h-Math.round(Math.round(b_hist.get(i, 0)[0]))),
	                mColorsBGR[c], 2, 8, 0);

	    }
	    
	    }

	    pupilHistograms.add(histImage);
		
	}

	public void setImagePath(String path) {
		imagePath = path;
	}

}
