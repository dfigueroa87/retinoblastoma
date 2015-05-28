package controller;

import java.net.URL;
import java.util.ResourceBundle;

import model.detection.Detector;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import utils.Utils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;

public class ResultsController implements Initializable{
	
	@FXML
	TilePane minPane;
	@FXML
	ImageView bigEyeView;
	@FXML
	ImageView histogramView;
	
	private Detector detection;
	
	private ImageView createImageView(Image image) {  
		final int DEFAULT_THUMBNAIL_WIDTH = 100;
	     ImageView imageView = new ImageView(image);  
	     imageView.setFitWidth(DEFAULT_THUMBNAIL_WIDTH);  
	     imageView.setPreserveRatio(true);  
	     imageView.setSmooth(true);  
	     return imageView; 
	}  
	
	@FXML
	public void clickImage(Event e){
		// Double click -> Display big image
		if(((MouseEvent) e).getButton().equals(MouseButton.PRIMARY)){
            if(((MouseEvent)e).getClickCount() == 2){
            	bigEyeView.setImage(((ImageView) e.getTarget()).getImage());
            	Integer i = (Integer) ((ImageView) e.getTarget()).getUserData();
//            	histogramView.setImage(detection.getHistogram(i)); no hay mas histograma
            }
        }
		
		
	}
	
	public void setResults(Detector det) {
		int i = 0;
		detection = det;
//		for (Rect eye : det.getDetectedEyes()){
//			Mat img = new Mat(det.getOriginalMat(), eye);
//			ImageView imageView = createImageView(Utils.ConvertMatToImage(img));
//			imageView.setUserData(i);
//			minPane.getChildren().add(imageView);
//			i++;
//		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		
	}

}
