package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.opencv.core.Core;

import application.Detector;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
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
	
	private List<File> imageFiles = new ArrayList<File>();
	private List<Boolean> detected = new ArrayList<Boolean>();
			
	@FXML
	Parent root;
	
	@FXML
	TilePane imageContainer;

	@FXML
	ImageView imageView;

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
				     ImageView imageView;
					try {
						imageView = createImageView(file);
						imageContainer.getChildren().add(imageView);
						detected.add(false);
						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  
				     
				}  
		
		if (!imageFiles.isEmpty()) {

				path = imageFiles.get(0).getAbsolutePath();				

            } 
			

	}
	
	@FXML
	public void Detect() {
		int i = 0;
		for (File file : imageFiles) {  
			if (detected.get(i).equals(false)) {
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
				Detector det = new Detector();
				det.setImagePath(file.getAbsolutePath());
				Image img = det.detect();
				detected.set(i, true);
				ImageView iv = createImageViewFromImage(img);
				imageContainer.getChildren().set(i, iv);
			}
		i++;
		}
	}
	
	private ImageView createImageView(final File imageFile) throws FileNotFoundException {  
		final int DEFAULT_THUMBNAIL_WIDTH = 100;
	     Image image = new Image(new FileInputStream(imageFile)) ;  
	     ImageView imageView = new ImageView(image);  
	     imageView.setFitWidth(DEFAULT_THUMBNAIL_WIDTH);  
	     imageView.setPreserveRatio(true);  
	     imageView.setSmooth(true);  
	     return imageView ;  
	}  
	
	private ImageView createImageViewFromImage(Image im) {  
		final int DEFAULT_THUMBNAIL_WIDTH = 100; 
	     ImageView imageView = new ImageView(im);  
	     imageView.setFitWidth(DEFAULT_THUMBNAIL_WIDTH);  
	     imageView.setPreserveRatio(true);  
	     imageView.setSmooth(true);  
	     return imageView ;  
	}  
	
	@FXML
	public void clickImage(Event e){
		// Double click -> Display big image
		if(((MouseEvent) e).getButton().equals(MouseButton.PRIMARY)){
            if(((MouseEvent)e).getClickCount() == 2){
            	imageView.setImage(((ImageView) e.getTarget()).getImage());
            }
        }
		
		
	}
	
	@FXML
	public void Exit() {
		System.exit(0);
	}

}
