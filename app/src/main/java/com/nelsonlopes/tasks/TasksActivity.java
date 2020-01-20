package com.nelsonlopes.tasks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nelsonlopes.tasks.adapters.TasksAdapter;
import com.nelsonlopes.tasks.models.Project;
import com.nelsonlopes.tasks.models.Task_;
import com.nelsonlopes.tasks.widget.WidgetUpdateService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.nelsonlopes.tasks.ProjectsActivity.KEY_USER_UID;

public class TasksActivity extends AppCompatActivity {

    private static final String TAG = TasksActivity.class.toString();
    public static final String KEY_PROJECT_ID = "project_id";
    public static final String KEY_TASK_NAME = "task_name";
    public static final String KEY_COMPLETE = "complete";
    public static final String KEY_TASKS = "tasks";

    @BindView(R.id.rv_tasks)
    RecyclerView recyclerView;
    @BindView(R.id.add_task_fab)
    FloatingActionButton addTaskFab;

    private List<Task_> mTasks = null;
    private RecyclerView.Adapter mAdapter;
    private Project mProject;

    public static CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        // Bind the view using Butter Knife
        ButterKnife.bind(this);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        // Get Project from Intent
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        mProject = intent.getParcelableExtra(getString(R.string.parcel_project));

        getSupportActionBar().setTitle(mProject.getName());

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

    @OnClick(R.id.add_task_fab)
    public void AddTaskFab(View view) {
        final EditText edittext = new EditText(this);

        // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
        AlertDialog.Builder builderEdit = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builderEdit.setView(edittext)
                .setTitle("Add Task")
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        if (!TextUtils.isEmpty(edittext.getText().toString().trim())) {
                            AddTask(edittext.getText().toString());
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
        AlertDialog dialogEdit = builderEdit.create();

        dialogEdit.show();
    }

    private void AddTask(String taskName) {
        // Add a new Task
        Map<String, Object> task = new HashMap<>();
        task.put(KEY_TASK_NAME, taskName);
        task.put(KEY_PROJECT_ID, mProject.getDocumentId());
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
                        newTask.setProjectId(mProject.getDocumentId());
                        newTask.setUserUid(MainActivity.mAuth.getCurrentUser().getUid());

                        mTasks.add(newTask);
                        ((TasksAdapter) mAdapter).setTasks(mTasks);

                        // Start Widget's Service, which is going to update the widget's list with the
                        // tasks data
                        startWidgetService();
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
                .whereEqualTo(KEY_PROJECT_ID, mProject.getDocumentId())
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

                            // Start Widget's Service, which is going to update the widget's list with the
                            // tasks data
                            startWidgetService();
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

    /**
     * This will trigger WidgetUpdateService to update the Widget
     * to the last recipe that the user has seen
     */
    void startWidgetService()
    {
        mProject.setTasks(mTasks);
        //Toast.makeText(this, String.valueOf(mTasks.size()), Toast.LENGTH_LONG).show();
        //Toast.makeText(this, String.valueOf(mProject.getTasks().size()), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, WidgetUpdateService.class);
        intent.putExtra(this.getResources().getString(R.string.parcel_project), mProject);
        intent.setAction(WidgetUpdateService.WIDGET_UPDATE_ACTION);
        this.startService(intent);
    }

    private void closeOnError() {
        this.finish();
        Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
    }
}
