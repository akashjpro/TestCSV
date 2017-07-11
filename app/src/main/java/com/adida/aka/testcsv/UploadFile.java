package com.adida.aka.testcsv;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tmha on 7/6/2017.
 */

public class UploadFile extends AsyncTask<String, Integer, String> implements AsyncTaskExecInterface{

    private final String TAG = this.getClass().getName();

    public  ProgressDialog mDialog;

    private String mHostName, mUserName, mPassWord;
    private int mPort;
    private List<String> mListPath;

    private Context mContext;
    private ConnectServerFTPService mConnectServerFTPService;
    private ServiceConnection mServiceConnection;
    private boolean isConnected;

    private static final String HOST_NAME = "1922.168.0.102";
    private static final String USER_NAME = "Aka";
    //    private static final String USER_NAME = "ISB-VIETNAM\\tmha";
    private static final String PASS_WORD = "0906304280";

    private static final int PORT = 21;

    private FTPClient mFTPClient = new FTPClient();
    private List<String> mListFileFail = new ArrayList<>();

    public UploadFile(Context mContext, List<String> mListPath) {
        this.mContext = mContext;
        this.mListPath = mListPath;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        connectService();
        if (!isConnected){
            Toast.makeText(mContext, "No connet to service", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean resultConnect = mConnectServerFTPService.ftpConnect(
                                    HOST_NAME,
                                    USER_NAME,
                                    PASS_WORD,
                                    PORT
        );

        if (!resultConnect){
            Toast.makeText(mContext, "No connect to server, please check hostname, ...", Toast.LENGTH_SHORT).show();
            return;
        }

        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage("Uploading...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setMax(mListPath.size());
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
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
                    String fileName = getFileName(pathFile) + i;
                    boolean resultFile = ftpUpload(pathFile, fileName);
                    if (!resultFile){
                        mListFileFail.add(fileName);
                    }else {
                        publishProgress(i + 1);
                    }
                }

                int totalFileSuccess = mListPath.size() - mListFileFail.size();
                int totalFileFail    = mListFileFail.size();
                if(totalFileFail > 0){
                    result = totalFileSuccess + "file succes"
                            + ", " + totalFileFail + "file fail";
                }else {
                    result = "Upload success";
                }
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
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mDialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mDialog.dismiss();
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();

    }


    private void connectService(){
        Intent intent = new Intent(mContext, ConnectServerFTPService.class);
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ConnectServerFTPService.MyBinder myBinder = (ConnectServerFTPService.MyBinder) service;
                mConnectServerFTPService = myBinder.getService();
                isConnected = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isConnected = false;
                mConnectServerFTPService = null;
            }
        };
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
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

    public boolean ftpUpload(String pathFile, String fileName){
        /*
         * Set File Transfer Mode
         * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
         */
        boolean result = false;
        try {
            mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
            mFTPClient.enterLocalPassiveMode(); // important!

            FileInputStream in = new FileInputStream(new File(pathFile));
            result = mFTPClient.storeFile("/"+ fileName, in);
            in.close();
            return result;
//            mFTPClient.logout();
//            mFTPClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Error: could upload file " + fileName);
            return result;
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


    @Override
    public <T> void execute(AsyncTask<T, ?, ?> task, T... args) {
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, args);
    }

    public void disconnectService(){
        mContext.unbindService(mServiceConnection);
    }


}
