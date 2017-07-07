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
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final
        String EXTERNAL_PATH
            = Environment.getExternalStorageDirectory()
                         .getPath() + "/";
//    private static final String FILE_NAME = "test1.csv";
    private static final String TAG = "MainActivity";
    private static final String HOST_NAME = "192.168.8.54";
    private static final String USER_NAME = "ISB-VIETNAM\\tmha";
    private static final String PASS_WORD = "AKSpro2020";

    private static final String PORT = "21";


    private Button mBtnUpload;
    private UploadFile mUploadFile;
    private EditText mEdtFileName;
    private String mFileName;
    private String mPathFile;
    private ArrayList<String> mListPathFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnUpload = (Button) findViewById(R.id.btn_upload);

        checkAndRequestPermissions();
        mEdtFileName = (EditText) findViewById(R.id.edt_file_name);
        mListPathFile = new ArrayList<>();


        mBtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUploadFile = new UploadFile(MainActivity.this, mListPathFile);
                mUploadFile.execute(HOST_NAME,
                                    USER_NAME,
                                    PASS_WORD,
                                    PORT
                );
            }
        });
    }

    /**
     * get name from file path
     * @param pathFile
     * @return
     */
    private String getFileName(String pathFile){
        try {
            int index = pathFile.lastIndexOf("/");
            String result = pathFile.substring(index + 1) ;
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null ;
    }




    public void createFile(View view) {
        try {
            String name = mEdtFileName.getText()
                                      .toString().trim();
            for (int i=0; i< 10; i++) {
                String fileName = name + i + ".csv";
                String path = EXTERNAL_PATH + fileName;
                mListPathFile.add(path);
                File file = new File(path);
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream outputStream
                        = new FileOutputStream(path, true);
                String dataCSV = "00,11,11,00,00,00,00,00,11,11,00,00,00,00,11,11,00,00,00,00,00,11,11,00,00,00,00";
                byte[] buff = dataCSV.getBytes();
                outputStream.write(buff, 0, buff.length);
                outputStream.close();
            }
            Toast.makeText(this, "Save file CSV success",
                    Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();

            Toast.makeText(this, "Save error: "+
                    e.toString(), Toast.LENGTH_SHORT).show();
        }

    }


    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]), 1);
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
