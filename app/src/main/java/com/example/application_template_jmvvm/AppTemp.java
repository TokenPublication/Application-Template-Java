package com.example.application_template_jmvvm;

import android.app.Application;

import com.tokeninc.deviceinfo.DeviceInfo;

public class AppTemp extends Application {
    private String currentDeviceMode = DeviceInfo.PosModeEnum.VUK507.name();
    private String currentFiscalID = null;
    private String currentCardRedirection = DeviceInfo.CardRedirect.NOT_ASSIGNED.name();

    @Override
    public void onCreate() {
        super.onCreate();
        startDeviceInfo();
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

    private void startDeviceInfo(){
        DeviceInfo deviceInfo = new DeviceInfo(this);
        deviceInfo.getFields(
                fields -> {
                    if (fields == null) return;
                    // fields is the string array that contains info in the requested order

                    this.setCurrentFiscalID(fields[0]);
                    this.setCurrentDeviceMode(fields[1]);
                    this.setCurrentCardRedirection(fields[2]);

                    deviceInfo.unbind();
                },
                DeviceInfo.Field.FISCAL_ID, DeviceInfo.Field.OPERATION_MODE, DeviceInfo.Field.CARD_REDIRECTION
        );
    }
}
