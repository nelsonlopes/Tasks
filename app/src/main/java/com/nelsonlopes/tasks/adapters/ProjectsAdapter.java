package com.nelsonlopes.tasks.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
        Button delete_project = holder.view.findViewById(R.id.delete_project);

        projectName.setText(mProjects.get(position).getName());
        projectName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, TasksActivity.class);
                intent.putExtra("project_id", mProjects.get(position).getDocumentId());
                intent.putExtra("project_name", mProjects.get(position).getName());
                mContext.startActivity(intent);
            }
        });
        delete_project.setOnClickListener(new View.OnClickListener() {
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
                                MainActivity.db.collection("projects")
                                        .document(mProjects.get(position).getDocumentId())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("DELETE", "DocumentSnapshot successfully deleted!");
                                                mProjects.remove(position);
                                                setProjects(mProjects);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("DELETE", "Error deleting document", e);
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
