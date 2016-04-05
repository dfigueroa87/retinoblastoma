package com.retinoblastoma;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by Tupac on 27/11/2015.
 */
@Parcel
public class DTOJsonResponse {

    private String file;
    private List<PupilResult> list;

    public DTOJsonResponse() {
    }

    public DTOJsonResponse(String file, List<PupilResult> list) {
        this.file = file;
        this.list = list;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public List<PupilResult> getList() {
        return list;
    }

    public void setList(List<PupilResult> list) {
        this.list = list;
    }

}
