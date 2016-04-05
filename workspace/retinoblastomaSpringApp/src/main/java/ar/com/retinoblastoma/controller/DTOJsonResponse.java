package ar.com.retinoblastoma.controller;

import java.util.ArrayList;
import java.util.List;

public class DTOJsonResponse {
  
  private String file;
  

  private List<PupilResult> list = new ArrayList<PupilResult>();

  public DTOJsonResponse(String file,List<PupilResult> list) {
    this.file = file;
    this.list = list;
  }

  public List<PupilResult> getList() {
    return list;
  }

  public void setList(List<PupilResult> list) {
    this.list = list;
  }
  
  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

}
