package ru.vit4liy.base.model.phone;

import ru.vit4liy.base.distribution.PrimaryId;
import ru.vit4liy.rsc.entity.DatabaseEntity;

import java.util.Objects;

public class Phone implements DatabaseEntity {
    @PrimaryId private long phoneId;
    private String corpPhoneNumber;
    private String publicPhoneNumber;
    private String ip;
    private long lastChecked;

    public Phone(long phoneId, String corpPhoneNumber, String publicPhoneNumber, String ip, long lastChecked) {
        this.phoneId = phoneId;
        this.corpPhoneNumber = corpPhoneNumber;
        this.publicPhoneNumber = publicPhoneNumber;
        this.lastChecked = lastChecked;
    }

    public Phone() {
    }

    public Long getId() {
        return phoneId;
    }

    @Override
    public void setId(Long aLong) {
        this.phoneId = aLong;
    }

    public void setId(long id) {
        this.phoneId = id;
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Phone phone = (Phone) object;
        return Objects.equals(corpPhoneNumber, phone.corpPhoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(corpPhoneNumber);
    }
}
