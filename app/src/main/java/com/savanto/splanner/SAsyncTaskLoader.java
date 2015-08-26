package com.savanto.splanner;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;


public abstract class SAsyncTaskLoader<T> extends AsyncTaskLoader<T> {
    private T data;

    public SAsyncTaskLoader(Context context) {
        super(context);
    }

    @Override
    public void deliverResult(T data) {
        this.data = data;

        // If the Loader is currently started, immediately deliver its results.
        if (this.isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        // If data are currently available, deliver them immediately.
        if (this.data != null) {
            this.deliverResult(this.data);
        }

        // If the data have changed since the last time they were loaded, or not currently
        // available, start a load.
        if (this.takeContentChanged() || this.data == null) {
            this.forceLoad();
        }
    }
}
