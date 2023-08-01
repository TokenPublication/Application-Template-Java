package com.example.application_template_jmvvm;

import android.app.Application;

import com.tokeninc.deviceinfo.DeviceInfo;

import dagger.hilt.android.HiltAndroidApp;

/**
 * Hilt needs to know application, Application should be annotated with @HiltAndroidApp
 * Also it set the device info contains Fiscal ID, cardRedirection and Device Mode there.
 */
@HiltAndroidApp
public class AppTemp extends Application {
    private String currentDeviceMode = DeviceInfo.PosModeEnum.VUK507.name();
    private String currentFiscalID = null;
    private String currentCardRedirection = DeviceInfo.CardRedirect.NOT_ASSIGNED.name();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public String getCurrentDeviceMode() {
        return currentDeviceMode;
    }

    public void setCurrentDeviceMode(String currentDeviceMode) {
        this.currentDeviceMode = currentDeviceMode;
    }

    public String getCurrentFiscalID() {
        return currentFiscalID;
    }

    public void setCurrentFiscalID(String currentFiscalID) {
        this.currentFiscalID = currentFiscalID;
    }

    public String getCurrentCardRedirection() {
        return currentCardRedirection;
    }

    public void setCurrentCardRedirection(String currentCardRedirection) {
        this.currentCardRedirection = currentCardRedirection;
    }
}
