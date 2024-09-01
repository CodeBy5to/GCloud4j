package com.github.codeby5to.gcloud4j.config;

import com.github.codeby5to.gcloud4j.config.util.Scopes;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class CredentialsManagerConfig {

    private static CredentialsManagerConfig instance;
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private final GoogleCredentials credentials;

    private static final ArrayList<String> scopes = new ArrayList<>();


    public CredentialsManagerConfig() {
        setScopes();

        var credentialPath = getClass().getResourceAsStream(CREDENTIALS_FILE_PATH);
        if(credentialPath == null) throw new RuntimeException("credentials json file not found");

        try {
            this.credentials = GoogleCredentials.fromStream(credentialPath)
                    .createScoped(scopes);
            instance = this;
        } catch (IOException e) {
            throw new RuntimeException("Generated GoogleCredentials Error");
        }
    }

    private void setScopes() {
        Arrays.stream(Scopes.values()).toList().forEach(scope -> {
            if(scope.getSingleScope() == null) {
                scopes.addAll(scope.getAllScopes());
            } else {
                scopes.add(scope.getSingleScope());
            }
        });
    }

    public static synchronized CredentialsManagerConfig getInstance() {
        if (instance == null) {
            throw new RuntimeException("the CredentialsManagerConfig class has not been initialized");
        }
        return instance;
    }

    public GoogleCredentials getCredentials() {
        return credentials;
    }

}

