package com.nelsonlopes.tasks.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.nelsonlopes.tasks.R;
import com.nelsonlopes.tasks.models.Project;
import com.nelsonlopes.tasks.models.Task_;

import java.util.List;

public class WidgetUpdateService extends IntentService {
    public static final String WIDGET_UPDATE_ACTION = "update_widget";
    private List<Task_> mTasks = null;

    public WidgetUpdateService() {
        super("WidgetServiceUpdate");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null && intent.getAction().equals(WIDGET_UPDATE_ACTION)) {
            Project project = intent.getParcelableExtra(getString(R.string.parcel_project));
            mTasks = project.getTasks();

            Log.d("WIDGET SERVICE TASKS", String.valueOf(mTasks.size()));
            Log.d("WIDGET SERVICE PROJECT", String.valueOf(project.getTasks().size()));

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, TasksProvider.class));
            TasksProvider.updateAppWidget(this, appWidgetManager, appWidgetIds, mTasks);
        }
    }
}

