package ar.com.retinoblastoma.controller;

import java.util.ArrayList;

public class DTOJsonResponse {
  
  private String file;
  

  private ArrayList<PupilResult> list = new ArrayList<PupilResult>();

  public DTOJsonResponse(String file,ArrayList<PupilResult> list) {
    this.file = file;
    this.list = list;
  }

  public ArrayList<PupilResult> getList() {
    return list;
  }

  public void setList(ArrayList<PupilResult> list) {
    this.list = list;
  }
  
  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

}
