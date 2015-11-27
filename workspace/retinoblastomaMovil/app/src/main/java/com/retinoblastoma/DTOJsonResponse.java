package com.retinoblastoma;

/**
 * Created by Tupac on 27/11/2015.
 */
public class DTOJsonResponse {
    private String file;
    private String white;
    private String black;
    private String red;
    private String yellow;
    private String messsage;

    public DTOJsonResponse(){
    }

    public DTOJsonResponse(String file, String white, String black, String red, String yellow, String messsage) {
        this.file = file;
        this.white = white;
        this.black = black;
        this.red = red;
        this.yellow = yellow;
        this.messsage = messsage;
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

    public String getMesssage() {
        return messsage;
    }

    public void setMesssage(String messsage) {
        this.messsage = messsage;
    }
}
