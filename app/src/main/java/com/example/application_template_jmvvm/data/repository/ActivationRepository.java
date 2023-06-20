package com.example.application_template_jmvvm.data.repository;

import com.example.application_template_jmvvm.data.database.activation.ActivationDao;
import com.example.application_template_jmvvm.data.database.activation.ActivationEntity;

public class ActivationRepository {
    private ActivationDao activationDao;

    public ActivationRepository(ActivationDao activationDao) {
        this.activationDao = activationDao;
        initializeActivation();
    }

    public void initializeActivation(){
        if (isTableEmpty()){
            ActivationEntity activationEntity = new ActivationEntity();
            activationEntity.setColIP("195.87.189.169");
            activationEntity.setColPort("1051");
            activationDao.insertActivation(activationEntity);
        }
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

    public void deleteAll() {
        activationDao.deleteAll();
    }
}
