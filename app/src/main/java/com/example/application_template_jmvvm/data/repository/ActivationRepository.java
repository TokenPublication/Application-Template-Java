package com.example.application_template_jmvvm.data.repository;

import com.example.application_template_jmvvm.data.database.activation.Activation;
import com.example.application_template_jmvvm.data.database.activation.ActivationDao;

import javax.inject.Inject;

/**
 * This class for the operations in Activation like update.
 * The ActivationDao parameter represents a Data Access Object
 * injected through Dependency Injection using the @Inject annotation.
 */
public class ActivationRepository {
    private ActivationDao activationDao;

    @Inject
    public ActivationRepository(ActivationDao activationDao) {
        this.activationDao = activationDao;
    }

    public void updateConnection(String ip, String port, String old_ip) {
        activationDao.updateConnection(ip, port, old_ip);
    }

    public void updateActivation(String terminalId, String merchantId, String ip) {
        activationDao.updateActivation(terminalId, merchantId, ip);
    }

    public String getMerchantId() {
        return activationDao.getMerchantId();
    }

    public String getTerminalId() {
        return activationDao.getTerminalId();
    }

    public String getHostIP() {
        return activationDao.getHostIP();
    }

    public String getHostPort() {
        return activationDao.getHostPort();
    }

    public boolean isTableEmpty() {
        return activationDao.isTableEmpty() == 0;
    }

}
