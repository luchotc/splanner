package com.savanto.splanner;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
    import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends AppCompatActivity {
    private ListView goalsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main_activity);

        this.goalsList = (ListView) this.findViewById(R.id.list_goals);

        this.getSupportLoaderManager().initLoader(0, null, new GoalsLoader());
    }


    private final class GoalsLoader implements LoaderManager.LoaderCallbacks<String[]> {
        @Override
        public Loader<String[]> onCreateLoader(int id, Bundle args) {
            return new SAsyncTaskLoader<String[]>(MainActivity.this) {
                @Override
                public String[] loadInBackground() {
                    return DatabaseHelper.getInstance(MainActivity.this).getGoals();
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<String[]> loader, String[] goals) {
            MainActivity.this.goalsList.setAdapter(new ArrayAdapter<>(
                    MainActivity.this,
                    android.R.layout.simple_list_item_1,
                    goals
            ));
        }

        @Override
        public void onLoaderReset(Loader<String[]> loader) {

        }
    }
}
