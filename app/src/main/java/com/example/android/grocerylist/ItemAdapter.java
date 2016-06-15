package com.example.android.grocerylist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lapa on 11.04.16.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private final List<TaskModel> data = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final CheckBox checkBox;
        final TextView textView;
        TaskModel taskModel;

        public ViewHolder(View view) {
            super(view);
            checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        MainActivity.DeleteItemsTask itemsTask = new MainActivity.DeleteItemsTask
                                (buttonView.getContext().getApplicationContext());
                        itemsTask.execute(taskModel);
                    }
                }
            });
            textView = (TextView) view.findViewById(R.id.list_item_text_view);
        }
    }

    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemAdapter.ViewHolder holder, int position) {
        TaskModel model = data.get(position);
        holder.textView.setText(model.getItemName());
        holder.checkBox.setChecked(false);
        holder.taskModel = model;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(ArrayList<TaskModel> data) {
        this.data.clear();
        this.data.addAll(data);
    }
}
