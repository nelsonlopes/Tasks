package com.nelsonlopes.tasks.models;

public class Project {
    private String documentId;
    private String name;
    private String userUid;

    // CONSTRUCTORS
    public Project() {

    }

    // GETTERS
    public String getDocumentId() {
        return documentId;
    }

    public String getName() {
        return name;
    }

    public String getUserUid() {
        return userUid;
    }

    // SETTERS
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}
