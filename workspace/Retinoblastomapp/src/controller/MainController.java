package controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.opencv.core.Core;

import application.Detector;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MainController implements Initializable{
	
	@FXML
	private Button btnAbrir;
	@FXML
	private Button btnDetectar;
	
	private String path;
	
	private List<File> imageFiles = new ArrayList<File>();
	private List<Boolean> detected = new ArrayList<Boolean>();
	
	private Detector currentDetection;
			
	@FXML
	Parent root;
	
	@FXML
	TilePane imageContainer;

	@FXML
	ImageView imageView;
	
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
		//File selectedFile = fileChooser.showOpenDialog(application.Main.stage);
		List<File> files = fileChooser.showOpenMultipleDialog(null);
		imageFiles.addAll(files);
		for (File file : files) {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			Detector det = new Detector(file.getAbsolutePath());
			this.detections.add(det);
			ImageView imageView = createImageView(det.getOriginalImage());
			imageView.setUserData(det);
			imageContainer.getChildren().add(imageView);
			detected.add(false);

		}  
		
		if (!imageFiles.isEmpty()) {

				path = imageFiles.get(0).getAbsolutePath();				

            } 
			

	}
	
	@FXML
	public void Detect() {
		currentDetection.detect();
		
		

		try{
			Stage stage = new Stage();
			
			Parent root = null;
			FXMLLoader loader = new FXMLLoader();
			root = loader.load(getClass().getResource("/view/ResultsView.fxml"));
			
			//BorderPane root = new BorderPane();
			
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setTitle("Resultados");
			
			
			
			root = (Parent) loader.load(getClass().getResource("/view/ResultsView.fxml").openStream());
			ResultsController rc = loader.getController();
			rc.setResults(currentDetection);
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
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
