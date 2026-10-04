package ru.vit4liy.backend.base.model.phone;

import ru.vit4liy.rsc.entity.DatabaseEntity;

public class Phone implements DatabaseEntity {
    private long id;
    private String corpPhoneNumber;
    private String publicPhoneNumber;
    private String ip;
    private long lastChecked;

    public Phone(long id, String corpPhoneNumber, String publicPhoneNumber, String ip, long lastChecked) {
        this.id = id;
        this.corpPhoneNumber = corpPhoneNumber;
        this.publicPhoneNumber = publicPhoneNumber;
        this.lastChecked = lastChecked;
    }

    public Phone() {
    }

    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long aLong) {
        this.id = aLong;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCorpPhoneNumber() {
        return corpPhoneNumber;
    }

    public void setCorpPhoneNumber(String corpPhoneNumber) {
        this.corpPhoneNumber = corpPhoneNumber;
    }

    public String getPublicPhoneNumber() {
        return publicPhoneNumber;
    }

    public void setPublicPhoneNumber(String publicPhoneNumber) {
        this.publicPhoneNumber = publicPhoneNumber;
    }

    public long getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(long lastChecked) {
        this.lastChecked = lastChecked;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
