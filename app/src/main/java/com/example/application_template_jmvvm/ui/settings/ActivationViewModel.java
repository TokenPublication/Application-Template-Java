package com.example.application_template_jmvvm.ui.settings;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.example.application_template_jmvvm.R;
import com.example.application_template_jmvvm.MainActivity;
import com.example.application_template_jmvvm.data.database.repository.ActivationRepository;

public class ActivationViewModel extends ViewModel {

    private ActivationRepository activationRepository;

    public ActivationViewModel(ActivationRepository activationRepository) {
        this.activationRepository = activationRepository;
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