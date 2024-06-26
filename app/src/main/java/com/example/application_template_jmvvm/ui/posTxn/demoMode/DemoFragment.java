package com.example.application_template_jmvvm.ui.posTxn.demoMode;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.R;

public class DemoFragment extends Fragment {
    private MainActivity mainActivity;
    public DemoFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_demo, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences("myprefs", Context.MODE_PRIVATE);
        boolean isEnabled = sharedPreferences.getBoolean("demo_mode", false);
        SwitchCompat demoStatus = view.findViewById(R.id.demoSwitch);
        demoStatus.setChecked(isEnabled);
        view.findViewById(R.id.demoSwitch).setOnClickListener(v -> changeMode(sharedPreferences, demoStatus.isChecked()));
    }

    private void changeMode(SharedPreferences sharedPreferences, boolean isEnabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("demo_mode", isEnabled);
        editor.apply();
    }
}
