package application;
	
import java.io.IOException;

import org.opencv.core.Core;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
 
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    public static Stage stage;
    
    @Override
    public void start(Stage primaryStage) {
        


        
        try {
        	
        	stage = primaryStage;
			Parent root = FXMLLoader.load(getClass().getResource("/view/MainView.fxml"));
			
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			primaryStage.setTitle("Retinoblastomapp 0.0");
			primaryStage.setScene(scene);
	        primaryStage.show();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

        
    }

	
	public void DoMagic() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		new DetectFaceDemo().run();
		
	}
}
