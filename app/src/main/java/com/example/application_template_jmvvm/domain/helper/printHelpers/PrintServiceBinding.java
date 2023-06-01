package com.example.application_template_jmvvm.domain.helper.printHelpers;

import android.os.IBinder;
import android.os.RemoteException;

import com.token.printerlib.IPrinterService;

import java.lang.reflect.Method;

public class PrintServiceBinding {
    private IPrinterService printerService;
    private Runnable runnable;

    public PrintServiceBinding() {
        Method method = null;
        try {
            method = Class.forName("android.os.ServiceManager")
                    .getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, "PrinterService");
            printerService = IPrinterService.Stub.asInterface(binder);
            executeRunnable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void executeRunnable() {
        if (printerService != null && runnable != null) {
            synchronized (runnable) {
                runnable.run();
                runnable = null;
            }
        }
    }

    public void print(String text) {
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    printerService.printText(text);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
        executeRunnable();
    }

    public void printBitmap(String name, int verticalMargin) {
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    printerService.printBitmap(name, verticalMargin);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
        executeRunnable();
    }
}
