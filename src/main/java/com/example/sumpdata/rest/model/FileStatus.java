package com.example.sumpdata.rest.model;

import java.io.Serializable;

public class FileStatus implements Serializable {
    private String filename;
    private boolean success = false;
    private int numEntries = 0;
    private String error = "";

    public FileStatus(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getNumEntries() {
        return numEntries;
    }

    public void setNumEntries(int numEntries) {
        this.numEntries = numEntries;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
