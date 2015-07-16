package controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import model.detection.CircleDetection;
import model.detection.Detection;
import model.detection.DetectionManager;
import model.detection.DetectionManagerImpl;
import model.detection.Detector;
import model.detection.RectDetection;
import model.processing.ColorHSL;
import model.processing.ColorHSV;
import model.processing.HistogramHSL;
import model.processing.HistogramHSV;
import model.processing.ProcessingManager;
import model.processing.ProcessingManagerImpl;
import model.processing.Rank;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;

import utils.Utils;

public class MainController implements Initializable{
	
	@FXML
	private Button btnAbrir;
	@FXML
	private Button btnDetectar;
	
	private String path;
	
	private DetectionManager detMan = new DetectionManagerImpl();
	private ProcessingManager procMan = new ProcessingManagerImpl();
	private Mat detectionMat;
	private boolean detected = false;
	
	private ImageView selectedMinView;
	
	@FXML
	Parent root;
	
	@FXML
	TilePane imageContainer;
	@FXML
	TilePane facesMinPane;
	@FXML
	TilePane eyesMinPane;

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
	
	@FXML
	ToggleButton mainToggleBtn;

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
			ImageView imageView = createImageView(file.getAbsolutePath());
			imageView.getProperties().put("path", file.getAbsolutePath());
			imageContainer.getChildren().add(imageView);
			//Add listener to mark the selected image
			DropShadow ds = new DropShadow(20, Color.BLUE);
			imageView.focusedProperty().addListener(( ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue ) -> {
		        if (newValue) {
		        	imageView.setEffect( ds );
		        } else {
		        	imageView.setEffect( null );
		        }
		    });
		}  
		
		//Save the path for the next file chooser dialog
		if (!files.isEmpty()) {
			path = files.get(0).getAbsolutePath();
		}
	}
	
	@FXML
	/**
	 * Open image when its min view is clicked
	 * @param e
	 */
	public void clickImage(Event e){
		if(((MouseEvent) e).getButton().equals(MouseButton.PRIMARY)) {
			//Clear result panes
			clearResults();
			
			selectedMinView = (ImageView) e.getTarget();
			selectedMinView.requestFocus();

			//Clear properties data from global imageView
			imageView.getProperties().clear();
			
            imageView.setImage(selectedMinView.getImage());
            imageView.getProperties().putAll(selectedMinView.getProperties());
            
            //If there are detections saved for the image, display them
			@SuppressWarnings("unchecked")
			ArrayList<Detection> faces = (ArrayList<Detection>) imageView.getProperties().get("detection");
            if (faces != null) {
            	setFaceResults(faces);
            }
        }
	}
	
	@FXML
	public void Detect() {
		//Clear previous results
		clearResults();
		
		detectionMat = detMan.detect(Highgui.imread((String) imageView.getProperties().get("path")));
		//Save images as properties
		imageView.getProperties().put("original", imageView.getImage());
		imageView.getProperties().put("detectionImage", Utils.ConvertMatToImage(detectionMat));
		//Set image with detections
		imageView.setImage(Utils.ConvertMatToImage(detectionMat));
		selectedMinView.setImage(Utils.ConvertMatToImage(detectionMat));
		mainToggleBtn.setSelected(true);
		//Save detection as property
		imageView.getProperties().put("detection", detMan.getFaces());
		selectedMinView.getProperties().put("detection", detMan.getFaces());
		
		setFaceResults(detMan.getFaces());
		
		detected = true;
	}
	
	public void setFaceResults(ArrayList<Detection> faces) {
		displayResults(faces, facesMinPane);
		if (!faces.isEmpty()) {
			displayResults(((RectDetection) faces.get(0)).getInnerDetections(), eyesMinPane);
		}
	}
	
	public void setResults(Detector det) {
//		int i = 0;
//		for (Rect pupil : det.getDetectedPupils()){
//			Mat img = new Mat(det.getOriginalMat(), pupil);
//			ImageView imageView = createImageView(Utils.ConvertMatToImage(img));
//			imageView.setUserData(i);
//			resultsMinPane.getChildren().add(imageView);
//			i++;
//		}
	}
	
	public void clearResults() {
		facesMinPane.getChildren().clear();
		eyesMinPane.getChildren().clear();
		resultImageView.setImage(null);
	}
	
	public void drawDetections(Detector det) {
		
	}
	
	@FXML
	public void clickFaceImage(Event e) {
		if(((MouseEvent) e).getButton().equals(MouseButton.PRIMARY)){
			eyesMinPane.getChildren().clear();
            resultImageView.setImage(((ImageView) e.getTarget()).getImage());
            RectDetection face = (RectDetection) ((ImageView) e.getTarget()).getProperties().get("detection");
            displayResults(face.getInnerDetections(), eyesMinPane);
        }
	}
	
	public void displayResults(ArrayList<Detection> dets, TilePane pane) {
		for (Detection det : dets) {
        	RectDetection rectDet = (RectDetection) det;
			Mat img = new Mat(Highgui.imread((String) imageView.getProperties().get("path")), new Rect(rectDet.getX(), rectDet.getY(), rectDet.getWidth(), rectDet.getHeight()));
			ImageView imageView = new ImageView(Utils.ConvertMatToImage(img));
			imageView.getProperties().put("detection", rectDet);
			imageView.setFitWidth(100);
		    imageView.setPreserveRatio(true);  
		    imageView.setSmooth(true);  
		    pane.getChildren().add(imageView);
        }
	}
	
	@FXML
	public void clickEyeImage(Event e) {
		if(((MouseEvent) e).getButton().equals(MouseButton.PRIMARY)){
            resultImageView.setImage(((ImageView) e.getTarget()).getImage());
//          Integer i = (Integer) ((ImageView) e.getTarget()).getUserData();            	
            //System.out.println(currentDetection.getPupilColorsPercentage(i));
//          histogramView.setImage(currentDetection.getHistogram(i)); voy a ver que devuelvo
        }
	}
	
	private ImageView createImageView(String imagePath) {
		String path = "file:///" + imagePath;
		Image image = new Image(path);
		final int DEFAULT_THUMBNAIL_WIDTH = 100;
	    ImageView imageView = new ImageView(image);  
	    imageView.setFitWidth(DEFAULT_THUMBNAIL_WIDTH);  
	    imageView.setPreserveRatio(true);  
	    imageView.setSmooth(true);  
	    return imageView; 
	}  
	
	@FXML
	public void toggleMainDet(Event e) {
		if (mainToggleBtn.isSelected()) {
			imageView.setImage((Image) imageView.getProperties().get("detectionImage"));
		} else {
			imageView.setImage((Image) imageView.getProperties().get("original"));
		}
	}
	
	@FXML
	public void calculateHistogramHSV() {
		//In OpenCV, H = 0-180, S = 0-255, V = 0-255
		if(detected==true) {
			for(Detection pupil : detMan.getPupils()) {				
				Mat img= new Mat(detectionMat, ((CircleDetection)pupil).getInternRect());
				ColorHSV white = new ColorHSV("blanco", new Rank(0.0,180.0), new Rank(0.0,56.0), new Rank(229.5,255.0));
				ColorHSV black = new ColorHSV("negro", new Rank(0.0,180.0), new Rank(0.0,255.0), new Rank(0.0,64.0));
				ColorHSV red = new ColorHSV("rojo", new Rank(166.5,10.0), new Rank(127.0,255.0), new Rank(64.0,229.5));
				ColorHSV yellow = new ColorHSV("amarillo", new Rank(19.0,31.0), new Rank(127.0,255.0), new Rank(64.0,229.5));
				ColorHSV green = new ColorHSV("verde", new Rank(31.0,68.0), new Rank(127.0,255.0), new Rank(64.0,229.5));
				HistogramHSV histogram=new HistogramHSV();
				histogram.addColor(white);
				histogram.addColor(black);
				histogram.addColor(red);
				histogram.addColor(yellow);
				histogram.addColor(green);
				procMan.CalculateColorsPercentageHSV(img, histogram);
				for(ColorHSV color: histogram.getColors()) {
					System.out.println("{"+color.getName()+", "+color.getPercentage()+"}");
				}
				System.out.println();
			}
		}
		
	}
	
	@FXML
	public void calculateHistogramHSL() {
		//In OpenCV, H = 0-180, S = 0-255, L = 0-255
		if(detected==true) {
			for(Detection pupil : detMan.getPupils()) {				
				Mat img= new Mat(detectionMat, ((CircleDetection)pupil).getInternRect());
				ColorHSL lum = new ColorHSL("luminoso", new Rank(0.0,180.0), new Rank(0.0,255.0), new Rank(204.0,255.0));
				ColorHSL osc = new ColorHSL("oscuro", new Rank(0.0,180.0), new Rank(0.0,255.0), new Rank(0.0,204.0));
				HistogramHSL histogram=new HistogramHSL();
				histogram.addColor(lum);
				histogram.addColor(osc);
				procMan.CalculateColorsPercentageHSL(img, histogram);
				for(ColorHSL color: histogram.getColors()) {
					System.out.println("{"+color.getName()+", "+color.getPercentage()+"}");					
				}
				System.out.println();
			}
		}
	}
	
	@FXML
	public void Exit() {
		System.exit(0);
	}

}
