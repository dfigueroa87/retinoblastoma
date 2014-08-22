package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
}
