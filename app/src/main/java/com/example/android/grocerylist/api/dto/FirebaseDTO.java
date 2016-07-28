package com.example.android.grocerylist.api.dto;

/**
 * Created by lapa on 28.07.16.
 */
public class FirebaseDTO {

    private String deviceId;
    private String token;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
