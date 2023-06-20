package com.example.application_template_jmvvm.ui.settings;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.application_template_jmvvm.data.repository.ActivationRepository;

public class ActivationViewModelFactory implements ViewModelProvider.Factory {
    private ActivationRepository activationRepository;

    public ActivationViewModelFactory(ActivationRepository activationRepository) {
        this.activationRepository = activationRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ActivationViewModel.class)) {
            return (T) new ActivationViewModel(activationRepository);
        }
        return null;
    }
}
