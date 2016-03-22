package ar.com.retinoblastoma.controller;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.opencv.core.Mat;
import org.opencv.core.Point;
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
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
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

  @FXML
  Button saveCommentButton;

  @FXML
  TextField comment;

  @FXML
  AnchorPane anchorPane;
  
  @FXML
  Button criterio1;
  
  @FXML
  Button criterio2;
  
  @FXML
  Button criterio3;

  private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

  private final List<Circle> circles = new ArrayList<Circle>();

  private final SimpleDoubleProperty selectionRectInitialX = new SimpleDoubleProperty();
  private final SimpleDoubleProperty selectionRectInitialY = new SimpleDoubleProperty();

  private final SimpleDoubleProperty selectionRectCurrentX = new SimpleDoubleProperty();
  private final SimpleDoubleProperty selectionRectCurrentY = new SimpleDoubleProperty();

  private Circle selectionCircle;

  private final SimpleDoubleProperty selectionCircleRadio = new SimpleDoubleProperty();

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    mainToggleBtn.setDisable(true);
    pieChart.setData(pieChartData);
    checkPositive.setDisable(true);
    comment.setDisable(true);
    comment.setPromptText("comentario");
    saveCommentButton.setDisable(true);
    criterio1.setDisable(true);
    criterio2.setDisable(true);
    criterio3.setDisable(true);

    // all the stuff to draw the circles
    this.selectionCircle = new Circle();
    this.selectionCircle.setFill(Color.web("coral", 0.4));
    this.selectionCircle.setStroke(Color.web("coral", 0.4));
    this.selectionCircle.setStrokeWidth(2);

    this.selectionCircle.radiusProperty().bind(selectionCircleRadio);

    anchorPane.setOnMousePressed(new EventHandler<MouseEvent>() {
      @Override
      public void handle(final MouseEvent event) {
        selectionRectInitialX.set(event.getX());
        selectionRectInitialY.set(event.getY());
        selectionCircle.centerXProperty().set(event.getX());
        selectionCircle.centerYProperty().set(event.getY());
        // to mark positive
        if (!circles.isEmpty()) {
          for (Circle circle : circles) {
            if (event.getX() < (circle.getCenterX() + circle.getRadius())
                && event.getX() > (circle.getCenterX() - circle.getRadius())
                && event.getY() < (circle.getCenterY() + circle.getRadius())
                && event.getY() > (circle.getCenterY() - circle.getRadius())) {
              circle.setFill(Color.web("blueviolet", 0.4));
              circle.setStroke(Color.web("blueviolet", 0.4));
            }

          }
          repaint();
        }

      }
    });

    anchorPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
      @Override
      public void handle(final MouseEvent event) {
        if (event.getX() - selectionRectInitialX.get() > event.getY() - selectionRectInitialY.get())
          selectionCircleRadio.set(event.getX() - selectionRectInitialX.get());
        else
          selectionCircleRadio.set(event.getY() - selectionRectInitialY.get());
        repaint();
      }
    });

    anchorPane.setOnMouseReleased(new EventHandler<MouseEvent>() {
      @Override
      public void handle(final MouseEvent event) {

        final Circle newCircle = new Circle();
        newCircle.setFill(Color.web("coral", 0.4));
        newCircle.setStroke(Color.web("coral", 0.4));
        newCircle.setStrokeWidth(2);
        newCircle.setRadius(selectionCircle.getRadius());
        newCircle.setCenterX(selectionCircle.getCenterX());
        newCircle.setCenterY(selectionCircle.getCenterY());
        if (newCircle.getRadius() > 2) {
          circles.add(newCircle);
        }
        selectionCircleRadio.set(0);
        selectionRectCurrentX.set(0);
        selectionRectCurrentY.set(0);

        repaint();
      }
    });

  }

  // to draw the circles over the image
  public void repaint() {
    this.anchorPane.getChildren().clear();
    this.anchorPane.getChildren().add(this.imageView);
    if (this.selectionCircle.getRadius() > 4)
      this.anchorPane.getChildren().add(this.selectionCircle);
    for (final Circle circle : this.circles) {
      this.anchorPane.getChildren().add(circle);
    }
  }

  @FXML
  public void LoadImage() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Abrir imagen");
    if (path != null) {
      File file = new File(path.substring(0, path.lastIndexOf("\\")));
      fileChooser.setInitialDirectory(file);
    }
    fileChooser.getExtensionFilters()
        .add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
    List<File> files = fileChooser.showOpenMultipleDialog(null);
    if (files != null) {
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
      
      try {
        selectedMinView = (ImageView) e.getTarget();
      } catch (Exception e2) {
        System.out.println("No fue seleccionada una imagen.");
        return;
      }
      // Clear properties data from global imageView
      imageView.getProperties().clear();

      imageView.setImage(selectedMinView.getImage());

      imageView.getProperties().putAll(selectedMinView.getProperties());

      // If there are detections saved for the image, display them
      @SuppressWarnings("unchecked")
      ArrayList<Detection> faces =
          (ArrayList<Detection>) imageView.getProperties().get("detection");
      if (faces != null) {
        setFaceResults(faces);
        mainToggleBtn.setDisable(false);
        toggleMainDet(null);
      }
      // we set the checkBoxFlash in false;
      checkFlash.setSelected(false);
      checkPositive.setDisable(true);
      comment.setDisable(true);
      comment.setPromptText("comentario");
      saveCommentButton.setDisable(true);
      criterio1.setDisable(true);
      criterio2.setDisable(true);
      criterio3.setDisable(true);
      circles.clear();
      repaint();
    }
  }

  // public class B extends ImageView {

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
      ImageView faceImage = null;
      try {
        faceImage = (ImageView) e.getTarget();
      } catch (Exception e2) {
        System.out.println("No fue seleccionada una imagen.");
        return;
      }
      eyesMinPane.getChildren().clear();
      resultImageView.getProperties().clear();
      resultImageView.setImage(faceImage.getImage());
      resultImageView.getProperties().putAll(faceImage.getProperties());
      RectDetection face =
          (RectDetection) faceImage.getProperties().get("detection");
      displayResults(face.getInnerDetections(), eyesMinPane);

      toggleMainDet(null);
      checkPositive.setDisable(true);
      checkPositive.setSelected(false);

      comment.setDisable(true);
      comment.setPromptText("comentario");
      saveCommentButton.setDisable(true);
      criterio1.setDisable(true);
      criterio2.setDisable(true);
      criterio3.setDisable(true);
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
      ImageView eyeImage = null;
      try {
        eyeImage = (ImageView) e.getTarget();
      } catch (Exception e2) {
        System.out.println("No fue seleccionada una imagen.");
        return;
      }
      resultImageView.getProperties().clear();
      resultImageView.setImage(eyeImage.getImage());
      resultImageView.getProperties().putAll(eyeImage.getProperties());

      toggleMainDet(null);

      // detects if the pupil selected has the flag positive in true
      RectDetection eye = (RectDetection) resultImageView.getProperties().get("detection");
      checkPositive.setDisable(true);
      comment.setDisable(true);
      comment.setPromptText("comentario");
      saveCommentButton.setDisable(true);
      checkPositive.setSelected(false);
      criterio1.setDisable(true);
      criterio2.setDisable(true);
      criterio3.setDisable(true);
      if (eye.getInnerDetections() != null && !eye.getInnerDetections().isEmpty()) {
        checkPositive.setDisable(false);
        comment.setDisable(false);
        saveCommentButton.setDisable(false);
        criterio1.setDisable(false);
        criterio2.setDisable(false);
        criterio3.setDisable(false);
        CircleDetection pupil = (CircleDetection) eye.getInnerDetections().get(0);
        checkPositive.setSelected(pupil.isPositive());
        comment.setText(pupil.getComment());
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
  public HistogramHSV calculateHistogramHSV() {
    // In OpenCV, H = 0-180, S = 0-255, V = 0-255
    HistogramHSV histogram = null;
    if (resultImageView.getImage() != null) {
      Detection pupil = ((RectDetection) resultImageView.getProperties().get("detection"))
          .getInnerDetections().get(0);
      Mat originalMat = Highgui.imread((String) imageView.getProperties().get("path"));
      Mat img = new Mat(originalMat, ((CircleDetection) pupil).getInternRect());
      histogram = procMan.calculateColorsPercentageHSV(img);
      int total =0;
      for (ColorHSV color : histogram.getColors()) {
        total += color.getOccurrence();
      }
      pieChartData.clear();      
      pieChartData.add(new PieChart.Data("blanco", procMan.getWhitePercentage(histogram, total) * 100));
      pieChartData.add(new PieChart.Data("negro", procMan.getBlackPercentage(histogram, total) * 100));
      pieChartData.add(new PieChart.Data("rojo", procMan.getRedPercentage(histogram, total) * 100));
      pieChartData.add(new PieChart.Data("amarillo", procMan.getYellowPercentage(histogram, total) * 100));
      applyCustomColorSequence(
          pieChartData, 
          "Snow", 
          "black", 
          "Tomato", 
          "Gold"
        );
      System.out.println("{ blanco , " + procMan.getWhitePercentage(histogram, total) + "}");
      System.out.println("{ negro , " + procMan.getBlackPercentage(histogram, total) + "}");
      System.out.println("{ rojo , " + procMan.getRedPercentage(histogram, total) + "}");
      System.out.println("{ amarillo , " + procMan.getYellowPercentage(histogram, total) + "}");
      
      System.out.println();
    }
    return histogram;
  }
  
  private void applyCustomColorSequence(ObservableList<PieChart.Data> pieChartData, String... pieColors) {
    int i = 0;
    for (PieChart.Data data : pieChartData) {
      data.getNode().setStyle("-fx-pie-color: " + pieColors[i % pieColors.length] + ";");
      i++;
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
          int alturaCara = ((RectDetection) face).getHeight();
          int anchuraCara = ((RectDetection) face).getWidth();
          for (Detection eye : eyes) {
            eyeCount++;
            ArrayList<Detection> pupils = new ArrayList<Detection>();
            pupils = ((RectDetection) eye).getInnerDetections();
            int pupilCount = 0;
            for (Detection pupil : pupils) {
              pupilCount++;
              saveToCSV(faceCount, eyeCount, pupilCount, alturaCara, pupil);
            }
          }
        }
      }
      // to save the selected circles
      if (!circles.isEmpty()) {
        int pupilCount = 0;
        double destWidth = imageView.getFitWidth();
        double destHeight = imageView.getFitHeight();
        // Needed to get private resources
        Field fdestWidth = getField(imageView.getClass(), "destWidth");
        Field fdestHeight = getField(imageView.getClass(), "destHeight");
        fdestWidth.setAccessible(true);
        fdestHeight.setAccessible(true);
        try {
          destWidth = fdestWidth.getDouble(imageView);
          destHeight = fdestHeight.getDouble(imageView);
        } catch (IllegalArgumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        for (Circle circle : circles) {

          // a three rule to get the real values
          Point pt = new Point(circle.getCenterX() * imageView.getImage().getWidth() / destWidth,
              circle.getCenterY() * imageView.getImage().getHeight() / destHeight);
          CircleDetection cD = new CircleDetection(pt,
              new Double(circle.getRadius() * imageView.getImage().getWidth() / destWidth)
                  .intValue());
          cD.setComment("seleccion manual");
          pupilCount++;
          if (circle.getFill().equals(Color.web("blueviolet", 0.4))) {
            cD.setPositive(true);
          }
          saveToCSV(99, 99, pupilCount, cD.getRadius() * 2, cD);
        }
      }
    }

  }

  public void saveToCSV(int faceCount, int eyeCount, int pupilCount, int alturaCara,
      Detection pupil) {
    // In OpenCV, H = 0-180, S = 0-255, V = 0-255
    Mat originalMat = Highgui.imread((String) imageView.getProperties().get("path"));
    Mat img = new Mat(originalMat, ((CircleDetection) pupil).getInternRect());
    HistogramHSV histogram = procMan.calculateColorsPercentageHSV(img);
    String path = (String) imageView.getProperties().get("path");
    procMan.saveToCSV(histogram, path, checkFlash.isSelected(), faceCount, eyeCount, pupilCount,
        alturaCara, (CircleDetection) pupil);
  }

  @FXML
  public void calculateHistogramHSL() {
    // In OpenCV, H = 0-180, S = 0-255, L = 0-255
//    if (resultImageView.getImage() != null) {
//      for (Detection pupil : ((RectDetection) resultImageView.getProperties().get("detection"))
//          .getInnerDetections()) {
//        Mat originalMat = Highgui.imread((String) imageView.getProperties().get("path"));
//        Mat img = new Mat(originalMat, ((CircleDetection) pupil).getInternRect());
//        ColorHSL lum = new ColorHSL("luminoso", new Rank(0.0, 180.0), new Rank(0.0, 255.0),
//            new Rank(204.0, 255.0));
//        ColorHSL osc = new ColorHSL("oscuro", new Rank(0.0, 180.0), new Rank(0.0, 255.0),
//            new Rank(0.0, 204.0));
//        HistogramHSL histogram = new HistogramHSL();
//        histogram.addColor(lum);
//        histogram.addColor(osc);
//        procMan.calculateColorsPercentageHSL(img, histogram);
//        int total = 0;
//        for (ColorHSV color : histogram.getColors()) {
//          total += color.getOccurrence();
//        }
//        pieChartData.clear();
//        pieChartData.add(new PieChart.Data("blanco", procMan.getWhitePercentage(histogram, total) * 100));
//        System.out.println("{" + color.getName() + ", " + color.getPercentage() + "}");
//        System.out.println("{" + color.getName() + ", " + color.getPercentage() + "}");
//        System.out.println("{" + color.getName() + ", " + color.getPercentage() + "}");
//        System.out.println("{" + color.getName() + ", " + color.getPercentage() + "}");
//        System.out.println();
//      }
//    }
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

  @FXML
  public void saveComment() {
    RectDetection eye = (RectDetection) resultImageView.getProperties().get("detection");
    CircleDetection pupil = (CircleDetection) eye.getInnerDetections().get(0);
    if (comment.getText() != null && !"".equals(comment.getText())) {
      pupil.setComment(comment.getText());
    }
  }

  @FXML
  public void criterio1() {
    HistogramHSV histogram = this.calculateHistogramHSV();
    if (histogram != null) {
      boolean criterio1 = procMan.criterio1(histogram);
      String mensaje = new String();
      if (criterio1) {
        mensaje = "Positivo.";
      } else {
        mensaje = "Negativo.";
      }
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Criterio 1");
      alert.setHeaderText("Resultado de evaluar el criterio número uno");
      alert.setContentText("Según el criterio, está pupila da: " + mensaje);
      alert.showAndWait();
    }
  }

  @FXML
  public void criterio2() {
    HistogramHSV histogram = this.calculateHistogramHSV();
    if (histogram != null) {
      boolean criterio2 = procMan.criterio2(histogram);
      String mensaje = new String();
      if (criterio2) {
        mensaje = "Positivo.";
      } else {
        mensaje = "Negativo.";
      }
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Criterio 2");
      alert.setHeaderText("Resultado de evaluar el criterio número dos");
      alert.setContentText("Según el criterio, está pupila da: " + mensaje);
      alert.showAndWait();
    }
  }

  @FXML
  public void criterio3() {
    HistogramHSV histogram = this.calculateHistogramHSV();
    if (histogram != null) {
      boolean criterio3 = procMan.criterio3(histogram);
      String mensaje = new String();
      if (criterio3) {
        mensaje = "Positivo.";
      } else {
        mensaje = "Negativo.";
      }
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Criterio 3");
      alert.setHeaderText("Resultado de evaluar el criterio número tres");
      alert.setContentText("Según el criterio, está pupila da: " + mensaje);
      alert.showAndWait();
    }
  }

  public static Field getField(Class<?> clazz, String fieldName) {
    Class<?> tmpClass = clazz;
    do {
      try {
        Field f = tmpClass.getDeclaredField(fieldName);
        return f;
      } catch (NoSuchFieldException e) {
        tmpClass = tmpClass.getSuperclass();
      }
    } while (tmpClass != null);

    throw new RuntimeException("Field '" + fieldName + "' not found on class " + clazz);
  }

}
