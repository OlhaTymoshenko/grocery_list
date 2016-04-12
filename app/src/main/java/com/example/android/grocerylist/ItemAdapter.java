package com.example.android.grocerylist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lapa on 11.04.16.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private ArrayList<TaskModel> data;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final CheckBox checkBox;
        final TextView textView;
        TaskModel item;

        public ViewHolder(View view) {
            super(view);
            checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        MainActivity.DeleteItemsTask itemsTask = new MainActivity.DeleteItemsTask
                                (buttonView.getContext().getApplicationContext());
                        itemsTask.execute(item);
                    }
                }
            });
            textView = (TextView) view.findViewById(R.id.list_item_text_view);
        }
    }

    public ItemAdapter(ArrayList<TaskModel> data) {
        this.data = data;
    }

    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ItemAdapter.ViewHolder holder, int position) {
        TaskModel item = data.get(position);
        holder.textView.setText(item.getItemName());
        holder.checkBox.setChecked(false);
        holder.item = item;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(ArrayList<TaskModel> data) {
        this.data = data;
    }
}
