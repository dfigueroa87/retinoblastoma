package main;

import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class Test1 {
	
	public static String imagesPath = "C:/retinoblastoma/DataSet";
	
	float EYE_SX = 0.16f;
	float EYE_SY = 0.26f;
	float EYE_SW = 0.30f;
	float EYE_SH = 0.28f;
	
	public static void main(String[] args) {
		System.out.println("Hello, Carlito");

		// Load the native library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Test1 t1=new Test1();
		t1.run();
		System.out.println("Fin.");
	}

	public void run() {

		// Create a face detector from the cascade file in the resources
		// directory.
		CascadeClassifier faceDetector = new CascadeClassifier(
				"C:/retinoblastoma/workspace/Resources/CascadeClassifiers/FaceDetection/haarcascade_frontalface_alt.xml");
		CascadeClassifier eyeDetector_eye = new CascadeClassifier(
				"C:/retinoblastoma/workspace/Resources/CascadeClassifiers/EyeDetection/haarcascade_eye.xml");
		CascadeClassifier eyeDetector_eyeglasses = new CascadeClassifier(
				"C:/retinoblastoma/workspace/Resources/CascadeClassifiers/EyeDetection/haarcascade_eye_tree_eyeglasses.xml");
		CascadeClassifier eyeDetector_big = new CascadeClassifier(
				"C:/retinoblastoma/workspace/Resources/CascadeClassifiers/EyeDetection/haarcascade_mcs_eyepair_big.xml");
		CascadeClassifier eyeDetector_small = new CascadeClassifier(
				"C:/retinoblastoma/workspace/Resources/CascadeClassifiers/EyeDetection/haarcascade_mcs_eyepair_small.xml");
		

		
		File imagesDir = new File(imagesPath);
    	String images[] = imagesDir.list();
    	
    	for(int k=0; k<images.length; k++) {
    		Mat image = Highgui.imread(imagesDir.getAbsolutePath() + "\\" + images[k]);
    		
    		// Convert to gray scale
    		
    		
    		 
    		// Detect faces in the image.
    		// MatOfRect is a special container class for Rect.
    		MatOfRect faceDetections = new MatOfRect();
    		// faceDetector.detectMultiScale(image, faceDetections);
    		faceDetector.detectMultiScale(image, faceDetections,1.05,1,1,new Size(image.size().width/10,image.size().height/10),image.size());
    		
    		System.out.println(String.format("Detected %s faces",
    				faceDetections.toArray().length));
    		
    		// Draw a bounding box around each face.
    		for (Rect rect : faceDetections.toArray()) {
    			Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x
    					+ rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
    			
    			MatOfRect eyeDetections = new MatOfRect();
    			
    			Rect boundedRect= new Rect(rect.x,rect.y,rect.width,(int)Math.floor(rect.height*0.60));
    			Mat topOfFace = new Mat(image, boundedRect);
    			
    			
    			
    			
    			//nuevo ints con respecto a la cara.
//    			int leftX = (int)Math.round(face.cols() * EYE_SX);
//    			int topY = (int)Math.round(face.rows() * EYE_SY);
//    			int widthX = (int)Math.round(face.cols() * EYE_SW);
//    			int heightY = (int)Math.round(face.rows() * EYE_SH);
//    			int rightX = (int)Math.round(face.cols() * (1.0-EYE_SX-EYE_SW));
    			
    			//Esto deberia suplantar esto:
//    			Mat topLeftOfFace = face(Rect(leftX, topY, widthX, heightY));
//    			Mat topRightOfFace = face(Rect(rightX, topY, widthX, heightY));
    			    			
//    			Mat topRightOfFace = face.submat(new Rect(rightX, topY, widthX, heightY));
    			    			
    			eyeDetector_eye.detectMultiScale(topOfFace, eyeDetections);
    			
    			
    			
    			System.out.println(String.format("Detected %s  eyes",
    					eyeDetections.toArray().length));
    			
    			
    			for (Rect rect2 : eyeDetections.toArray()) {
    				   Core.rectangle(image, new Point(rect.x + rect2.x, rect.y + rect2.y), new Point(rect.x + rect2.x + rect2.width, rect.y + rect2.y + rect2.height), new Scalar(255, 0, 0));
    			   }
    			
    			
    		}  		
    		
    		// Save the visualized detection.
    		String filename = images[k] + ".png";
    		//System.out.println(String.format("Writing %s", filename));
    		Highgui.imwrite(filename, image);
    		    		
    	}	
	}

	
}


//int main()
//{
//	float EYE_SX = 0.16f;
//	float EYE_SY = 0.26f;
//	float EYE_SW = 0.30f;
//	float EYE_SH = 0.28f;
 
//	Mat dest, gray;
//	Mat imagen = imread("images.jpg");
 
//	CascadeClassifier detector, eyes_detector;
// 
//	if(!detector.load("haarcascade_frontalface_alt2.xml")) 
//		cout << "No se puede abrir clasificador." << endl;
// 
//	if(!eyes_detector.load("haarcascade_eye_tree_eyeglasses.xml"))
//		cout << "No se puede abrir clasificador para los ojos." << endl;
 
//	cvtColor(imagen, gray, CV_BGR2GRAY);
//	equalizeHist(gray, dest);
 
//	vector<Rect> rect;
//	detector.detectMultiScale(dest, rect);
 
//	for(Rect rc : rect)
//	{
//		rectangle(imagen, 
//			Point(rc.x, rc.y), 
//			Point(rc.x + rc.width, rc.y + rc.height), 
//			CV_RGB(0,255,0), 2);
//	}
 
//	if(rect.size() > 0)
//	{
//		Mat face = dest(rect[0]).clone();
//		vector<Rect> leftEye, rightEye;
 
//		int leftX = cvRound(face.cols * EYE_SX);
//		int topY = cvRound(face.rows * EYE_SY);
//		int widthX = cvRound(face.cols * EYE_SW);
//		int heightY = cvRound(face.rows * EYE_SH);
//		int rightX = cvRound(face.cols * (1.0-EYE_SX-EYE_SW));
 
//		Mat topLeftOfFace = face(Rect(leftX, topY, widthX, heightY));
//		Mat topRightOfFace = face(Rect(rightX, topY, widthX, heightY));
 
//		eyes_detector.detectMultiScale(topLeftOfFace, leftEye);
//		eyes_detector.detectMultiScale(topRightOfFace, rightEye);
// 
//		if((int)leftEye.size() > 0)
//		{
//			rectangle(imagen, 
//				Point(leftEye[0].x + leftX + rect[0].x, leftEye[0].y + topY + rect[0].y), 
//				Point(leftEye[0].width + widthX + rect[0].x - 5, leftEye[0].height + heightY + rect[0].y), 
//				CV_RGB(0,255,255), 2);
//		}
// 
//		if((int)rightEye.size() > 0)
//		{
//			rectangle(imagen, 
//				Point(rightEye[0].x + rightX + leftX + rect[0].x, rightEye[0].y + topY + rect[0].y), 
//				Point(rightEye[0].width + widthX + rect[0].x + 5, rightEye[0].height + heightY + rect[0].y), 
//				CV_RGB(0,255,255), 2);
//		} 
//	}
 
//	imshow("Ojos", imagen);
// 
//	waitKey(0);
//	return 1;
//}