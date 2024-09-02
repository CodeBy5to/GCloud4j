package com.github.codeby5to.gcloud4j.client;

import com.github.codeby5to.gcloud4j.client.Util.MimeTypeUtil;
import com.github.codeby5to.gcloud4j.config.DriveServiceConfig;
import com.github.codeby5to.gcloud4j.model.FileObject;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DriveClient {

    private final MimeTypeUtil mimeTypeUtil;
    private final Drive drive;

    public DriveClient() {
        this.mimeTypeUtil = new MimeTypeUtil();
        this.drive = DriveServiceConfig.getInstance().getService();
    }

    public FileObject getFile(String fileId) {
        var outputStream = new ByteArrayOutputStream();
        var response = new FileObject();
        try{
            Drive.Files.Get fileGet = drive.files().get(fileId);
            var file = fileGet.execute();
            if(file == null) return null;
            file.setFileExtension(mimeTypeUtil.getExtensionFromMimeType(file.getMimeType()));
            response.setFileInfo(file);
            fileGet.executeMediaAndDownloadTo(outputStream);
            response.setFileData(outputStream.toByteArray());
        }catch (IOException e ) {
            throw new RuntimeException("Error downloading file", e);
        }
        return response;
    }

    public List<File> listAllFiles() {
        List<File> files = new ArrayList<>();
        String pageToken = null;
        try {
            do {
                FileList result = drive.files().list()
                        .setPageSize(100)
                        .setFields("nextPageToken, files(id, name, mimeType, createdTime, size, fileExtension)")
                        .setPageToken(pageToken)
                        .execute();
                result.getFiles().forEach(file -> {
                    file.setFileExtension(mimeTypeUtil.getExtensionFromMimeType(file.getMimeType()));
                });
                files.addAll(result.getFiles());
                pageToken = result.getNextPageToken();
            } while (pageToken != null);
        } catch (IOException e) {
            throw new RuntimeException("Error listing files", e);
        }
        return files;
    }

    public File uploadFile(String fileName, byte[] dataFile) {

        var mimeType = mimeTypeUtil.getMimeTypeFromFile(dataFile);
        var extension = mimeTypeUtil.getExtensionFromMimeType(mimeType);

        var fileMetadata = new File();
        fileMetadata.setName(fileName);
        fileMetadata.setMimeType(mimeType);

        var inputStream = new ByteArrayInputStream(dataFile);

        File file = null;
        try {
            file = drive.files().create(fileMetadata, new InputStreamContent(mimeType, inputStream))
                    .setFields("id, name, mimeType, createdTime, size, fileExtension")
                    .execute();
            file.setFileExtension(extension);
        } catch (IOException e) {
            throw new RuntimeException("Error uploading file", e);
        }
        return file;
    }

    public void deleteFile(String fileId) {
        try {
            drive.files().delete(fileId).execute();
        } catch (IOException e) {
            throw new RuntimeException("Error deleting file", e);
        }
    }
}
