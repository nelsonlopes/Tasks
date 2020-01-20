package com.nelsonlopes.tasks.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.nelsonlopes.tasks.R;
import com.nelsonlopes.tasks.models.Task_;

import java.util.List;

public class ListViewService extends RemoteViewsService {

    /**
     * @param intent intent that triggered this service
     * @return new ListViewsFactory Object with the appropriate implementation
     */
    public ListViewFactory onGetViewFactory(Intent intent) {
        return new ListViewFactory(this.getApplicationContext());
    }
}

class ListViewFactory implements RemoteViewsService.RemoteViewsFactory
{
    private Context mContext;
    private List<Task_> mTasks;

    public ListViewFactory(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate() {

    }

    //Very Important,this is the place where the data is being changed each time by the adapter.
    @Override
    public void onDataSetChanged() {
        mTasks = TasksProvider.mTasks;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mTasks == null)
            return 0;
        return mTasks.size();
    }

    /**
     * @param position position of current view in the ListView
     * @return a new RemoteViews object that will be one of many in the ListView
     */
    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(),
                R.layout.task_textview_widget_layout);
        views.setTextViewText(R.id.text_view_task_widget,
                mTasks.get(position).getName());

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}