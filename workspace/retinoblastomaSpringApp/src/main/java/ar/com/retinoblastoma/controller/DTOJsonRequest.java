package ar.com.retinoblastoma.controller;

public class DTOJsonRequest {
	
	private String file;
	
	private String extension; 
	
	public DTOJsonRequest(){}
	
	public DTOJsonRequest(String file, String extension){
		this.extension = extension;
		this.file=file;
	}
	
	public String getFile(){
		return file;
	}

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }

}
