package com.example.sumpdata.rest.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UploadStatus implements Serializable {
    private int numFiles;
    private int totalEntries;
    private boolean success;
    private List<FileStatus> fileStatus = new ArrayList<>();

    public int getNumFiles() {
        return numFiles;
    }

    public void setNumFiles(int numFiles) {
        this.numFiles = numFiles;
    }

    public int getTotalEntries() {
        return totalEntries;
    }

    public void setTotalEntries(int totalEntries) {
        this.totalEntries = totalEntries;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void addFileStatus(FileStatus fs) {
        fileStatus.add(fs);
    }

    public List<FileStatus> getFileStatus() {
        return fileStatus;
    }
}
