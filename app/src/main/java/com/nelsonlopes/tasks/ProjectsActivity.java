package com.nelsonlopes.tasks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nelsonlopes.tasks.adapters.ProjectsAdapter;
import com.nelsonlopes.tasks.models.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProjectsActivity extends AppCompatActivity {

    private static final String TAG = "PROJECTS";

    @BindView(R.id.rv_projects) RecyclerView recyclerView;
    @BindView(R.id.et_project_name) EditText projectNameEt;
    @BindView(R.id.bt_submit_project) Button submitProject;

    private List<Project> mProjects = null;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        // Bind the view using Butter Knife
        ButterKnife.bind(this);

        mProjects = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.HORIZONTAL));
        mAdapter = new ProjectsAdapter(this, mProjects);
        recyclerView.setAdapter(mAdapter);

        ListProjects();
    }

    @OnClick(R.id.bt_submit_project)
    public void SubmitProject(View view) {
        AddProject(projectNameEt.getText().toString());
    }

    private void AddProject(String projectName) {
        // Create a new project
        Map<String, Object> project = new HashMap<>();
        project.put("project_name", projectName);

        // Add a new document with a generated ID
        MainActivity.db.collection("projects")
                .add(project)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Project project = new Project();
                        project.setName(projectNameEt.getText().toString());
                        mProjects.add(project);
                        ((ProjectsAdapter) mAdapter).setProjects(mProjects);

                        projectNameEt.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void ListProjects() {
        MainActivity.db.collection("projects")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Project project = new Project();
                                project.setName(document.getString("project_name"));
                                mProjects.add(project);
                                ((ProjectsAdapter) mAdapter).setProjects(mProjects);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
