package com.adida.aka.testcsv;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private static final String EXTERNAL_PATH = Environment.getExternalStorageDirectory().getPath() + "/";
    private static final String  FILE_NAME = "test.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void uploadFile(View view) {

        FTPClient ftpClient = new FTPClient();
        String path = EXTERNAL_PATH + FILE_NAME ;

        try {

            ftpClient.connect("ftp.akashjpro.esy.es", 21);
            ftpClient.login("u413200587", "AKSpro2020");
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.changeWorkingDirectory("/upload/");
            ftpClient.enterLocalPassiveMode(); // important!

            FileInputStream in = new FileInputStream(new File(path));
            boolean result = ftpClient.storeFile("/"+FILE_NAME, in);
            in.close();
            if (result) Log.d("upload result", "succeeded");
            ftpClient.logout();
            ftpClient.disconnect();
            Toast.makeText(this, "upload success", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createFile(View view) {
        try {
            String path = EXTERNAL_PATH + FILE_NAME ;
            File file = new File(path);
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(path, true);
            String dataCSV = "11,22,33,44,55,66,77";
            byte[] buff = dataCSV.getBytes();
            outputStream.write(buff, 0, buff.length);
            outputStream.close();
            Toast.makeText(this, "Save file CSV success", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Save error", Toast.LENGTH_SHORT).show();
        }

    }
}
