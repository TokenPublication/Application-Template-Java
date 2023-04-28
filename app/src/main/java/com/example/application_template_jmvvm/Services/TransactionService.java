package com.example.application_template_jmvvm.Services;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.application_template_jmvvm.Uicomponents.MainActivity;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;

import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class TransactionService implements InfoDialogListener {

    private String testString = "one";
    private Observable<ContentValues> observable;
    private Observer<ContentValues> observer;
    private InfoDialog dialog;
    public void doInBackground(MainActivity main, Context context, ContentValues values) {

        dialog = main.showInfoDialog(InfoDialog.InfoType.Progress, "Progress",false);
        observable = Observable.just(values)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
        observer = new Observer<ContentValues>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i("Disposed","Dispose");
            }

            @Override
            public void onNext(ContentValues contentValues) {
                for (int i = 0; i <= 10; i++){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    dialog.update(InfoDialog.InfoType.Progress,"Progress: "+(i*10));
                }
                Log.i("Values",contentValues.toString());
            }

            @Override
            public void onError(Throwable e) {
                Log.i("Error","Error");
            }

            @Override
            public void onComplete() {
                Log.i("Complete","Complete");
                main.showInfoDialog(InfoDialog.InfoType.Confirmed, "Confirmed",false);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                main.finish();
            }
        };
        observable.subscribe(observer);
    }

    @Override
    public void confirmed(int i) {

    }

    @Override
    public void canceled(int i) {

    }
}
