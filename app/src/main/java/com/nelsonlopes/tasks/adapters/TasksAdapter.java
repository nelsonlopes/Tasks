package com.nelsonlopes.tasks.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.nelsonlopes.tasks.MainActivity;
import com.nelsonlopes.tasks.R;
import com.nelsonlopes.tasks.models.Task_;

import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.MyViewHolder> {
    private Context mContext;
    private List<Task_> mTasks;

    // Provide a reference to the views for each data item
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private View view;

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
        RadioGroup radioGroup = holder.view.findViewById(R.id.radioGroup);
        RadioButton radioButton = holder.view.findViewById(R.id.rd_task);
        TextView taskName = holder.view.findViewById(R.id.task_name);
        Button editTask = holder.view.findViewById(R.id.edit_task);
        Button deleteTask = holder.view.findViewById(R.id.delete_task);

        // Complete Task
        radioGroup.clearCheck(); // after checking the first, there was always one that was checked without being checked by the user
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = group.findViewById(R.id.rd_task);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked)
                {
                    // Changes the textview's text to "Checked: example radiobutton text"
                    DocumentReference documentReference = MainActivity.db.collection("tasks")
                            .document(mTasks.get(position).getDocumentId());
                    documentReference.update("complete", true)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(mContext,"Task completed",Toast.LENGTH_LONG).show();

                                    mTasks.remove(position);
                                    setTasks(mTasks);
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
        });

        // Set Task Name
        taskName.setText(mTasks.get(position).getName());

        // Edit Task
        editTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText edittext = new EditText(mContext);
                edittext.setText(mTasks.get(position).getName());

                // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
                AlertDialog.Builder builderEdit = new AlertDialog.Builder(mContext);

                // 2. Chain together various setter methods to set the dialog characteristics
                builderEdit.setView(edittext)
                        .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                if (!TextUtils.isEmpty(edittext.getText().toString().trim())) {
                                    DocumentReference documentReference = MainActivity.db.collection("tasks")
                                            .document(mTasks.get(position).getDocumentId());
                                    documentReference.update("task_name", edittext.getText().toString())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mTasks.get(position).setName(edittext.getText().toString());
                                                    setTasks(mTasks);
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

        // Delete Task
        deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("Delete " + mTasks.get(position).getName() + "?")
                        //.setTitle("Task deletion confirmation")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                MainActivity.db.collection("tasks")
                                        .document(mTasks.get(position).getDocumentId())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //Log.d("DELETE", "DocumentSnapshot successfully deleted!");
                                                mTasks.remove(position);
                                                setTasks(mTasks);
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
