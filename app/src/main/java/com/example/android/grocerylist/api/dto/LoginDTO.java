package com.example.android.grocerylist.api.dto;

/**
 * Created by lapa on 13.05.16.
 */
public class LoginDTO {

   private String email;
   private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
