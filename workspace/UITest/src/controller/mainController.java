package controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class mainController implements Initializable{
	
	@FXML
	private Button btnLoad;
	@FXML
	private Button btnDetect;
	@FXML
	private ImageView imageView;
	
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
		fileChooser.getExtensionFilters().add(
		         new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
		         );
		File selectedFile = fileChooser.showOpenDialog(application.Main.stage);

		if (selectedFile != null) {
			try {
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
		
	}

}
