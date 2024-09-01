package com.github.codeby5to.gcloud4j.config.util;

import java.util.Set;

public enum Scopes {

    FIRESTORE("https://www.googleapis.com/auth/datastore", null);


    final String singleScope;
    final Set<String> allScopes;

    Scopes(String singleScope, Set<String> allScopes){
        this.singleScope = singleScope;
        this.allScopes = allScopes;
    }

    public String getSingleScope() {
        return singleScope;
    }

    public Set<String> getAllScopes() {
        return allScopes;
    }
}
