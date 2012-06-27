/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ts.examples;

import com.sun.deploy.uitoolkit.impl.fx.ui.FXAboutDialog;
import java.util.concurrent.*;
import javafx.application.Platform;

/**
 * This class acts as a proxy to the existing Future object
 * 
 * @author boxcat
 */
public class FutureManager {

    private final ScheduledExecutorService srv = Executors.newScheduledThreadPool(3);

    private FutureTask<String> current = null;
    private final static String ALREADY_CANCELLED = "Cancelled";
    private final FutureController controller;

    FutureManager(FutureController controller) {
        this.controller = controller;
    }
    
    public boolean cancel(boolean mayInterruptIfRunning) {
        controller.hideSpinner();
        return current.cancel(mayInterruptIfRunning);
    }

    public boolean isCancelled() {
        return current.isCancelled();
    }

    public boolean isDone() {
        return current.isDone();
    }

    public String get() throws InterruptedException, ExecutionException {
        if (current.isCancelled()) return ALREADY_CANCELLED;
        return current.get();
    }

    public String get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return current.get(timeout, unit);
    }
    
    /**
     * Creates a new Future object and sets it running on the threadpool
     * @param waitTime
     * @param message 
     */
    public synchronized void createFuture(final int waitTime, final String message) {
        if (current != null) current.cancel(true); 
        current = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                controller.showSpinner();
                Thread.sleep(waitTime * 1000);
                controller.hideSpinner();
                return message;
            }
            
        });
        srv.schedule(current, 500, TimeUnit.MILLISECONDS);
    }
    
}
