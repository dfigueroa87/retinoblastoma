package ar.com.retinoblastoma.controller;

public class PupilResult {


  private String white;
  private String black;
  private String red;
  private String yellow;
  private String messsage;

  public PupilResult(String white, String black, String red, String yellow, String messsage) {

    this.white = white;
    this.black = black;
    this.red = red;
    this.yellow = yellow;
    this.messsage = messsage;
  }



  public String getWhite() {
    return white;
  }

  public void setWhite(String white) {
    this.white = white;
  }

  public String getBlack() {
    return black;
  }

  public void setBlack(String black) {
    this.black = black;
  }

  public String getRed() {
    return red;
  }

  public void setRed(String red) {
    this.red = red;
  }

  public String getYellow() {
    return yellow;
  }

  public void setYellow(String yellow) {
    this.yellow = yellow;
  }

  public String getMesssage() {
    return messsage;
  }

  public void setMesssage(String messsage) {
    this.messsage = messsage;
  }
}
