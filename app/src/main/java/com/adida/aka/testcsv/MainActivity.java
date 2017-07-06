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
    private static final String FILE_NAME = "file_new.csv";
    private static final String TAG = "MainActivity";
    private static final String HOST_NAME = "ftp.akashjpro.esy.es";
    private static final String USER_NAME = "u413200587";
    private static final String PASS = "AKSpro2020";
    private static final int    PORT = 21;

    private Button mBtnUpload;

    private MyFTPClientFunctions ftpclient = null;
    private UploadFile mUploadFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ftpclient = new MyFTPClientFunctions(this);
        mBtnUpload = (Button) findViewById(R.id.btn_upload);
        mUploadFile = new UploadFile(this);

        checkAndRequestPermissions();

        mBtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mUploadFile.execute(FILE_NAME);
                    }
                });


//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        boolean kq = ftpclient.ftpConnect(HOST_NAME, USER_NAME, PASS, PORT);
//                        if (kq){
//                        }
//                    }
//                }).start();

            }
        });
    }



    public void uploadFile(View view) {



//        FTP ftpClient = new FTPClient();
//        String path = EXTERNAL_PATH + FILE_NAME ;

//        try {
//
//            ftpClient.connect("ftp.akashjpro.esy.es", 21);
//            ftpClient.login("u413200587", "AKSpro2020");
//            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
//            ftpClient.changeWorkingDirectory("/upload/");
//            ftpClient.enterLocalPassiveMode(); // important!
//
//            FileInputStream in = new FileInputStream(new File(path));
//            boolean result = ftpClient.storeFile("/"+FILE_NAME, in);
//            in.close();
//            if (result) Log.d("upload result", "succeeded");
//            ftpClient.logout();
//            ftpClient.disconnect();
//            Toast.makeText(this, "upload success", Toast.LENGTH_SHORT).show();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    boolean kq = ftpclient.ftpConnect(HOST_NAME, USER_NAME, PASS, PORT);
                    if (kq){
                        Toast.makeText(MainActivity.this, "Connect success.", Toast.LENGTH_SHORT).show();
                    }
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

//    public boolean ftpConnect(String host, String username, String password,
//                              int port) {
//        FTPClient mFTPClient = new FTPClient();
//        try {
//            mFTPClient = new FTPClient();
//            // connecting to the host
//            mFTPClient.connect(host, port);
//
//            // now check the reply code, if positive mean connection success
//            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
//                // login using username & password
//                boolean status = mFTPClient.login(username, password);
//
//				/*
//				 * Set File Transfer Mode
//				 *
//				 * To avoid corruption issue you must specified a correct
//				 * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
//				 * EBCDIC_FILE_TYPE .etc. Here, I use BINARY_FILE_TYPE for
//				 * transferring text, image, and compressed files.
//				 */
//                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
//                mFTPClient.enterLocalPassiveMode();
//                return status;
//            }
//        } catch (Exception e) {
//            Log.d(TAG, "Error: could not connect to host " + host);
//        }
//
//        return false;
//    }

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
