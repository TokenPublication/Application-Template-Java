package com.example.application_template_jmvvm.ui.trigger;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.R;
import com.token.uicomponents.infodialog.InfoDialog;
import com.token.uicomponents.infodialog.InfoDialogListener;

public class TriggerFragment extends Fragment implements InfoDialogListener {
    private MainActivity mainActivity;
    private TriggerViewModel triggerViewModel;
    private InfoDialog infoDialog;

    public TriggerFragment (MainActivity mainActivity, TriggerViewModel triggerViewModel) {
        this.mainActivity = mainActivity;
        this.triggerViewModel = triggerViewModel;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trigger, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startDummyParameterUploading();
    }

    /**
     * This function uploads parameters in IO thread, and update UI in main thread dynamically while uploading parameters.
     * After parameters are uploaded successfully it sends the result with intents to finish the main activity.
     */
    private void startDummyParameterUploading() {
        AssetManager assetManager = mainActivity.getAssets();
        triggerViewModel.parameterRoutine(mainActivity, assetManager);
        triggerViewModel.getInfoDialogLiveData().observe(getViewLifecycleOwner(), infoDialogData -> {
            if (infoDialogData.getType() == InfoDialog.InfoType.Confirmed) {
                infoDialog.update(infoDialogData.getType(), infoDialogData.getText());
            } else {
                infoDialog = mainActivity.showInfoDialog(infoDialogData.getType(), infoDialogData.getText(), false);
            }
        });
        triggerViewModel.getIntentLiveData().observe(getViewLifecycleOwner(), resultIntent -> {
            mainActivity.setResult(Activity.RESULT_OK, resultIntent);
            mainActivity.finish();
        });
    }

    @Override
    public void confirmed(int i) { }

    @Override
    public void canceled(int i) { }
}
