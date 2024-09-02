package com.github.codeby5to.gcloud4j.repository;

import com.github.codeby5to.gcloud4j.config.FirestoreDbConfig;
import com.github.codeby5to.gcloud4j.model.Identifiable;
import com.github.codeby5to.gcloud4j.repository.util.MapperUtil;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.firestore.DocumentSnapshot;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FirestoreRepository<T extends Identifiable> {
    private final Class<T> clazz;
    protected final CollectionReference collection;

    private static final Logger log = Logger.getLogger(FirestoreRepository.class.getName());

    @SuppressWarnings("unchecked")
    public FirestoreRepository() {
        log.setLevel(Level.ALL);
        this.clazz = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        var db = FirestoreDbConfig.getInstance().getDb();
        this.collection = db.collection(clazz.getSimpleName());
    }

    public List<T> getAll() {
        var response = new ArrayList<T>();
        try {
            ApiFuture<QuerySnapshot> future = collection.get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                T object = document.toObject(clazz);
                object.setDocumentId(document.getId());
                response.add(object);
            }
        } catch (ExecutionException | InterruptedException e) {
            log.severe(String.format("Error in get %s collection", clazz.getSimpleName()));
        }
        return response;
    }

    public T save(T object) {
        T response = null;
        DocumentReference docRef = collection.document();
        if(object.getDocumentId() != null) docRef = collection.document(object.getDocumentId());
        var documentRequest = MapperUtil.convertToMap(object);
        try {
            ApiFuture<WriteResult> futureWriteResult = docRef.set(documentRequest);
            futureWriteResult.get();
            ApiFuture<DocumentSnapshot> readFuture = docRef.get();
            DocumentSnapshot documentSnapshot = readFuture.get();
            response = documentSnapshot.toObject(clazz);
            if(response != null) response.setDocumentId(docRef.getId());
        } catch (InterruptedException | ExecutionException e) {
            log.severe(String.format("Error on save in %s collection",clazz.getSimpleName()));
        }
        return response;
    }

    public void delete(T object) {
        var docRef = collection.document(object.getDocumentId());
        try {
            ApiFuture<WriteResult> future = docRef.delete();
            log.info(String.format("deleted %s with id %s at %s",clazz.getSimpleName(), object.getDocumentId(),
                    future.get().getUpdateTime()));

        } catch (InterruptedException | ExecutionException e) {
            log.severe(String.format("Error on delete in %s collection",clazz.getSimpleName()));
        }
    }

    public T findById(String id) {
        DocumentReference docRef = collection.document(id);
        try {
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                return document.toObject(clazz);
            } else {
                log.info(String.format("No such %s",clazz.getSimpleName()));
                return null;
            }
        } catch (Exception e) {
            log.severe(String.format("Error retrieving %s: %s",clazz.getSimpleName(), e.getMessage()));
            return null;
        }
    }
}
