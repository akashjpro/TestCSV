package com.adida.aka.testcsv;

import android.Manifest;
import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final
        String EXTERNAL_PATH
            = Environment.getExternalStorageDirectory()
                         .getPath() + "/";
//    private static final String FILE_NAME = "test1.csv";
    private static final String TAG = "MainActivity";
    private static final String HOST_NAME = "192.168.0.102";
    private static final String USER_NAME = "Aka";
//    private static final String USER_NAME = "ISB-VIETNAM\\tmha";
    private static final String PASS_WORD = "0906304280";

    private static final String PORT = "21";
    private final int REQUEST_CODE_CAPTURE = 112;


    private Button mBtnUpload;
    private UploadFile mUploadFile;
    private EditText mEdtFileName;
    private String mFileName;
    private String mPathFile;
    private List<String> mListPathFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnUpload = (Button) findViewById(R.id.btn_upload);

        checkAndRequestPermissions();
        if (isNetworkConnected()){
            Toast.makeText(this, "Connet", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Disconnect", Toast.LENGTH_SHORT).show();
        }
        mEdtFileName = (EditText) findViewById(R.id.edt_file_name);
        mListPathFile = new ArrayList<>();


        mBtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUploadFile = new UploadFile(MainActivity.this, mListPathFile);
//                mUploadFile.execute(HOST_NAME,
//                                    USER_NAME,
//                                    PASS_WORD,
//                                    PORT
//                );

                mUploadFile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, HOST_NAME,
                        USER_NAME,
                        PASS_WORD,
                        PORT );

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
            for (int i=0; i< 50; i++) {
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
                for (int j = 0; j< 100; j++){
                    dataCSV += "\n00,11,11,00,00,00,00,00,11,11,00,00,00,00,11,11,00,00,00,00,00,11,11,00,00,00,00";
                }
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


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE
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

    private File createImageFile() throws IOException {
        // Create an image file name
        Calendar calendar = Calendar.getInstance();
        String timeStamp
                = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(calendar.getTime());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        String path = image.getAbsolutePath();
        for (int i =0 ; i< 100; i++){
            mListPathFile.add(path);
        }
        return image;
    }

    private void captureImage(){
        Intent cameraIntent
                = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i(TAG, "IOException");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent,
                        REQUEST_CODE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_CAPTURE
                && resultCode == RESULT_OK){
            Toast.makeText(this, "Create photo success", Toast.LENGTH_SHORT).show();
        }
    }

    public void createFilePhoto(View view) {
        captureImage();
    }

    public void initPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                //Permisson don't granted
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(MainActivity.this, "Permission isn't granted ", Toast.LENGTH_SHORT).show();
                }
                // Permisson don't granted and dont show dialog again.
                else {
                    Toast.makeText(MainActivity.this, "Permisson don't granted and dont show dialog again ", Toast.LENGTH_SHORT).show();
                }
                //Register permission
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            }
        }
    }

    public void openWifi(View view) {
//        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
//        showDialog();
        Dialog dialog = new Dialog(this);
        dialog.show();
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Thong bao");
        builder.setMessage("Do you upload again!!!");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Ok", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "No", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
