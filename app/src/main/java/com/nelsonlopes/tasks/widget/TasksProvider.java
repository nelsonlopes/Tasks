package com.nelsonlopes.tasks.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.nelsonlopes.tasks.models.Task_;

import com.nelsonlopes.tasks.R;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class TasksProvider extends AppWidgetProvider {

    public static List<Task_> mTasks;

    public TasksProvider() {

    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetIds[], List<Task_> tasks) {
        mTasks = tasks;

        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, ListViewService.class);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tasks_widget);
            views.setRemoteAdapter(R.id.list_view_widget, intent);
            ComponentName component = new ComponentName(context, TasksProvider.class);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.list_view_widget);
            appWidgetManager.updateAppWidget(component, views);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

