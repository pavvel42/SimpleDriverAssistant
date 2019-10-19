package com.example.simpledriverassistant;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatDialogFragment;

public class InfoDialog extends AppCompatDialogFragment {

    private String info = null;
    private Intent intent = null;

    public InfoDialog(String info, Intent intent) {
        this.info = info;
        this.intent = intent;
    }

    public InfoDialog(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public Intent getIntent() {
        return intent;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.information))
                .setMessage(getInfo())
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (getIntent() != null) {
                            startActivity(getIntent());
                        }
                    }
                })
                .setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }
}
