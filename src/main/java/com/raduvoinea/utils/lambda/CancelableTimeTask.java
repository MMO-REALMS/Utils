package com.raduvoinea.utils.lambda;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.TimerTask;

@Getter
public abstract class CancelableTimeTask extends TimerTask {

    @Setter
    private @Nullable Thread thread;
    private boolean canceled;

    public abstract void execute();

    @Override
    public void run() {
        if (canceled) {
            if (thread != null) {
                thread.interrupt();
            }
            return;
        }
        execute();
    }

    @Override
    public boolean cancel() {
        this.canceled = true;
        boolean result = super.cancel();
        if (thread != null) {
            thread.interrupt();
        }

        return result;
    }
}
