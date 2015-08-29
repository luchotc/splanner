package com.savanto.splanner;


/* package */ abstract class Model {
    public final long id;
    public final String text;

    public Model(long id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }


    /* package */ static class Goal extends Model {
        public Goal(long id, String text) {
            super(id, text);
        }
    }


    /* package */ static class Task extends Goal {
        public final long goalId;

        public Task(long id, long goalId, String text) {
            super(id, text);
            this.goalId = goalId;
        }
    }


    /* package */ static final class Time extends Task {
        public final long taskId;
        public final long time;

        public Time(long id, long goalId, long taskId, long time, String text) {
            super(id, goalId, text);
            this.taskId = taskId;
            this.time = time;
        }
    }
}
