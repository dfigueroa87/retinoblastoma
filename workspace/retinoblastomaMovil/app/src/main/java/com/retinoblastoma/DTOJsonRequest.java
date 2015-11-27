package com.retinoblastoma;

import com.google.gson.annotations.SerializedName;

public class DTOJsonRequest {

    private String file;

    private String extension;

    public DTOJsonRequest() {
    }

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

}
