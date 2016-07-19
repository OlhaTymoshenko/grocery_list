package com.example.android.grocerylist.api.dto;

/**
 * Created by lapa on 12.04.16.
 */
public class TaskDTO {

    String title;
    Integer id;

    public String getTitle() {
        return title;
    }

    public Integer getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
