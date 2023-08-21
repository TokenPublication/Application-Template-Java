package com.example.application_template_jmvvm.ui.service;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.AppTemp;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.ui.sale.CardViewModel;
import com.example.application_template_jmvvm.utils.objects.InfoDialogData;
import com.token.uicomponents.infodialog.InfoDialog;
import com.tokeninc.deviceinfo.DeviceInfo;
import com.tokeninc.libtokenkms.KMSWrapperInterface;
import com.tokeninc.libtokenkms.TokenKMS;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ServiceViewModel extends ViewModel {
    private MutableLiveData<InfoDialogData> infoDialogLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isConnectedLiveData = new MutableLiveData<>();
    @Inject
    public ServiceViewModel() { }

    public void ServiceRoutine(MainActivity mainActivity, CardViewModel cardViewModel) {
        Context context = mainActivity.getApplicationContext();
        AppTemp appTemp = (AppTemp) context;
        DeviceInfo deviceInfo = new DeviceInfo(context);
        TokenKMS tokenKMS = new TokenKMS();
        CountDownTimer deviceInfoTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) { }

            @Override
            public void onFinish() {
                setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Error, mainActivity.getString(R.string.device_info_service_Error)));
            }
        };
        deviceInfoTimer.start();
        deviceInfo.getFields(
                fields -> {
                    Log.d("Device Info:", "Success");
                    if (fields == null) {
                        setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Error, mainActivity.getString(R.string.device_info_service_Error)));
                    }

                    appTemp.setCurrentFiscalID(fields[0]);
                    appTemp.setCurrentDeviceMode(fields[1]);
                    appTemp.setCurrentCardRedirection(fields[2]);

                    deviceInfo.unbind();
                    deviceInfoTimer.cancel();
                    tokenKMS.init(context, new KMSWrapperInterface.InitCallbacks() {
                        @Override
                        public void onInitSuccess() {
                            Log.d("KMS Init Success:", "Init Success");
                            final boolean[] isCancelled = {false};
                            CountDownTimer cardServiceTimer = new CountDownTimer(30000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) { }

                                @Override
                                public void onFinish() {
                                    isCancelled[0] = true;
                                    setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Declined, mainActivity.getString(R.string.card_service_error)));
                                }
                            };
                            cardServiceTimer.start();
                            cardViewModel.initializeCardServiceBinding(mainActivity);

                            cardViewModel.getIsCardServiceConnect().observe(mainActivity, isConnected -> {
                                if (isConnected && !isCancelled[0]) {
                                    Log.d("Card Service Bind:", "Success");
                                    cardServiceTimer.cancel();
                                    cardViewModel.setEMVConfiguration(mainActivity, true);
                                    setIsConnectedLiveData(true);
                                }
                            });
                        }

                        @Override
                        public void onInitFailed() {
                            Log.v("Token KMS onInitFailed", "KMS Init Failed");
                            setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Error, mainActivity.getString(R.string.kms_service_error)));
                        }
                    });
                },
                DeviceInfo.Field.FISCAL_ID, DeviceInfo.Field.OPERATION_MODE, DeviceInfo.Field.CARD_REDIRECTION
        );
    }

    public MutableLiveData<InfoDialogData> getInfoDialogLiveData() {
        return infoDialogLiveData;
    }

    public void setInfoDialogLiveData(InfoDialogData infoDialogData) {
        infoDialogLiveData.postValue(infoDialogData);
    }

    public MutableLiveData<Boolean> getIsConnectedLiveData() {
        return isConnectedLiveData;
    }

    public void setIsConnectedLiveData(Boolean isConnected) {
        isConnectedLiveData.postValue(isConnected);
    }
}
