package com.example.application_template_jmvvm.ui.trigger;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.data.database.transaction.TransactionEntity;
import com.example.application_template_jmvvm.data.model.card.ICCCard;
import com.example.application_template_jmvvm.data.model.code.TransactionCode;
import com.example.application_template_jmvvm.data.model.response.OnlineTransactionResponse;
import com.example.application_template_jmvvm.data.repository.ActivationRepository;
import com.example.application_template_jmvvm.data.repository.BatchRepository;
import com.example.application_template_jmvvm.ui.sale.TransactionViewModel;
import com.example.application_template_jmvvm.utils.objects.InfoDialogData;
import com.token.uicomponents.infodialog.InfoDialog;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TriggerViewModel extends ViewModel {

    private MutableLiveData<Intent> intentLiveData  = new MutableLiveData<>();
    private MutableLiveData<InfoDialogData> infoDialogLiveData = new MutableLiveData<>();

    public void parameterRoutine(MainActivity mainActivity, AssetManager assetManager) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Progress, mainActivity.getApplicationContext().getString(R.string.parameter_loading)));
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Observable<Boolean> observable = Observable.just(true)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io());
            Observer<Boolean> observer = new Observer<Boolean>() {
                @Override
                public void onSubscribe(Disposable d) {
                    Log.i("Disposed","Dispose");
                }

                @Override
                public void onNext(Boolean bool) { }

                @Override
                public void onError(Throwable e) {
                    Log.i("Error","Error");
                }

                @Override
                public void onComplete() {
                    Log.i("Complete","Complete");
                    Intent resultIntent = new Intent();
                    Bundle bundle = new Bundle();
                    String clConfigFile = "";

                    try {
                        InputStream xmlCLStream = assetManager.open("custom_emv_cl_config.xml");
                        BufferedReader rCL = new BufferedReader(new InputStreamReader(xmlCLStream));
                        StringBuilder totalCL = new StringBuilder();
                        String line;
                        while ((line = rCL.readLine()) != null) {
                            totalCL.append(line).append('\n');
                        }
                        clConfigFile = totalCL.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    bundle.putString("clConfigFile", clConfigFile);

                    String bins = "[{\"cardRangeStart\":\"1111110000000\",\"cardRangeEnd\":\"1111119999999\",\"OwnerShip\":\"ISSUER\",\"CardType\":\"C\"}," +
                            "{\"cardRangeStart\":\"2222220000000\",\"cardRangeEnd\":\"2222229999999\",\"OwnerShip\":\"NONE\",\"CardType\":\"C\"}," +
                            "{\"cardRangeStart\":\"3333330000000\",\"cardRangeEnd\":\"3333339999999\",\"OwnerShip\":\"BRAND\",\"CardType\":\"C\"}]";
                    bundle.putString("BINS", bins);
                    bundle.putString("AllowedOperations", "{\"QrAllowed\":1,\"KeyInAllowed\":1}");
                    bundle.putString("SupportedAIDs", "[A0000000031010, A0000000041010, A0000000032010]");

                    resultIntent.putExtras(bundle);
                    mainHandler.post(() -> setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Confirmed, mainActivity.getApplicationContext().getString(R.string.parameter_load_successful))));
                    new Handler(Looper.getMainLooper()).postDelayed(() -> mainHandler.post(() -> setIntentLiveData(resultIntent)), 2000);
                }
            };
            observable.subscribe(observer);
        }, 3000);

    }

    public MutableLiveData<Intent> getIntentLiveData() {
        return intentLiveData;
    }

    public void setIntentLiveData(Intent intent) {
        intentLiveData.postValue(intent);
    }

    public MutableLiveData<InfoDialogData> getInfoDialogLiveData() {
        return infoDialogLiveData;
    }

    public void setInfoDialogLiveData(InfoDialogData infoDialogData) {
        infoDialogLiveData.postValue(infoDialogData);
    }

}