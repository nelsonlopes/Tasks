package com.nelsonlopes.tasks.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.nelsonlopes.tasks.R;
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
        delete_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "DELETE", Toast.LENGTH_SHORT).show();
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
