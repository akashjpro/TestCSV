package com.adida.aka.testcsv;

import android.os.AsyncTask;

/**
 * Created by tmha on 7/7/2017.
 */

public interface AsyncTaskExecInterface {
    <T> void execute(AsyncTask<T,?,?> task, T... args);
}
