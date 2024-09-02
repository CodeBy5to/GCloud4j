package com.github.codeby5to.gcloud4j.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.About;
import com.google.auth.http.HttpCredentialsAdapter;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.Logger;

public class DriveServiceConfig {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String STORAGE_QUOTA = "storageQuota";
    private static final Logger log = Logger.getLogger(DriveServiceConfig.class.getName());
    private static DriveServiceConfig instance;
    private final Drive service;

    public DriveServiceConfig() {
        try {
            this.service = new Drive.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, null)
                    .setHttpRequestInitializer(
                            new HttpCredentialsAdapter(CredentialsManagerConfig.getInstance().getCredentials())
                    ).build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized DriveServiceConfig getInstance() {
        if (instance == null) {
            instance = new DriveServiceConfig();
        }
        return instance;
    }

    public Drive getService() {
        return this.service;
    }

    public About getQuota() {
        var about = new About();
        try {
            about = service.about().get().setFields(STORAGE_QUOTA).execute();
        } catch (IOException e) {
            log.severe("Error retrieving quota");
        }
        return about;
    }

}
