package com.savanto.splanner;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Model[]> {
    private static final int LOADER_GOALS = 0;
    private static final int LOADER_TASKS = 1;
    private static final int LOADER_TIMES = 2;

    private ListView goalsList;
    private ListView tasksList;
    private ListView timesList;

    private long selectedGoalId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main_activity);

        this.goalsList = (ListView) this.findViewById(R.id.list_goals);
        this.tasksList = (ListView) this.findViewById(R.id.list_tasks);
        this.timesList = (ListView) this.findViewById(R.id.list_times);

        final LoaderManager lm = this.getSupportLoaderManager();
        lm.initLoader(LOADER_GOALS, null, this);
        lm.initLoader(LOADER_TASKS, null, this);
        lm.initLoader(LOADER_TIMES, null, this);

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
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position,
                                           final long id) {
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.dialog_delete_goal_title)
                    .setMessage(R.string.dialog_delete_goal_message)
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (DatabaseHelper.getInstance(MainActivity.this).deleteGoal(id)) {
                                if (((CheckedTextView) view).isChecked()) {
                                    MainActivity.this.selectedGoalId = 0;
                                }
                                lm.restartLoader(LOADER_GOALS, null, MainActivity.this);
                                lm.restartLoader(LOADER_TASKS, null, MainActivity.this);
                                lm.restartLoader(LOADER_TIMES, null, MainActivity.this);
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
                MainActivity.this.selectedGoalId = id;
                lm.restartLoader(LOADER_TASKS, null, MainActivity.this);
            }
        });

        /* Tasks list */
        this.findViewById(R.id.btn_add_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.this.selectedGoalId == 0) {
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
                                    MainActivity.this.selectedGoalId, task.getText().toString())) {
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
                    .setTitle(R.string.dialog_delete_task_title)
                    .setMessage(R.string.dialog_delete_task_message)
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (DatabaseHelper.getInstance(MainActivity.this).deleteTask(id)) {
                                lm.restartLoader(LOADER_TASKS, null, MainActivity.this);
                                lm.restartLoader(LOADER_TIMES, null, MainActivity.this);
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
                                    (Model.Task) parent.getAdapter().getItem(position),
                                    time.getCurrentHour() * 3600 + time.getCurrentMinute() * 60)) {
                                lm.restartLoader(LOADER_TIMES, null, MainActivity.this);
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

        /* Schedule */
        this.timesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                           final long id) {
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.dialog_delete_time_title)
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (DatabaseHelper.getInstance(MainActivity.this).deleteTime(id)) {
                                lm.restartLoader(LOADER_TIMES, null, MainActivity.this);
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
    }

    @Override
    public Loader<Model[]> onCreateLoader(final int id, final Bundle args) {
        return new SAsyncTaskLoader<Model[]>(this) {
            @Override
            public Model[] loadInBackground() {
                switch (id) {
                case LOADER_TASKS:
                    return DatabaseHelper.getInstance(MainActivity.this).getTasks(
                                    MainActivity.this.selectedGoalId);
                case LOADER_TIMES:
                    return DatabaseHelper.getInstance(MainActivity.this).getTimes();
                case LOADER_GOALS:
                    // FALL-THROUGH
                default:
                    return DatabaseHelper.getInstance(MainActivity.this).getGoals();
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Model[]> loader, Model[] items) {
        if (items == null) {
            items = new Model[0];
        }
        switch (loader.getId()) {
        case LOADER_GOALS:
            final SPlannerAdapter adapter = new SPlannerAdapter(
                    this, R.layout.list_item_single, items);
            this.goalsList.setAdapter(adapter);
            final int nGoals = adapter.getCount();
            for (int pos = 0; pos < nGoals; ++pos) {
                if (adapter.getItemId(pos) == this.selectedGoalId) {
                    this.goalsList.setItemChecked(pos, true);
                    break;
                }
            }
            break;
        case LOADER_TASKS:
            this.tasksList.setAdapter(new SPlannerAdapter(this, R.layout.list_item, items));
            break;
        case LOADER_TIMES:
            this.timesList.setAdapter(new TimesAdapter(this, items));
            break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Model[]> loader) { /* NOP */ }


    private static final class DialogCancelListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }


    private static class SPlannerAdapter extends ArrayAdapter<Model> {
        public SPlannerAdapter(Context context, int resource, Model[] items) {
            super(context, resource, items);
        }

        @Override
        public long getItemId(int position) {
            return this.getItem(position).id;
        }
    }


    private static final class TimesAdapter extends SPlannerAdapter {
        private static final int TIME = 0;
        private static final int TASK = 1;
        private static final DateFormat TIME_FORMAT = DateFormat.getDateTimeInstance(
                DateFormat.SHORT, DateFormat.SHORT);
        static {
            TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        private final LayoutInflater inflater;

        public TimesAdapter(Context context, Model[] items) {
            super(context, 0, items);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.list_item_time, parent, false);
            }
            final Model.Time time = (Model.Time) this.getItem(position);

            ((TextView) ((ViewGroup) convertView).getChildAt(TIME)).setText(
                    TIME_FORMAT.format(new Date(time.time * 1000)));
            ((TextView) ((ViewGroup) convertView).getChildAt(TASK)).setText(time.text);

            return convertView;
        }
    }
}
