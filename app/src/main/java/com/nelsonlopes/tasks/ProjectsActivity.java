package com.nelsonlopes.tasks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

        getSupportActionBar().setTitle("Projects");

        mProjects = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        mAdapter = new ProjectsAdapter(this, mProjects);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart(){
        super.onStart();

        if (MainActivity.status == 0) {
            ListProjects();
        } else {
            finish();
        }
    }

    @OnClick(R.id.bt_submit_project)
    public void SubmitProject(View view) {
        if (!TextUtils.isEmpty(projectNameEt.getText().toString().trim())) {
            AddProject(projectNameEt.getText().toString());
        }
    }

    private void AddProject(String projectName) {
        // Create a new project
        Map<String, Object> project = new HashMap<>();
        project.put("project_name", projectName);
        project.put("user_uid", MainActivity.mAuth.getCurrentUser().getUid());

        // Add a new document with a generated ID
        MainActivity.db.collection("projects")
                .add(project)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                        Project newProject = new Project();

                        newProject.setDocumentId(documentReference.getId());
                        newProject.setName(projectName);
                        newProject.setUserUid(MainActivity.mAuth.getCurrentUser().getUid());

                        mProjects.add(newProject);
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
                .whereEqualTo("user_uid", MainActivity.mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mProjects.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());

                                Project project = new Project();

                                project.setDocumentId(document.getId());
                                project.setName(document.getString("project_name"));
                                project.setUserUid(document.getString("user_uid"));

                                mProjects.add(project);
                                ((ProjectsAdapter) mAdapter).setProjects(mProjects);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem signOut = menu.findItem(R.id.sign_out);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.sign_out:
                MainActivity.status = 1;
                finish();
                return true;
            default:
        }

        return super.onOptionsItemSelected(item);
    }
}
