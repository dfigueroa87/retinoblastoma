package ar.com.retinoblastoma.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;

import ar.com.retinoblastoma.model.detection.CircleDetection;
import ar.com.retinoblastoma.model.detection.Detection;
import ar.com.retinoblastoma.model.detection.DetectionManager;
import ar.com.retinoblastoma.model.detection.DetectionManagerImpl;
import ar.com.retinoblastoma.model.detection.RectDetection;
import ar.com.retinoblastoma.model.processing.ColorHSL;
import ar.com.retinoblastoma.model.processing.ColorHSV;
import ar.com.retinoblastoma.model.processing.HistogramHSL;
import ar.com.retinoblastoma.model.processing.HistogramHSV;
import ar.com.retinoblastoma.model.processing.ProcessingManager;
import ar.com.retinoblastoma.model.processing.ProcessingManagerImpl;
import ar.com.retinoblastoma.model.processing.Rank;
import ar.com.retinoblastoma.utils.Utils;
import au.com.bytecode.opencsv.CSVWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainController implements Initializable {

	@FXML
	private Button btnAbrir;
	@FXML
	private Button btnDetectar;

	private String path;

	private DetectionManager detMan = new DetectionManagerImpl();
	private ProcessingManager procMan = new ProcessingManagerImpl();
	private Mat detectionMat;

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
	TableView<HashMap<String, Double>> tableColorsPercentage;
	@FXML
	TableColumn<Double, Double> tableColumnWhite;

	@FXML
	ToggleButton mainToggleBtn;

	@FXML
	PieChart pieChart;

	@FXML
	CheckBox checkFlash;

	@FXML
	CheckBox checkPositive;

	boolean flash = false;

	private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		mainToggleBtn.setDisable(true);
		pieChart.setData(pieChartData);
		checkPositive.setDisable(true);
	}

	@FXML
	public void LoadImage() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Abrir imagen");
		if (path != null) {
			File file = new File(path.substring(0, path.lastIndexOf("\\")));
			fileChooser.setInitialDirectory(file);
		}
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
		List<File> files = fileChooser.showOpenMultipleDialog(null);
		for (File file : files) {
			ImageView imageView = createImageView(file.getAbsolutePath());
			imageView.getProperties().put("path", file.getAbsolutePath());
			imageContainer.getChildren().add(imageView);
		}

		// Save the path for the next file chooser dialog
		if (!files.isEmpty()) {
			path = files.get(0).getAbsolutePath();
		}
	}

	@FXML
	/**
	 * Open image when its min view is clicked
	 * 
	 * @param e
	 */
	public void clickImage(Event e) {
		if (((MouseEvent) e).getButton().equals(MouseButton.PRIMARY)) {
			// Clear result panes
			clearResults();

			selectedMinView = (ImageView) e.getTarget();

			// Clear properties data from global imageView
			imageView.getProperties().clear();

			imageView.setImage(selectedMinView.getImage());
			imageView.getProperties().putAll(selectedMinView.getProperties());

			// If there are detections saved for the image, display them
			@SuppressWarnings("unchecked")
			ArrayList<Detection> faces = (ArrayList<Detection>) imageView.getProperties().get("detection");
			if (faces != null) {
				setFaceResults(faces);
				mainToggleBtn.setDisable(false);
				toggleMainDet(null);
			}
			// we set the checkBoxFlash in false;
			checkFlash.setSelected(false);
			checkPositive.setDisable(true);
		}
	}

	@FXML
	public void Detect() {
		// Clear previous results
		clearResults();

		detectionMat = detMan.detect(Highgui.imread((String) imageView.getProperties().get("path")));
		// Save images as properties
		imageView.getProperties().put("original", imageView.getImage());
		selectedMinView.getProperties().put("original", imageView.getImage());
		imageView.getProperties().put("detectionImage", Utils.ConvertMatToImage(detectionMat));
		selectedMinView.getProperties().put("detectionImage", Utils.ConvertMatToImage(detectionMat));
		imageView.getProperties().put("detectionMat", detectionMat);
		selectedMinView.getProperties().put("detectionMat", detectionMat);
		// Set image with detections
		imageView.setImage(Utils.ConvertMatToImage(detectionMat));
		mainToggleBtn.setSelected(true);
		// Save detection as property
		imageView.getProperties().put("detection", detMan.getFaces());
		selectedMinView.getProperties().put("detection", detMan.getFaces());

		setFaceResults(detMan.getFaces());

		mainToggleBtn.setDisable(false);
		imageView.getProperties().put("flash", new Boolean(false));
	}

	public void setFaceResults(ArrayList<Detection> faces) {
		displayResults(faces, facesMinPane);
		if (!faces.isEmpty()) {
			displayResults(((RectDetection) faces.get(0)).getInnerDetections(), eyesMinPane);
		}
	}

	public void clearResults() {
		facesMinPane.getChildren().clear();
		eyesMinPane.getChildren().clear();
		resultImageView.setImage(null);
		resultImageView.getProperties().clear();
		mainToggleBtn.setDisable(true);
		pieChartData.clear();
	}

	@FXML
	public void clickFaceImage(Event e) {
		if (((MouseEvent) e).getButton().equals(MouseButton.PRIMARY)) {
			eyesMinPane.getChildren().clear();

			resultImageView.getProperties().clear();

			resultImageView.setImage(((ImageView) e.getTarget()).getImage());
			resultImageView.getProperties().putAll(((ImageView) e.getTarget()).getProperties());

			RectDetection face = (RectDetection) ((ImageView) e.getTarget()).getProperties().get("detection");
			displayResults(face.getInnerDetections(), eyesMinPane);

			toggleMainDet(null);
			checkPositive.setDisable(true);
		}
	}

	public void displayResults(ArrayList<Detection> dets, TilePane pane) {
		for (Detection det : dets) {
			RectDetection rectDet = (RectDetection) det;
			Mat imgMat = new Mat(Highgui.imread((String) imageView.getProperties().get("path")),
					new Rect(rectDet.getX(), rectDet.getY(), rectDet.getWidth(), rectDet.getHeight()));
			Image img = Utils.ConvertMatToImage(imgMat);
			Mat detectionMat = new Mat((Mat) imageView.getProperties().get("detectionMat"),
					new Rect(rectDet.getX(), rectDet.getY(), rectDet.getWidth(), rectDet.getHeight()));
			Image detectionImg = Utils.ConvertMatToImage(detectionMat);
			ImageView imageView = new ImageView(img);
			imageView.getProperties().put("original", img);
			imageView.getProperties().put("detectionImage", detectionImg);
			imageView.getProperties().put("detection", rectDet);
			imageView.setFitWidth(100);
			imageView.setPreserveRatio(true);
			imageView.setSmooth(true);
			pane.getChildren().addAll(imageView);
		}
	}

	@FXML
	public void clickEyeImage(Event e) {
		if (((MouseEvent) e).getButton().equals(MouseButton.PRIMARY)) {
			resultImageView.getProperties().clear();

			resultImageView.setImage(((ImageView) e.getTarget()).getImage());
			resultImageView.getProperties().putAll(((ImageView) e.getTarget()).getProperties());

			toggleMainDet(null);

			// detects if the pupil selected has the flag positive in true
			RectDetection eye = (RectDetection) resultImageView.getProperties().get("detection");
			if (eye.getInnerDetections() != null && !eye.getInnerDetections().isEmpty()) {
				checkPositive.setDisable(false);
				CircleDetection pupil = (CircleDetection) eye.getInnerDetections().get(0);
				checkPositive.setSelected(pupil.isPositive());
			}
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
			resultImageView.setImage((Image) resultImageView.getProperties().get("detectionImage"));
		} else {
			imageView.setImage((Image) imageView.getProperties().get("original"));
			resultImageView.setImage((Image) resultImageView.getProperties().get("original"));
		}
	}

	@FXML
	public void calculateHistogramHSV() {
		// In OpenCV, H = 0-180, S = 0-255, V = 0-255
		if (resultImageView.getImage() != null) {
			for (Detection pupil : ((RectDetection) resultImageView.getProperties().get("detection"))
					.getInnerDetections()) {
				Mat originalMat = Highgui.imread((String) imageView.getProperties().get("path"));
				Mat img = new Mat(originalMat, ((CircleDetection) pupil).getInternRect());
				ColorHSV white = new ColorHSV("blanco", new Rank(0.0, 180.0), new Rank(0.0, 56.0),
						new Rank(229.5, 255.0));
				ColorHSV black = new ColorHSV("negro", new Rank(0.0, 180.0), new Rank(0.0, 255.0), new Rank(0.0, 64.0));
				ColorHSV red = new ColorHSV("rojo", new Rank(166.5, 10.0), new Rank(127.0, 255.0),
						new Rank(64.0, 229.5));
				ColorHSV yellow = new ColorHSV("amarillo", new Rank(19.0, 31.0), new Rank(127.0, 255.0),
						new Rank(64.0, 229.5));
				HistogramHSV histogram = new HistogramHSV();
				histogram.addColor(white);
				histogram.addColor(black);
				histogram.addColor(red);
				histogram.addColor(yellow);
				procMan.CalculateColorsPercentageHSV(img, histogram);
				pieChartData.clear();
				for (ColorHSV color : histogram.getColors()) {
					pieChartData.add(new PieChart.Data(color.getName(), color.getPercentage() * 100));
					System.out.println("{" + color.getName() + ", " + color.getPercentage() + "}");
				}

				System.out.println();
			}
		}

	}

	@SuppressWarnings("unchecked")
	@FXML
	public void saveToText() {
		if (imageView.getImage() != null) {
			ArrayList<Detection> detections = new ArrayList<Detection>();
			if (imageView.getProperties() != null && imageView.getProperties().containsKey("detection")) {
				int faceCount = 0;
				detections = (ArrayList<Detection>) imageView.getProperties().get("detection");
				for (Detection face : detections) {
					faceCount++;
					ArrayList<Detection> eyes = new ArrayList<Detection>();
					eyes = ((RectDetection) face).getInnerDetections();
					int eyeCount = 0;
					for (Detection eye : eyes) {
						eyeCount++;
						ArrayList<Detection> pupils = new ArrayList<Detection>();
						pupils = ((RectDetection) eye).getInnerDetections();
						int pupilCount = 0;
						for (Detection pupil : pupils) {
							pupilCount++;
							saveToCSV(faceCount, eyeCount, pupilCount, pupil);
						}
					}
				}
			}

		}

	}

	public void saveToCSV(int faceCount, int eyeCount, int pupilCount, Detection pupil) {
		Mat originalMat = Highgui.imread((String) imageView.getProperties().get("path"));
		Mat img = new Mat(originalMat, ((CircleDetection) pupil).getInternRect());
		ColorHSV white = new ColorHSV("blanco", new Rank(0.0, 180.0), new Rank(0.0, 56.0), new Rank(229.5, 255.0));
		ColorHSV black = new ColorHSV("negro", new Rank(0.0, 180.0), new Rank(0.0, 255.0), new Rank(0.0, 64.0));
		ColorHSV red = new ColorHSV("rojo", new Rank(166.5, 10.0), new Rank(127.0, 255.0), new Rank(64.0, 229.5));
		ColorHSV yellow = new ColorHSV("amarillo", new Rank(19.0, 31.0), new Rank(127.0, 255.0), new Rank(64.0, 229.5));
		HistogramHSV histogram = new HistogramHSV();
		histogram.addColor(white);
		histogram.addColor(black);
		histogram.addColor(red);
		histogram.addColor(yellow);
		procMan.CalculateColorsPercentageHSV(img, histogram);
		// we are going to save a percentage but just with these colors
		int total = 0;
		for (ColorHSV color : histogram.getColors()) {
			total += color.getOccurrence();
		}
		Double whitePerc = new Double(
				((double) histogram.getColors().get(histogram.getColors().indexOf(white)).getOccurrence())
						/ ((double) total));
		Double blackPerc = new Double(
				((double) histogram.getColors().get(histogram.getColors().indexOf(black)).getOccurrence())
						/ ((double) total));
		Double redPerc = new Double(
				((double) histogram.getColors().get(histogram.getColors().indexOf(red)).getOccurrence())
						/ ((double) total));
		Double yellowPerc = new Double(
				((double) histogram.getColors().get(histogram.getColors().indexOf(yellow)).getOccurrence())
						/ ((double) total));
		String path = (String) imageView.getProperties().get("path");
		String[] record = { path.substring(path.lastIndexOf("\\")), "" + faceCount, "" + eyeCount, "" + pupilCount,
				whitePerc.toString(), blackPerc.toString(), redPerc.toString(), yellowPerc.toString(),
				"" + checkFlash.isSelected(), "" + ((CircleDetection) pupil).isPositive() };
		CSVWriter writer;
		String csv = "data.csv";
		try {
			writer = new CSVWriter(new FileWriter(csv, true));
			writer.writeNext(record);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@FXML
	public void calculateHistogramHSL() {
		// In OpenCV, H = 0-180, S = 0-255, L = 0-255
		if (resultImageView.getImage() != null) {
			for (Detection pupil : ((RectDetection) resultImageView.getProperties().get("detection"))
					.getInnerDetections()) {
				Mat originalMat = Highgui.imread((String) imageView.getProperties().get("path"));
				Mat img = new Mat(originalMat, ((CircleDetection) pupil).getInternRect());
				ColorHSL lum = new ColorHSL("luminoso", new Rank(0.0, 180.0), new Rank(0.0, 255.0),
						new Rank(204.0, 255.0));
				ColorHSL osc = new ColorHSL("oscuro", new Rank(0.0, 180.0), new Rank(0.0, 255.0), new Rank(0.0, 204.0));
				HistogramHSL histogram = new HistogramHSL();
				histogram.addColor(lum);
				histogram.addColor(osc);
				procMan.CalculateColorsPercentageHSL(img, histogram);
				pieChartData.clear();
				for (ColorHSL color : histogram.getColors()) {
					pieChartData.add(new PieChart.Data(color.getName(), color.getPercentage() * 100));
					System.out.println("{" + color.getName() + ", " + color.getPercentage() + "}");
				}
				System.out.println();
			}
		}
	}

	@FXML
	public void Exit() {
		System.exit(0);
	}

	@FXML
	public void checkPositivo() {
		RectDetection eye = (RectDetection) resultImageView.getProperties().get("detection");
		CircleDetection pupil = (CircleDetection) eye.getInnerDetections().get(0);
		if (checkPositive.isSelected())
			pupil.setPositive(true);
		else
			pupil.setPositive(false);

	}

}
