package com.example.application_template_jmvvm.ui.activation;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.data.repository.ActivationRepository;
import com.example.application_template_jmvvm.ui.sale.CardViewModel;
import com.example.application_template_jmvvm.utils.objects.InfoDialogData;
import com.example.application_template_jmvvm.utils.printHelpers.PrintHelper;
import com.token.uicomponents.infodialog.InfoDialog;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * We use the HiltViewModel annotation to inform Hilt (Dependency Injection) that this class is our ViewModel.
 * Later on, we can use this ViewModel without passing its parameters, thanks to Hilt's automatic injection.
 * We don't need to manually call the repository when calling the ViewModel class since we define our repository
 * in the AppModule with Hilt.
 */
@HiltViewModel
public class ActivationViewModel extends ViewModel {
    @Inject
    public ActivationRepository activationRepository;
    private MutableLiveData<InfoDialogData> infoDialogLiveData = new MutableLiveData<>();

    @Inject
    public ActivationViewModel(ActivationRepository activationRepository) {
        this.activationRepository = activationRepository;
    }

    public ActivationRepository getActivationRepository() {
        return activationRepository;
    }

    public void updateConnection(String ip, String port, String old_ip) {
        activationRepository.updateConnection(ip, port, old_ip);
    }

    public void updateActivation(String terminalId, String merchantId, String ip) {
        activationRepository.updateActivation(terminalId, merchantId, ip);
    }

    public String getMerchantId() {
        return activationRepository.getMerchantId();
    }

    public String getTerminalId() {
        return activationRepository.getTerminalId();
    }

    public String getHostIP() {
        return activationRepository.getHostIP();
    }

    public String getHostPort() {
        return activationRepository.getHostPort();
    }

    public MutableLiveData<InfoDialogData> getInfoDialogLiveData() {
        return infoDialogLiveData;
    }

    public void setInfoDialogLiveData(InfoDialogData infoDialogData) {
        infoDialogLiveData.postValue(infoDialogData);
    }

    /**
     * It runs functions in parallel while ui updating dynamically in main thread
     * Additionally, in IO coroutine thread make setEMVConfiguration method
     */
    public void setupRoutine(MainActivity mainActivity, CardViewModel cardViewModel) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Processing, mainActivity.getApplicationContext().getString(R.string.starting_activation)));
        Observable<Boolean> observable = Observable.just(true)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
        Observer<Boolean> observer = new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i("Disposed","Dispose");
            }

            @Override
            public void onNext(Boolean bool) {
                cardViewModel.setEMVConfiguration(mainActivity, false);
            }

            @Override
            public void onError(Throwable e) {
                Log.i("Error","Error");
            }

            @Override
            public void onComplete() {
                Log.i("Complete","Complete");
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    mainHandler.post(() -> setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Progress, mainActivity.getApplicationContext().getString(R.string.parameter_loading))));
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        mainHandler.post(() -> setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Confirmed, mainActivity.getApplicationContext().getString(R.string.member_act_completed))));
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            mainHandler.post(() -> setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Progress, mainActivity.getApplicationContext().getString(R.string.rkl_loading))));
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                mainHandler.post(() -> setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Confirmed, mainActivity.getApplicationContext().getString(R.string.rkl_loaded))));
                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    mainHandler.post(() -> setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Progress, mainActivity.getApplicationContext().getString(R.string.key_block_loading))));
                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                        mainHandler.post(() -> setInfoDialogLiveData(new InfoDialogData(InfoDialog.InfoType.Confirmed, mainActivity.getApplicationContext().getString(R.string.activation_completed))));
                                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                            PrintHelper.PrintSuccess(mainActivity.getApplicationContext());
                                            mainActivity.finish();
                                        }, 2000);
                                    }, 2000);
                                }, 2000);
                            }, 2000);
                        }, 2000);
                    }, 2000);
                }, 2000);
            }
        };
        observable.subscribe(observer);
    }
}
