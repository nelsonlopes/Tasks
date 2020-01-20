package com.nelsonlopes.tasks.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Task_ implements Parcelable {
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

    // PARCELABLE
    private Task_(Parcel in) {
        documentId = in.readString();
        name = in.readString();
        projectId = in.readString();
        userUid = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(documentId);
        parcel.writeString(name);
        parcel.writeString(projectId);
        parcel.writeString(userUid);
    }

    public static final Parcelable.Creator<Task_> CREATOR = new Parcelable.Creator<Task_>() {
        @Override
        public Task_ createFromParcel(Parcel parcel) {
            return new Task_(parcel);
        }

        @Override
        public Task_[] newArray(int i) {
            return new Task_[i];
        }
    };
}
