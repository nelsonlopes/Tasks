package com.nelsonlopes.tasks.models;

public class Task_ {
    private String documentId;
    private String name;
    private String projectId;
    private String userUid;

    // CONSTRUCTORS
    public Task_() {

    }

    // GETTERS
    public String getDocumentId() {
        return documentId;
    }

    public String getName() {
        return name;
    }

    public String getProjectId() {
        return projectId;
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

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}
