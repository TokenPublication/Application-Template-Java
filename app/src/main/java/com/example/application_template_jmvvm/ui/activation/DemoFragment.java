package com.example.application_template_jmvvm.ui.activation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        TextView demoStatus = view.findViewById(R.id.demoStatusTextView);
        if (isEnabled) {
            demoStatus.setText(mainActivity.getString(R.string.demo_mode_enabled));
        } else {
            demoStatus.setText(mainActivity.getString(R.string.demo_mode_disabled));
        }
        view.findViewById(R.id.enableButton).setOnClickListener(v -> {
            changeMode(sharedPreferences, true);
            demoStatus.setText(mainActivity.getString(R.string.demo_mode_enabled));
        });
        view.findViewById(R.id.disableButton).setOnClickListener(v -> {
            changeMode(sharedPreferences, false);
            demoStatus.setText(mainActivity.getString(R.string.demo_mode_disabled));
        });
    }

    private void changeMode(SharedPreferences sharedPreferences, boolean isEnabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("demo_mode", isEnabled);
        editor.apply();
    }
}
