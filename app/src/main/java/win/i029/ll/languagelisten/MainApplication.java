
package win.i029.ll.languagelisten;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Calendar;

public class MainApplication extends Application{
    private final static String TAG = "study-Application";

    private static MainApplication mInstance = null;

    public MainApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        File cachedDir = new File(getTempoPath());
        for( File f : cachedDir.listFiles()) {
            Calendar cal = Calendar.getInstance();
            long currentTime = cal.getTimeInMillis();
            if (f.isFile() && f.getName().endsWith(".wav")) {
                long timeout = currentTime - f.lastModified();
                Log.d(TAG, f.getAbsolutePath() + " time : "
                        + timeout / 1000.0 / 3600.0 / 24 + " days");
                if( timeout > TIMEOUT) {
                    try {
                        Log.e(TAG, f.getAbsolutePath() + " too old , delete");
                        f.delete();
                    } catch (Exception e) {
                        Log.e(TAG, "Clean Cache Error : " + f.getAbsolutePath());
                    }
                }
            }
        }
    }

    private long TIMEOUT = 1000 * 60 * 60 * 24 * 10 ;// ms
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static MainApplication getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        return getInstance().getApplicationContext();
    }

    private String getTempoPath() {
        return getCacheDir().getPath();
    }
}
