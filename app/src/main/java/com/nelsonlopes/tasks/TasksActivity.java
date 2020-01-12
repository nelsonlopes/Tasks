package com.nelsonlopes.tasks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.nelsonlopes.tasks.adapters.TasksAdapter;
import com.nelsonlopes.tasks.models.Task_;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TasksActivity extends AppCompatActivity {

    private static final String TAG = "PROJECTS";

    @BindView(R.id.rv_tasks)
    RecyclerView recyclerView;
    @BindView(R.id.et_task_name)
    EditText taskNameEt;
    @BindView(R.id.bt_submit_task)
    Button submitTask;

    private List<Task_> mTasks = null;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String projectId;
    private String projectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        // Bind the view using Butter Knife
        ButterKnife.bind(this);

        // Get project id from Intent
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        projectId = intent.getStringExtra("project_id");
        projectName = intent.getStringExtra("project_name");
        //Toast.makeText(this, projectId, Toast.LENGTH_LONG).show();

        getSupportActionBar().setTitle(projectName);

        mTasks = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        mAdapter = new TasksAdapter(this, mTasks);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume(){
        super.onResume();

        ListTasks();
    }

    @OnClick(R.id.bt_submit_task)
    public void SubmitTask(View view) {
        AddTask(taskNameEt.getText().toString());
    }

    private void AddTask(String taskName) {
        // Create a new project
        Map<String, Object> task = new HashMap<>();
        task.put("task_name", taskName);
        task.put("project_id", projectId);
        task.put("user_uid", MainActivity.mAuth.getCurrentUser().getUid());

        // Add a new document with a generated ID
        MainActivity.db.collection("tasks")
                .add(task)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                        Task_ newTask = new Task_();

                        newTask.setDocumentId(documentReference.getId());
                        newTask.setName(taskName);
                        newTask.setProjectId(projectId);
                        newTask.setUserUid(MainActivity.mAuth.getCurrentUser().getUid());

                        mTasks.add(newTask);
                        ((TasksAdapter) mAdapter).setTasks(mTasks);

                        taskNameEt.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void ListTasks() {
        MainActivity.db.collection("tasks")
                .whereEqualTo("project_id", projectId)
                .whereEqualTo("user_uid", MainActivity.mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mTasks.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());

                                Task_ task_ = new Task_();

                                task_.setDocumentId(document.getId());
                                task_.setName(document.getString("task_name"));
                                task_.setProjectId(document.getString("project_id"));
                                task_.setUserUid(document.getString("user_uid"));

                                mTasks.add(task_);
                                ((TasksAdapter) mAdapter).setTasks(mTasks);
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

    private void closeOnError() {
        this.finish();
        //Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
    }
}
