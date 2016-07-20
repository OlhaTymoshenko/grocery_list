package com.example.android.grocerylist.ui.items;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.example.android.grocerylist.R;

/**
 * Created by lapa on 29.03.16.
 */
public class ItemDialogFragment extends DialogFragment {
    ItemDialogListener mCallback;

    public interface ItemDialogListener {
        void onOKButtonClick(String item);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try {
            mCallback = (ItemDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement ItemDialogListener");
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.fragment_dialog_for_main_activity, null))
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) getDialog().findViewById(R.id.edit_text);
                        String item = editText.getText().toString();
                        mCallback.onOKButtonClick(item);
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismissAllowingStateLoss();
                    }
                });
        return builder.create();
    }

}
