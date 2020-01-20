package com.nelsonlopes.tasks.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Project implements Parcelable {
    private String documentId;
    private String name;
    private String userUid;
    private List<Task_> tasks = new ArrayList<>();

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

    public List<Task_> getTasks() {
        return tasks;
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

    public void setTasks(List<Task_> tasks) {
        this.tasks = tasks;
    }

    // PARCELABLE
    private Project(Parcel in) {
        documentId = in.readString();
        name = in.readString();
        userUid = in.readString();
        in.readList(this.tasks, (Task_.class.getClassLoader()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(documentId);
        parcel.writeString(name);
        parcel.writeString(userUid);
        parcel.writeList(tasks);
    }

    public static final Parcelable.Creator<Project> CREATOR = new Parcelable.Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel parcel) {
            return new Project(parcel);
        }

        @Override
        public Project[] newArray(int i) {
            return new Project[i];
        }
    };
}
