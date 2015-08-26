package com.savanto.splanner;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<String[]> {

    private enum LoaderType {
        GOALS, TASKS, DAY
        ;

        public static LoaderType get(int index) {
            final LoaderType[] values = LoaderType.values();
            return index >= 0 && index < values.length ? values[index] : GOALS;
        }
    }

    private ListView goalsList;
    private ListView tasksList;
    private ListView dayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main_activity);

        this.goalsList = (ListView) this.findViewById(R.id.list_goals);
        this.tasksList = (ListView) this.findViewById(R.id.list_tasks);
        this.dayList = (ListView) this.findViewById(R.id.list_day);

        final LoaderManager lm = this.getSupportLoaderManager();
        for (final LoaderType type : LoaderType.values()) {
            lm.initLoader(type.ordinal(), null, this);
        }
    }

    @Override
    public Loader<String[]> onCreateLoader(final int id, Bundle args) {
        return new SAsyncTaskLoader<String[]>(this) {
            @Override
            public String[] loadInBackground() {
                switch (LoaderType.get(id)) {
                case TASKS:
                    return DatabaseHelper.getInstance(MainActivity.this).getTasks();
                case DAY:
                    return DatabaseHelper.getInstance(MainActivity.this).getDay();
                case GOALS:
                    // FALL-THROUGH
                default:
                    return DatabaseHelper.getInstance(MainActivity.this).getGoals();

                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        switch (LoaderType.get(loader.getId())) {
        case GOALS:
            this.goalsList.setAdapter(
                    new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data));
            break;
        case TASKS:
            this.tasksList.setAdapter(
                    new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data));
            break;
        case DAY:
            this.dayList.setAdapter(
                    new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data));
            break;
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) { /* NOP */ }
}
