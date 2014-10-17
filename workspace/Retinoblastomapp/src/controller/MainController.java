package controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import utils.Utils;
import application.Detector;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainController implements Initializable{
	
	@FXML
	private Button btnAbrir;
	@FXML
	private Button btnDetectar;
	
	private String path;
	
	private Detector currentDetection;
			
	@FXML
	Parent root;
	
	@FXML
	TilePane imageContainer;
	@FXML
	TilePane resultsMinPane;

	@FXML
	ImageView imageView;
	@FXML
	ImageView resultImageView;
	@FXML
	ImageView histogramView;
	@FXML
	TableView<HashMap<String,Double>> tableColorsPercentage;
	@FXML
	TableColumn<Double, Double> tableColumnWhite;
	
	private ArrayList<Detector> detections = new ArrayList<Detector>();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@FXML
	public void LoadImage() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Abrir imagen");
		if(path!=null){
			File file= new File(path.substring(0,path.lastIndexOf("\\")));
			fileChooser.setInitialDirectory(file);		
		}
		fileChooser.getExtensionFilters().add(
		         new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
		         );
		List<File> files = fileChooser.showOpenMultipleDialog(null);
		for (File file : files) {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			Detector det = new Detector(file.getAbsolutePath());
			this.detections.add(det);
			ImageView imageView = createImageView(det.getOriginalImage());
			imageView.setUserData(det);
			imageContainer.getChildren().add(imageView);
		}  
		
		if (!files.isEmpty()) {
			path = files.get(0).getAbsolutePath();
		}

	}
	
	@FXML
	public void Detect() {
		imageView.setImage(currentDetection.detect());
		setResults(currentDetection);
//		try{
//			Stage stage = new Stage();
//			
//			Parent root = null;
//			FXMLLoader loader = new FXMLLoader();
//			root = FXMLLoader.load(getClass().getResource("/view/ResultsView.fxml"));
//			stage.setTitle("Resultados");
//			
//			root = (Parent) loader.load(getClass().getResource("/view/ResultsView.fxml").openStream());
//			ResultsController rc = loader.getController();
//			rc.setResults(currentDetection);
//			Scene scene = new Scene(root);
//			stage.setScene(scene);
//			stage.show();
//			
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
	}
	
	public void setResults(Detector det) {
		int i = 0;
		for (Rect eye : det.getDetectedEyes()){
			Mat img = new Mat(det.getOriginalMat(), eye);
			ImageView imageView = createImageView(Utils.ConvertMatToImage(img));
			imageView.setUserData(i);
			resultsMinPane.getChildren().add(imageView);
			i++;
		}
	}
	
	public void drawDetections(Detector det) {
		
	}
	
	@FXML
	public void clickedResultImage(Event e){
		// Double click -> Display big image
		if(((MouseEvent) e).getButton().equals(MouseButton.PRIMARY)){
            if(((MouseEvent)e).getClickCount() == 2){
            	resultImageView.setImage(((ImageView) e.getTarget()).getImage());
            	Integer i = (Integer) ((ImageView) e.getTarget()).getUserData();            	
            	System.out.println(currentDetection.getPupilColorsPercentage(i));
//            	histogramView.setImage(currentDetection.getHistogram(i)); voy a ver que devuelvo
            	
            	
            }
        }
		
		
	}
	
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
            	imageView.setImage(((ImageView) e.getTarget()).getImage());
            	currentDetection = (Detector) ((ImageView) e.getTarget()).getUserData();
            }
        }
	}
	
	@FXML
	public void Exit() {
		System.exit(0);
	}

}
