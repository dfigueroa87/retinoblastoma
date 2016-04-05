package ar.com.retinoblastoma.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;

import ar.com.retinoblastoma.model.detection.CircleDetection;
import ar.com.retinoblastoma.model.detection.Detection;
import ar.com.retinoblastoma.model.detection.DetectionManager;
import ar.com.retinoblastoma.model.detection.DetectionManagerImpl;
import ar.com.retinoblastoma.model.detection.RectDetection;
import ar.com.retinoblastoma.model.processing.HistogramHSV;
import ar.com.retinoblastoma.model.processing.ProcessingManager;
import ar.com.retinoblastoma.model.processing.ProcessingManagerImpl;
import ar.com.retinoblastoma.utils.Utils;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;

public class APIController {

  static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private Random rnd = new Random();
  static DetectionManager detMan = new DetectionManagerImpl();
  static ProcessingManager procMan = new ProcessingManagerImpl();

  // Convert a Base64 string and create a file
  private File convertFile(String file_string, String file_name) throws IOException {
    Base64.Decoder decoder = Base64.getDecoder();
    byte[] decodedByteArray = decoder.decode(file_string);//
    File file = new File(file_name);
    FileOutputStream fop = new FileOutputStream(file);
    fop.write(decodedByteArray);
    fop.flush();
    fop.close();
    return file;
  }

  private String randomString(int len, String extension) {
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++)
      sb.append(AB.charAt(rnd.nextInt(AB.length())));
    return sb.toString() + extension;
  }

  private String convertFileToString(File file) throws IOException {
    byte[] bytes = Files.readAllBytes(file.toPath());
    Base64.Encoder encoder = Base64.getEncoder();
    String b = encoder.encodeToString(bytes);
    return b;
  }

  public DTOJsonResponse calcularCriterio2(DTOJsonRequest request) {
    String fileName = randomString(8, request.getExtension());
    File file;
    try {
      file = convertFile(request.getFile(), fileName);
      String path;
      ArrayList<PupilResult> list = new ArrayList<PupilResult>();
      if (file != null) {
        path = file.getAbsolutePath();
        Mat originalMat = Highgui.imread(path);
        Mat detectionMat = detMan.detect(originalMat);
        ArrayList<Detection> detections = detMan.getFaces();
        for (Detection face : detections) {
          ArrayList<Detection> eyes = new ArrayList<Detection>();
          eyes = ((RectDetection) face).getInnerDetections();
          for (Detection eye : eyes) {
            ArrayList<Detection> pupils = new ArrayList<Detection>();
            pupils = ((RectDetection) eye).getInnerDetections();
            for (Detection pupil : pupils) {
              Mat img = new Mat(originalMat, ((CircleDetection) pupil).getInternRect());
              HistogramHSV histogram = procMan.calculateColorsPercentageHSV(img);
              if (histogram != null) {
                boolean criterio2 = procMan.criterio2(histogram);
                int total = procMan.getTotal(histogram);
                String mensaje = new String();
                if (criterio2) {
                  mensaje = "Positivo";
                } else {
                  mensaje = "Negativo";
                }
                Mat eyeMat = new Mat(originalMat,
                        new Rect(((RectDetection) eye).getX(), ((RectDetection) eye).getY(), ((RectDetection) eye).getWidth(), ((RectDetection) eye).getHeight()));
                    String pathEye = randomString(8, "eye.jpg");
                    Highgui.imwrite(pathEye, eyeMat);
                        File fileResult = new File(pathEye);
                PupilResult pupilResult = new PupilResult(
                    new DecimalFormat("0.00")
                        .format(procMan.getWhitePercentage(histogram, total) * 100),
                    new DecimalFormat("0.00")
                        .format(procMan.getBlackPercentage(histogram, total) * 100),
                    new DecimalFormat("0.00")
                        .format(procMan.getRedPercentage(histogram, total) * 100),
                    new DecimalFormat("0.00")
                        .format(procMan.getYellowPercentage(histogram, total) * 100),
                    mensaje,convertFileToString(fileResult));
                list.add(pupilResult);

              }
            }
          }
        }
        String pathResult =
            path.substring(0, path.indexOf(".") - 1) + "-Detected" + request.getExtension();
        Highgui.imwrite(pathResult, detectionMat);
        File fileResult = new File(pathResult);        
        DTOJsonResponse result = new DTOJsonResponse(convertFileToString(fileResult), list);
        return result;
      }
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    return null;
  }
}
