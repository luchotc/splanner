package com.savanto.splanner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Item[]> {
    private static final String PREF_YESTERDAY = "com.savanto.splanner.Yesterday";
    private static final int LOADER_GOALS = 0;
    private static final int LOADER_TASKS = 1;
    private static final int LOADER_DAY = 2;

    private ListView goalsList;
    private ListView tasksList;
    private ListView dayList;

    private long selectedGoal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main_activity);

        this.goalsList = (ListView) this.findViewById(R.id.list_goals);
        this.tasksList = (ListView) this.findViewById(R.id.list_tasks);
        this.dayList = (ListView) this.findViewById(R.id.list_day);

        final LoaderManager lm = this.getSupportLoaderManager();
        lm.initLoader(LOADER_GOALS, null, this);
        lm.initLoader(LOADER_TASKS, null, this);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final long yesterday = prefs.getLong(PREF_YESTERDAY, 0);
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        final long today = calendar.getTimeInMillis() / 1000;
        if (true || today > yesterday) {
            DatabaseHelper.getInstance(this).clearDay();
            prefs.edit().putLong(PREF_YESTERDAY, today).apply();
        }
        lm.initLoader(LOADER_DAY, null, this);

        /* Goals list */
        this.findViewById(R.id.btn_add_goal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText goal = (EditText) LayoutInflater.from(MainActivity.this).inflate(
                        R.layout.add_dialog, null);
                goal.setHint(R.string.dialog_add_goal_hint);
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.dialog_add_goal)
                    .setView(goal)
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (DatabaseHelper.getInstance(MainActivity.this).insertGoal(
                                    goal.getText().toString())) {
                                lm.restartLoader(LOADER_GOALS, null, MainActivity.this);
                            } else {
                                Toast.makeText(
                                        MainActivity.this, R.string.error, Toast.LENGTH_SHORT)
                                    .show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, new DialogCancelListener())
                    .show();
            }
        });
        this.goalsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                           final long id) {
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.dialog_delete_goal)
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (DatabaseHelper.getInstance(MainActivity.this).deleteGoal(id)) {
                                lm.restartLoader(LOADER_GOALS, null, MainActivity.this);
                            } else {
                                Toast.makeText(
                                        MainActivity.this, R.string.error, Toast.LENGTH_SHORT)
                                    .show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, new DialogCancelListener())
                    .show();
                return true;
            }
        });
        this.goalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.this.selectedGoal = id;
                lm.restartLoader(LOADER_TASKS, null, MainActivity.this);
            }
        });

        /* Tasks list */
        this.findViewById(R.id.btn_add_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.this.selectedGoal == 0) {
                    Toast.makeText(MainActivity.this, R.string.select_goal, Toast.LENGTH_SHORT)
                        .show();
                    return;
                }
                final EditText task = (EditText) LayoutInflater.from(MainActivity.this).inflate(
                        R.layout.add_dialog, null, false);
                task.setHint(R.string.dialog_add_task_hint);
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.dialog_add_task)
                    .setView(task)
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (DatabaseHelper.getInstance(MainActivity.this).insertTask(
                                    MainActivity.this.selectedGoal, task.getText().toString())) {
                                lm.restartLoader(LOADER_TASKS, null, MainActivity.this);
                            } else {
                                Toast.makeText(
                                        MainActivity.this, R.string.error, Toast.LENGTH_SHORT)
                                    .show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, new DialogCancelListener())
                    .show();
            }
        });
        this.tasksList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                           final long id) {
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.dialog_delete_task)
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (DatabaseHelper.getInstance(MainActivity.this).deleteTask(id)) {
                                lm.restartLoader(LOADER_TASKS, null, MainActivity.this);
                            } else {
                                Toast.makeText(
                                        MainActivity.this, R.string.error, Toast.LENGTH_SHORT)
                                    .show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, new DialogCancelListener())
                    .show();
                return true;
            }
        });
        this.tasksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position,
                                    long id) {
                final TimePicker time = (TimePicker) LayoutInflater.from(MainActivity.this).inflate(
                        R.layout.time_dialog, null, false);
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.dialog_add_time)
                    .setView(time)
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (DatabaseHelper.getInstance(MainActivity.this).insertTime(
                                    time.getCurrentHour() * 3600 + time.getCurrentMinute() * 60,
                                    (Item) parent.getAdapter().getItem(position))) {
                                lm.restartLoader(LOADER_DAY, null, MainActivity.this);
                            } else {
                                Toast.makeText(
                                        MainActivity.this, R.string.error, Toast.LENGTH_SHORT)
                                    .show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, new DialogCancelListener())
                    .show();
            }
        });

        /* Day schedule */

    }

    @Override
    public Loader<Item[]> onCreateLoader(final int id, final Bundle args) {
        return new SAsyncTaskLoader<Item[]>(this) {
            @Override
            public Item[] loadInBackground() {
                switch (id) {
                case LOADER_TASKS:
                    return DatabaseHelper.getInstance(MainActivity.this).getTasks(
                                    MainActivity.this.selectedGoal);
                case LOADER_DAY:
                    return DatabaseHelper.getInstance(MainActivity.this).getDay();
                case LOADER_GOALS:
                    // FALL-THROUGH
                default:
                    return DatabaseHelper.getInstance(MainActivity.this).getGoals();
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Item[]> loader, Item[] items) {
        if (items == null) {
            items = new Item[0];
        }
        switch (loader.getId()) {
        case LOADER_GOALS:
            this.goalsList.setAdapter(new SPlannerAdapter(this, R.layout.list_item_single, items));
            break;
        case LOADER_TASKS:
            this.tasksList.setAdapter(new SPlannerAdapter(this, R.layout.list_item, items));
            break;
        case LOADER_DAY:
            this.dayList.setAdapter(new SPlannerAdapter(this, R.layout.list_item, items));
            break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Item[]> loader) { /* NOP */ }


    private static final class DialogCancelListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }


    private static final class SPlannerAdapter extends ArrayAdapter<Item> {
        public SPlannerAdapter(Context context, int resource, Item[] items) {
            super(context, resource, items);
        }

        @Override
        public long getItemId(int position) {
            return this.getItem(position).id;
        }
    }
}
