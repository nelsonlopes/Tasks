package com.nelsonlopes.tasks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
//import android.util.Log;
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

import static com.nelsonlopes.tasks.ProjectsActivity.KEY_PROJECT_NAME;
import static com.nelsonlopes.tasks.ProjectsActivity.KEY_USER_UID;

public class TasksActivity extends AppCompatActivity {

    private static final String TAG = TasksActivity.class.toString();

    @BindView(R.id.rv_tasks)
    RecyclerView recyclerView;
    @BindView(R.id.et_task_name)
    EditText taskNameEt;
    @BindView(R.id.bt_submit_task)
    Button submitTask;

    private List<Task_> mTasks = null;
    private RecyclerView.Adapter mAdapter;
    private String projectId;
    private String projectName;

    private static final String KEY_PROJECT_ID = "project_id";
    private static final String KEY_TASK_NAME = "task_name";
    private static final String KEY_COMPLETE = "complete";
    private static final String KEY_TASKS = "tasks";

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

        projectId = intent.getStringExtra(KEY_PROJECT_ID);
        projectName = intent.getStringExtra(KEY_PROJECT_NAME);

        getSupportActionBar().setTitle(projectName);

        mTasks = new ArrayList<>();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
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
        if (!TextUtils.isEmpty(taskNameEt.getText().toString().trim())) {
            AddTask(taskNameEt.getText().toString());
        }
    }

    private void AddTask(String taskName) {
        // Add a new Task
        Map<String, Object> task = new HashMap<>();
        task.put(KEY_TASK_NAME, taskName);
        task.put(KEY_PROJECT_ID, projectId);
        task.put(KEY_USER_UID, MainActivity.mAuth.getCurrentUser().getUid());
        task.put(KEY_COMPLETE, false);

        // Add a new document with a generated ID
        MainActivity.db.collection(KEY_TASKS)
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
                        //Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void ListTasks() {
        MainActivity.db.collection(KEY_TASKS)
                .whereEqualTo(KEY_PROJECT_ID, projectId)
                .whereEqualTo(KEY_USER_UID, MainActivity.mAuth.getCurrentUser().getUid())
                .whereEqualTo(KEY_COMPLETE, false)
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
                                task_.setName(document.getString(KEY_TASK_NAME));
                                task_.setProjectId(document.getString(KEY_PROJECT_ID));
                                task_.setUserUid(document.getString(KEY_USER_UID));

                                mTasks.add(task_);
                                ((TasksAdapter) mAdapter).setTasks(mTasks);
                            }
                        } else {
                            //Log.w(TAG, "Error getting documents.", task.getException());
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
        Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
    }
}
