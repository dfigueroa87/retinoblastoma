package ar.com.retinoblastoma.controller;

public class PupilResult {


	private String file;
  private String white;
  private String black;
  private String red;
  private String yellow;
  private String message;

  public PupilResult(String white, String black, String red, String yellow, String message, String file) {

    this.white = white;
    this.black = black;
    this.red = red;
    this.yellow = yellow;
    this.message = message;
    this.file = file;
  }



  public String getFile() {
	return file;
}



public void setFile(String file) {
	this.file = file;
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

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
