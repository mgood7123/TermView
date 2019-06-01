package api28.android.app;

import android.os.Handler;

public class Activity {

    private Thread mUiThread;
    final Handler mHandler = new Handler();


    /**
     *
     * THIS IS COPIED DIRECTLY FROM THE Android API 28 Platform (Android PIE) SOURCE CODE
     *
     *
     * Runs the specified action on the UI thread. If the current thread is the UI
     * thread, then the action is executed immediately. If the current thread is
     * not the UI thread, the action is posted to the event queue of the UI thread.
     *
     * @param action the action to run on the UI thread
     */
    public final void runOnUiThread(Runnable action) {
        if (Thread.currentThread() != mUiThread) {
            mHandler.post(action);
        } else {
            action.run();
        }
    }
}
