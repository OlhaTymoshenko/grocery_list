package ua.com.amicablesoft.android.grocerylist.model;

/**
 * Created by lapa on 09.04.16.
 */
public class TaskModel {

    private String itemName;
    private int itemId;
    private Integer remoteId;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public Integer getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(Integer remoteId) {
        this.remoteId = remoteId;
    }
}
