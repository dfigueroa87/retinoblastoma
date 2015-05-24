package model.processing;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ProcessingManagerImpl implements ProcessingManager {

	@Override
	public void CalculateColorsPercentage(Mat img,HistogramHSV histogram) {
		Mat imgHSV=new Mat();
		Imgproc.cvtColor(img, imgHSV, Imgproc.COLOR_BGR2HSV);
		
		for(int x=0; x<imgHSV.cols();x++){
	    	for(int y=0; y<imgHSV.rows();y++){
	    		double[] hsv=imgHSV.get(y, x);
	    		histogram.calculate(hsv[0], hsv[1], hsv[2]);	    		
	    	}
		}
		
		
//		HashMap<String, Double> colors = new HashMap<String, Double>(); 
//		Mat imgHSV=new Mat();
//		Imgproc.cvtColor(img, imgHSV, Imgproc.COLOR_BGR2HSV);
//				
//		
//		int total= imgHSV.cols()*imgHSV.rows();
//		int white = 0;
//		int black = 0;
//		int red = 0;
//		int yellow = 0;
//		int green = 0;		
//		 
//		for(int x=0; x<imgHSV.cols();x++){
//	    	for(int y=0; y<imgHSV.rows();y++){
//	    		double[] hsv=imgHSV.get(y, x);	    		
//	    		double h=hsv[0];
//	    		double s=hsv[1];
//	    		double v=hsv[2];
//	    		//first we check if its a white color
//	    		if(v>229.5 && s<56){
//	    			white++;
//	    		}else{
//	    			//black
//	    			if(v<64.0){
//	    				black++;	    				
//	    			}else{
//	    				//red
//	    				if(h<10.0 || h>166.5 && s>127) red++;
//	    				//yellow
//	    				if(h>19.0 && h<31.0 && s>127) yellow++;
//	    				//green
//	    				if(h<31.0 && h>68.0 && s>127) green++;
//	    			}
//	    			
//	    		}
//	    	}
//	    }
//		Double whitePercent=(double)white/(double)total;
//		Double blackPercent=(double)black/(double)total;
//		Double redPercent=(double)red/(double)total;
//		Double yellowPercent=(double)yellow/(double)total;
//		Double greenPercent=(double)green/(double)total;
//		Double othersPercent= 1.0 - (whitePercent+blackPercent+redPercent+yellowPercent+greenPercent);
//			
//		colors.put("white", whitePercent);
//		colors.put("black", blackPercent);
//		colors.put("red", redPercent);
//		colors.put("yellow", yellowPercent);
//		colors.put("green", greenPercent);
//		colors.put("others", othersPercent);
//		
////		java.util.List<Mat> hsvPlanes = new LinkedList<Mat>();
////	    Core.split(img, hsvPlanes);
//
////	    MatOfInt histSize = new MatOfInt(256);
////
////	    final MatOfFloat histRange = new MatOfFloat(0f, 256f);
////
////	    boolean accumulate = false;
////
////	    Mat b_hist = new  Mat();
////	    
////	    int hist_w = 512;
////	    int hist_h = 256;
////	    long bin_w;
////	    bin_w = Math.round((double) (hist_w / 256));
////
////	    Mat histImage = new Mat(hist_h, hist_w, CvType.CV_8UC3, new Scalar(0,0,0));
//	   
//	    
//	    
////	    for (int c=0;c<3;c++){
////		    Imgproc.calcHist(hsvPlanes, new MatOfInt(c),new Mat(), b_hist, histSize, histRange, accumulate);
////		    Core.normalize(b_hist, b_hist, 3, histImage.rows(), Core.NORM_MINMAX);
////		    for (int i = 1; i < 256; i++) {
////		        Core.line(histImage, new Point(bin_w * (i - 1),hist_h- Math.round(b_hist.get( i-1,0)[0])), 
////		                new Point(bin_w * (i), hist_h-Math.round(Math.round(b_hist.get(i, 0)[0]))),
////		                mColorsBGR[c], 2, 8, 0);
////		    }
////	    }
//
//		pupilsColor.add(colors);
//	    
	}

}
