package com.adida.aka.testcsv;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String EXTERNAL_PATH = Environment.getExternalStorageDirectory().getPath() + "/";
    private static final String FILE_NAME = "test2.csv";
    private static final String TAG = "MainActivity";
    private static final String HOST_NAME = "192.168.0.102";
    private static final String USER_NAME = "Aka";
    private static final String PASS_WORD = "0906304280";

    private Button mBtnUpload;
    private UploadFile mUploadFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnUpload = (Button) findViewById(R.id.btn_upload);

        checkAndRequestPermissions();

        mBtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUploadFile = new UploadFile(MainActivity.this);
                mUploadFile.execute(HOST_NAME,
                                    USER_NAME,
                                    PASS_WORD,
                                    FILE_NAME,
                                    EXTERNAL_PATH
                );

            }
        });
    }




    public void createFile(View view) {
        try {
            String path = EXTERNAL_PATH + FILE_NAME ;
            File file = new File(path);
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(path, true);
            String dataCSV = "00,11,11,00,00,00,00";
            byte[] buff = dataCSV.getBytes();
            outputStream.write(buff, 0, buff.length);
            outputStream.close();
            Toast.makeText(this, "Save file CSV success", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();

            Toast.makeText(this, "Save error: "+ e.toString(), Toast.LENGTH_SHORT).show();
        }

    }


    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
