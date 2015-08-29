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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Item[]> {
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
        lm.initLoader(LOADER_DAY, null, this);

        /* Goals list */
        this.findViewById(R.id.btn_add_goal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = LayoutInflater.from(MainActivity.this).inflate(
                        R.layout.add_dialog, null);
                final EditText goal = (EditText) view.findViewById(R.id.new_item);
                goal.setHint(R.string.dialog_add_goal_hint);
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.dialog_add_goal)
                    .setView(view)
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
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
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
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
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
                final View view = LayoutInflater.from(MainActivity.this).inflate(
                        R.layout.add_dialog, null, false);
                final EditText task = (EditText) view.findViewById(R.id.new_item);
                task.setHint(R.string.dialog_add_task_hint);
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.dialog_add_task)
                    .setView(view)
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
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
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
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
                return true;
            }
        });
        this.tasksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, Long.toString(id), Toast.LENGTH_SHORT).show();
            }
        });
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
                    return null;//DatabaseHelper.getInstance(MainActivity.this).getDay();
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
            this.goalsList.setAdapter(
                    new SPlannerAdapter(this, R.layout.list_item_single, items));
            break;
        case LOADER_TASKS:
            this.tasksList.setAdapter(
                    new SPlannerAdapter(this, R.layout.list_item, items));
            break;
        case LOADER_DAY:
            this.dayList.setAdapter(
                    new SPlannerAdapter(this, R.layout.list_item, items));
            break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Item[]> loader) { /* NOP */ }


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
