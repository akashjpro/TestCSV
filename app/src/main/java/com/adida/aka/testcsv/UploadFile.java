package com.adida.aka.testcsv;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by tmha on 7/6/2017.
 */

public class UploadFile extends AsyncTask<String, Void, String> {

    private static final int    PORT = 21;
    private static final String TAG = "MainActivity";

    private String mHostName;
    private String mUserName;
    private String mPassWord;
    private String mFileName;
    private String mPathFile;

    private ProgressDialog mDialog;


    private Context mContext;

    private FTPClient mFTPClient = new FTPClient();

    public UploadFile(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage("Uploading...");
        mDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            mHostName = params[0];
            mUserName = params[1];
            mPassWord = params[2];
            mFileName = params[3];
            mPathFile = params[4] + mFileName;
            boolean resultConnect = ftpConnect(mHostName, mUserName, mPassWord, PORT);
            if (resultConnect){
               boolean resultUpload = ftpUpload(mPathFile, mFileName);
                if (resultUpload){
                    return "Upload success";
                }
            }
            return "Upload fail!!!";
        }catch (Exception e){
            e.printStackTrace();
            String error ="Failure : " + e.getLocalizedMessage();
            Log.d(TAG, error);
            return error;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mDialog.dismiss();
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
    }

    public boolean ftpConnect(String host, String username, String password,
                              int port) {
        boolean result = false;
        try {
            mFTPClient = new FTPClient();
            // connecting to the host
            mFTPClient.connect(host, port);

            // now check the reply code, if positive mean connection success
            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                // login using username & password
              result = mFTPClient.login(username, password);
            }
        } catch (Exception e) {
            Log.d(TAG, "Error: could not connect to host " + host);
        }

        return result;
    }

    public boolean ftpUpload(String pathFile, String fileName){
        boolean result = false;
        /*
         * Set File Transfer Mode
         * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
         */
        try {
            mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
            mFTPClient.enterLocalPassiveMode(); // important!

            FileInputStream in = new FileInputStream(new File(pathFile));
            result = mFTPClient.storeFile("/"+ fileName, in);
            in.close();
//            mFTPClient.logout();
//            mFTPClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Error: could upload file " + fileName);
        }

        return result;
    }


}
