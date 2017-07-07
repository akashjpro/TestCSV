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
import java.util.List;

/**
 * Created by tmha on 7/6/2017.
 */

public class UploadFile extends AsyncTask<String, Void, String>{

    private static final String TAG = "MainActivity";
    public  ProgressDialog mDialog;

    private String mHostName, mUserName, mPassWord;
    private int mPort;
    private List<String> mListPath;


    private Context mContext;

    private FTPClient mFTPClient = new FTPClient();

    public UploadFile(Context mContext, List<String> mListPath) {
        this.mContext = mContext;
        this.mListPath = mListPath;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage("Uploading...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String result = "";
            mHostName = params[0]; // set hostname
            mUserName = params[1]; // set username
            mPassWord = params[2]; // set pass
            mPort     = Integer.parseInt(params[3]); //set Port
            boolean resultConnect = ftpConnect(mHostName,
                                mUserName,
                                mPassWord,
                                mPort);
            if (resultConnect){
                for (int i=0; i< mListPath.size(); i++) {
                    String pathFile = mListPath.get(i);
                    String fileName = getFileName(pathFile);
                    ftpUpload(pathFile, fileName);
                }
                result = "Upload success";
            }else {
                result = "Connect to server fail, " +
                        "please check host name or username and password again!!!";
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            String error ="Failure : " + e.getLocalizedMessage();
            Log.d(TAG, error);
            mDialog.dismiss();
            return "upload fail!!!";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mDialog.dismiss();
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();

    }

    public boolean ftpConnect(String host,
                              String username,
                              String password,
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

    public void ftpUpload(String pathFile, String fileName){
        /*
         * Set File Transfer Mode
         * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
         */
        try {
            mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
            mFTPClient.enterLocalPassiveMode(); // important!

            FileInputStream in = new FileInputStream(new File(pathFile));
            mFTPClient.storeFile("/"+ fileName, in);
            in.close();
//            mFTPClient.logout();
//            mFTPClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Error: could upload file " + fileName);
        }
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


}
