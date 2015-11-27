package com.retinoblastoma;

import com.google.gson.annotations.SerializedName;

public class DTOJson {

    private String file;

    private String extension;

    public DTOJson() {
    }

    public DTOJson(String file, String extension){
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
