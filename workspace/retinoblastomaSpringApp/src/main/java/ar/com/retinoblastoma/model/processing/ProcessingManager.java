package ar.com.retinoblastoma.model.processing;

import org.opencv.core.Mat;

import ar.com.retinoblastoma.model.detection.CircleDetection;


public interface ProcessingManager {

  public HistogramHSV calculateColorsPercentageHSV(Mat img);

  public void calculateColorsPercentageHSL(Mat img, HistogramHSL histogram);

  public void saveToCSV(HistogramHSV histogram, String path, Boolean flash, Integer faceCount,
      Integer eyeCount, Integer pupilCount, Integer alturaCara, CircleDetection pupil);

  public Boolean criterio1(HistogramHSV histogram);

  public Boolean criterio2(HistogramHSV histogram);

  public Boolean criterio3(HistogramHSV histogram);

  Double getYellowPercentage(HistogramHSV histogram, int total);

  Double getRedPercentage(HistogramHSV histogram, int total);

  Double getBlackPercentage(HistogramHSV histogram, int total);

  Double getWhitePercentage(HistogramHSV histogram, int total);
}
