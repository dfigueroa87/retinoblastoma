package controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import javax.imageio.ImageIO;

import org.opencv.core.Core;

import application.Detector;

public class mainController implements Initializable{
	
	@FXML
	private Button btnLoad;
	@FXML
	private Button btnDetect;
	
	private String path;
	
	private List<File> imageFiles = new ArrayList<File>();
	private List<Boolean> detected = new ArrayList<Boolean>();
			
	@FXML
	Parent root;
	
	@FXML
	Pane imageContainer ; // initialize to TilePane or FlowPane as desired  

	

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
		List<File> files = fileChooser.showOpenMultipleDialog(application.Main.stage);
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
		//imageContainer.getChildren().remove(file);
		//imageView.setImage(img);
			}
		i++;
		}
	}
	
	private ImageView createImageView(final File imageFile) throws FileNotFoundException {  
		final int DEFAULT_THUMBNAIL_WIDTH = 100;
	     final Image image = new Image(new FileInputStream(imageFile)) ;  
	     final ImageView imageView = new ImageView(image);  
	     imageView.setFitWidth(DEFAULT_THUMBNAIL_WIDTH);  
	     imageView.setPreserveRatio(true);  
	     imageView.setSmooth(true);  
	     return imageView ;  
	}  

}
