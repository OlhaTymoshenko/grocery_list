package ua.com.amicablesoft.android.grocerylist.api.dto;

/**
 * Created by lapa on 20.05.16.
 */
public class UserDTO {

   private String name;
   private String email;
   private String avatar;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
