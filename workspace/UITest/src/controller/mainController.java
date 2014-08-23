package controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
	@FXML
	private ImageView imageView;
	
	private String path;
			
	@FXML
	Parent root;
	

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
		File selectedFile = fileChooser.showOpenDialog(application.Main.stage);
		
		if (selectedFile != null) {
			try {
				path = selectedFile.getAbsolutePath();				
                BufferedImage bufferedImage = ImageIO.read(selectedFile);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                imageView.setImage(image);
            } catch (IOException ex) {
                //TODO
            }
			
		}

	}
	
	@FXML
	public void Detect() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Detector det = new Detector();
		det.setImagePath(path);
		Image img = det.detect();
		imageView.setImage(img);
	}

}
