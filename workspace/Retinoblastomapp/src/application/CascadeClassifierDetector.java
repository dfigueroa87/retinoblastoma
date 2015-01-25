/**
 * 
 */
package application;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;

/**
 * @author David
 *
 */
public class CascadeClassifierDetector extends Detector {
	
	private CascadeClassifier classifier;
	
	private double scaleFactor = 1.05;
	private int minNeighbors = 1;
	private int flags = org.opencv.objdetect.Objdetect.CASCADE_DO_CANNY_PRUNING;
	private int minSizeRatio = 10;
	
	private ArrayList<Rect> detections = new ArrayList<Rect>();

	public CascadeClassifierDetector(String classifierPath) {
		super();
		classifier = new CascadeClassifier(classifierPath);
	}
	
	public void setClassifier(String path) {
		classifier = new CascadeClassifier(path);
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
	
	public ArrayList<Detection> detect(Mat image) {
		
		ArrayList<Detection> detections = new ArrayList<Detection>();

		Size minSize = new Size(image.size().width/minSizeRatio, image.size().height/minSizeRatio);
		Size maxSize = image.size();

		// Detect faces in the image.
		MatOfRect detectionsMat = new MatOfRect();
		classifier.detectMultiScale(image, detectionsMat, scaleFactor, minNeighbors, flags, minSize, maxSize);

		System.out.println(String.format("%s detections", detectionsMat.toArray().length));

		for (Rect rect : detectionsMat.toArray()) {
		   //Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
		   detections.add(new RectDetection(rect));
		}
		
		return detections;
	}

}
