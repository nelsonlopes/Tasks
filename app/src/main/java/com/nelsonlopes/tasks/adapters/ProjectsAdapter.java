package com.nelsonlopes.tasks.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nelsonlopes.tasks.MainActivity;
import com.nelsonlopes.tasks.R;
import com.nelsonlopes.tasks.TasksActivity;
import com.nelsonlopes.tasks.models.Project;

import java.util.List;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.MyViewHolder> {
    private Context mContext;
    private List<Project> mProjects;

    // Provide a reference to the views for each data item
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public MyViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public ProjectsAdapter(Context context, List<Project> projects) {
        mContext = context;
        mProjects = projects;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ProjectsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_item, parent, false);

        ProjectsAdapter.MyViewHolder vh = new ProjectsAdapter.MyViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ProjectsAdapter.MyViewHolder holder, int position) {
        // - get element from the dataset at this position
        // - replace the contents of the view with that
        TextView projectName = holder.view.findViewById(R.id.project_name);
        Button editProject = holder.view.findViewById(R.id.edit_project);
        Button deleteProject = holder.view.findViewById(R.id.delete_project);

        projectName.setText(mProjects.get(position).getName());
        projectName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, TasksActivity.class);
                intent.putExtra(mContext.getResources().getString(R.string.parcel_project), mProjects.get(position));

                mContext.startActivity(intent);
            }
        });

        // Edit Project
        editProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText edittext = new EditText(mContext);
                edittext.setText(mProjects.get(position).getName());

                // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
                AlertDialog.Builder builderEdit = new AlertDialog.Builder(mContext);

                // 2. Chain together various setter methods to set the dialog characteristics
                builderEdit.setView(edittext)
                        .setTitle("Edit Project")
                        .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                if (!TextUtils.isEmpty(edittext.getText().toString().trim())) {
                                    DocumentReference documentReference = MainActivity.db.collection("projects")
                                            .document(mProjects.get(position).getDocumentId());
                                    documentReference.update("project_name", edittext.getText().toString())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mProjects.get(position).setName(edittext.getText().toString());
                                                    setProjects(mProjects);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_LONG).show();
                                                    //Log.d("Androidview", e.getMessage());
                                                }
                                            });
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
        });

        // Delete Project and its Tasks
        deleteProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("Delete " + mProjects.get(position).getName() + " and its tasks?")
                        //.setTitle("Task deletion confirmation")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button

                                // Delete the tasks inside this project
                                MainActivity.db.collection("tasks")
                                        .whereEqualTo("project_id", mProjects.get(position).getDocumentId())
                                        .whereEqualTo("user_uid", MainActivity.mAuth.getCurrentUser().getUid())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        //Log.d(TAG, document.getId() + " => " + document.getData());
                                                        MainActivity.db.collection("tasks")
                                                                .document(document.getId())
                                                                .delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        //Log.d("DELETE", "DocumentSnapshot successfully deleted!");
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        //Log.w("DELETE", "Error deleting document", e);
                                                                    }
                                                                });

                                                    }
                                                } else {
                                                    //Log.w(TAG, "Error getting documents.", task.getException());
                                                }
                                            }
                                        });

                                // Delete the project
                                MainActivity.db.collection("projects")
                                        .document(mProjects.get(position).getDocumentId())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //Log.d("DELETE", "DocumentSnapshot successfully deleted!");
                                                mProjects.remove(position);
                                                setProjects(mProjects);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Log.w("DELETE", "Error deleting document", e);
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });

                // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
                AlertDialog dialog = builder.create();

                dialog.show();
            }
        });
    }

    // Return the size of the dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mProjects != null) {
            return mProjects.size();
        } else {
            return 0;
        }
    }

    public Project getItem(int position) {
        if (mProjects == null || mProjects.size() == 0) {
            return null;
        }

        return mProjects.get(position);
    }

    public void setProjects(List<Project> projects) {
        this.mProjects = projects;
        notifyDataSetChanged();
    }
}
