package com.github.codeby5to.gcloud4j.config;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

public class FirestoreDbConfig {

    private static FirestoreDbConfig instance;
    private final Firestore db;

    public FirestoreDbConfig() {
        this.db = FirestoreOptions.newBuilder()
                .setCredentials(CredentialsManagerConfig.getInstance().getCredentials())
                .build().getService();
    }

    public FirestoreDbConfig(String dataBaseId) {
        this.db = FirestoreOptions.newBuilder()
                .setCredentials(CredentialsManagerConfig.getInstance().getCredentials())
                .setDatabaseId(dataBaseId)
                .build().getService();
    }

    public static synchronized FirestoreDbConfig getInstance() {
        if (instance == null) {
            instance = new FirestoreDbConfig();
        }
        return instance;
    }

    public Firestore getDb() {
        return this.db;
    }
}
