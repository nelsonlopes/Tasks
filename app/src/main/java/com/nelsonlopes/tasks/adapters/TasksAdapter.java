package com.nelsonlopes.tasks.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.nelsonlopes.tasks.MainActivity;
import com.nelsonlopes.tasks.R;
import com.nelsonlopes.tasks.models.Task_;

import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.MyViewHolder> {
    private Context mContext;
    private List<Task_> mTasks;

    // Provide a reference to the views for each data item
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public MyViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public TasksAdapter(Context context, List<Task_> tasks) {
        mContext = context;
        mTasks = tasks;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TasksAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);

        TasksAdapter.MyViewHolder vh = new TasksAdapter.MyViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final TasksAdapter.MyViewHolder holder, int position) {
        // - get element from the dataset at this position
        // - replace the contents of the view with that
        TextView taskName = holder.view.findViewById(R.id.task_name);
        Button delete_task = holder.view.findViewById(R.id.delete_task);

        taskName.setText(mTasks.get(position).getName());
        delete_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.db.collection("tasks").document(mTasks.get(position).getDocumentId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("DELETE", "DocumentSnapshot successfully deleted!");
                                mTasks.remove(position);
                                setTasks(mTasks);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("DELETE", "Error deleting document", e);
                            }
                        });
            }
        });
    }

    // Return the size of the dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mTasks != null) {
            return mTasks.size();
        } else {
            return 0;
        }
    }

    public Task_ getItem(int position) {
        if (mTasks == null || mTasks.size() == 0) {
            return null;
        }

        return mTasks.get(position);
    }

    public void setTasks(List<Task_> tasks) {
        this.mTasks = tasks;
        notifyDataSetChanged();
    }
}
