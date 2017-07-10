package com.adida.aka.testcsv;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Aka on 7/10/2017.
 */

public class Dialog extends android.app.Dialog {
    Context context;
    public Dialog(@NonNull Context context) {
        super(context);
        this.context = context;
        initView();
    }

    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog);
        Button btnNo = (Button) findViewById(R.id.btn_no);
        Button btnOk = (Button) findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Ok", Toast.LENGTH_SHORT).show();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "No", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
