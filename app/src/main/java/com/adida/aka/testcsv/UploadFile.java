package com.adida.aka.testcsv;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by tmha on 7/6/2017.
 */

public class UploadFile extends AsyncTask<String, Void, String> {
    private static final String EXTERNAL_PATH = Environment.getExternalStorageDirectory().getPath() + "/";
    private static final String  FILE_NAME = "newfile.csv";
    private static final String TAG = "MainActivity";
    private static final String HOST_NAME = "ftp.akashjpro.esy.es";
    private static final String USER_NAME = "u413200587";
    private static final String PASS = "AKSpro2020";
    private static final int    PORT = 21;
    private  String mFileName = "";

    private Context mContext;

    private FTPClient mFTPClient = new FTPClient();

    public UploadFile(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(mContext, "Start connect...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "Connect fail!!!";
        mFileName = params[0];
        boolean kq = ftpConnect(HOST_NAME, USER_NAME, PASS, PORT);
        if (kq){
            result = "Connect success";
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
    }

    public boolean ftpConnect(String host, String username, String password,
                              int port) {
        try {
            String path = EXTERNAL_PATH + mFileName ;
            mFTPClient = new FTPClient();
            // connecting to the host
            mFTPClient.connect(host, port);

            // now check the reply code, if positive mean connection success
            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                // login using username & password
                boolean status = mFTPClient.login(username, password);

				/*
				 * Set File Transfer Mode
				 * To avoid corruption issue you must specified a correct
				 * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
				 */
                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
//                mFTPClient.changeWorkingDirectory("/upload/");
                mFTPClient.enterLocalPassiveMode(); // important!

                FileInputStream in = new FileInputStream(new File(path));
                boolean result = mFTPClient.storeFile("/"+mFileName, in);
                in.close();
                if (result) Log.d("upload result", "succeeded");
                mFTPClient.logout();
                mFTPClient.disconnect();
                return result;
            }
        } catch (Exception e) {
            Log.d(TAG, "Error: could not connect to host " + host);
        }

        return false;
    }


}
