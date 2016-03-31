package ar.com.retinoblastoma.model.processing;

import java.io.FileWriter;
import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import ar.com.retinoblastoma.model.detection.CircleDetection;
import au.com.bytecode.opencsv.CSVWriter;

public class ProcessingManagerImpl implements ProcessingManager {
  private ColorHSV white =
      new ColorHSV("blanco", new Rank(0.0, 180.0), new Rank(0.0, 56.0), new Rank(229.5, 255.0));
  private ColorHSV black =
      new ColorHSV("negro", new Rank(0.0, 180.0), new Rank(0.0, 255.0), new Rank(0.0, 51.0));
  private ColorHSV red =
      new ColorHSV("rojo", new Rank(166.5, 10.0), new Rank(127.0, 255.0), new Rank(51.0, 255.0));
  private ColorHSV yellow =
      new ColorHSV("amarillo", new Rank(22.5, 37.5), new Rank(127.0, 255.0), new Rank(51.0, 255.0));

  @Override
  public HistogramHSV calculateColorsPercentageHSV(Mat img) {
    HistogramHSV histogram = new HistogramHSV();
    histogram.addColor(white.clone());
    histogram.addColor(black.clone());
    histogram.addColor(red.clone());
    histogram.addColor(yellow.clone());
    Mat imgHSV = new Mat();
    Imgproc.cvtColor(img, imgHSV, Imgproc.COLOR_BGR2HSV);

    for (int x = 0; x < imgHSV.cols(); x++) {
      for (int y = 0; y < imgHSV.rows(); y++) {
        double[] hsv = imgHSV.get(y, x);
        histogram.calculate(hsv[0], hsv[1], hsv[2]);
      }
    }
    return histogram;
  }

  @Override
  public void calculateColorsPercentageHSL(Mat img, HistogramHSL histogram) {
    Mat imgHSL = new Mat();
    Imgproc.cvtColor(img, imgHSL, Imgproc.COLOR_BGR2HLS);

    for (int x = 0; x < imgHSL.cols(); x++) {
      for (int y = 0; y < imgHSL.rows(); y++) {
        double[] hsl = imgHSL.get(y, x);
        histogram.calculate(hsl[0], hsl[2], hsl[1]);
      }
    }
  }

  @Override
  public void saveToCSV(HistogramHSV histogram, String path, Boolean flash, Integer faceCount,
      Integer eyeCount, Integer pupilCount, Integer alturaCara, CircleDetection pupil) {
    int total = getTotal(histogram);
    Double whitePerc = getWhitePercentage(histogram, total);
    Double blackPerc = getBlackPercentage(histogram, total);
    Double redPerc = getRedPercentage(histogram, total);
    Double yellowPerc = getYellowPercentage(histogram, total);
    String[] record =
        {path.substring(path.lastIndexOf("\\")), "" + faceCount, "" + eyeCount, "" + pupilCount,
            whitePerc.toString(), blackPerc.toString(), redPerc.toString(), yellowPerc.toString(),
            "" + flash, "" + pupil.isPositive(), pupil.getComment(), "" + alturaCara};
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



  @Override
  public Boolean criterio1(HistogramHSV histogram) {
    int total = getTotal(histogram);
    Double blackPerc = getBlackPercentage(histogram, total);
    Double redPerc = getRedPercentage(histogram, total);
    if (blackPerc > 0.5 || redPerc > 0.5) {
      return false;
    }

    return true;
  }



  @Override
  public Boolean criterio2(HistogramHSV histogram) {
    int total = getTotal(histogram);
    Double whitePerc = getWhitePercentage(histogram, total);
    Double yellowPerc = getYellowPercentage(histogram, total);
    if (whitePerc > 0.3 || yellowPerc > 0.3) {
      return true;
    }

    return false;
  }

  @Override
  public Boolean criterio3(HistogramHSV histogram) {
    int total = getTotal(histogram);
    Double whitePerc = getWhitePercentage(histogram, total);
    Double yellowPerc = getYellowPercentage(histogram, total);
    Double blackPerc = getBlackPercentage(histogram, total);
    Double redPerc = getRedPercentage(histogram, total);
    if ((blackPerc < 0.5 && redPerc < 0.5) || whitePerc > 0.5 || yellowPerc > 0.5) {
      return true;
    }

    return false;
  }

  @Override
  public Double getWhitePercentage(HistogramHSV histogram, int total) {
    Double whitePerc = new Double(
        ((double) histogram.getColors().get(histogram.getColors().indexOf(white)).getOccurrence())
            / ((double) total));
    return whitePerc;
  }

  @Override
  public Double getBlackPercentage(HistogramHSV histogram, int total) {
    Double whitePerc = new Double(
        ((double) histogram.getColors().get(histogram.getColors().indexOf(black)).getOccurrence())
            / ((double) total));
    return whitePerc;
  }

  @Override
  public Double getRedPercentage(HistogramHSV histogram, int total) {
    Double whitePerc = new Double(
        ((double) histogram.getColors().get(histogram.getColors().indexOf(red)).getOccurrence())
            / ((double) total));
    return whitePerc;
  }

  @Override
  public Double getYellowPercentage(HistogramHSV histogram, int total) {
    Double whitePerc = new Double(
        ((double) histogram.getColors().get(histogram.getColors().indexOf(yellow)).getOccurrence())
            / ((double) total));
    return whitePerc;
  }

  @Override
  public int getTotal(HistogramHSV histogram) {
    int total = 0;
    for (ColorHSV color : histogram.getColors()) {
      total += color.getOccurrence();
    }
    return total;
  }
}
