package ru.vit4liy.backend.data.model;

public class Phone {
    private long id;
    private String corpPhoneNumber;
    private String publicPhoneNumber;
    private long lastChecked;

    public Phone(long id, String corpPhoneNumber, String publicPhoneNumber, long lastChecked) {
        this.id = id;
        this.corpPhoneNumber = corpPhoneNumber;
        this.publicPhoneNumber = publicPhoneNumber;
        this.lastChecked = lastChecked;
    }

    public Phone() {
    }

    public long getId() {
        return id;
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
}
