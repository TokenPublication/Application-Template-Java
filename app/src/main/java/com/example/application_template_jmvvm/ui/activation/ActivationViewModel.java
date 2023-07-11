package com.example.application_template_jmvvm.ui.activation;

import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.data.repository.ActivationRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ActivationViewModel extends ViewModel {

    @Inject
    public ActivationRepository activationRepository;

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

    public boolean isTableEmpty() {
        return activationRepository.isTableEmpty();
    }

    public void deleteAll() {
        activationRepository.deleteAll();
    }
}