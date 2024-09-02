package com.github.codeby5to.gcloud4j.model;

import com.google.api.services.drive.model.File;

import java.util.Arrays;

public class FileObject {

    public FileObject() {
    }

    public FileObject(File fileInfo, byte[] fileData) {
        this.fileInfo = fileInfo;
        this.fileData = fileData;
    }

    private File fileInfo;
    private byte[] fileData;

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public File getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(File fileInfo) {
        this.fileInfo = fileInfo;
    }

    @Override
    public String toString() {
        return "FileObject{" +
                "fileInfo=" + fileInfo +
                ", fileData=" + Arrays.toString(fileData) +
                '}';
    }
}
