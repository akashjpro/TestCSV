package com.adida.aka.testcsv;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by tmha on 7/14/2017.
 */

public class DownloadFile extends AsyncTask<String, Integer, Boolean> {

    String pathLocal = Environment.getExternalStorageDirectory().getAbsolutePath() +
            "/" + Environment.DIRECTORY_DOWNLOADS + "/";

    private final String TAG = this.getClass().getName();

    public ProgressDialog mDialog;

    private String mHostName, mUserName, mPassWord;
    private int mPort;
    private List<String> mListPath;

    private Context mContext;
    private static final String HOST_NAME = "192.168.8.54";
    private static final String USER_NAME = "ISB-VIETNAM\\tmha";
    private static final String PASS_WORD = "AKSpro2020";

    private static final int PORT = 21;

    private FTPClient mFTPClient = new FTPClient();
    private FTPSClient mFtpsClient;
    private List<String> mLocalFile;
    private List<String> mListRemoteFile;


    public DownloadFile(Context mContext, List<String> mListRemoteFile) {
        this.mContext = mContext;
        this.mListRemoteFile = mListRemoteFile;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage("Downloading...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setMax(mListRemoteFile.size());
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            mHostName = params[0]; // set hostname
            mUserName = params[1]; // set username
            mPassWord = params[2]; // set pass
            mPort     = Integer.parseInt(params[3]); //set Port
            boolean resultConnect = ftpConnect(mHostName,
                    mUserName,
                    mPassWord,
                    mPort);
            if (resultConnect){
                for (int i = 0 ; i< mListRemoteFile.size(); i++){
                    String path =  pathLocal + getFileName(mListRemoteFile.get(i));
                    boolean isDownload = downloadFile(path, mListRemoteFile.get(i));
                    publishProgress(i + 1);
                }
                return true;

            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
            mDialog.dismiss();
            return false;
        }
    }

    private boolean downloadFile(String mLocalFile, String mRemoteFile) {
        boolean result = false;
        try {
            mFTPClient.enterLocalPassiveMode(); // important!
            File file = new File(mLocalFile);
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
            mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
            InputStream inputStream = mFTPClient.retrieveFileStream(mRemoteFile);

            byte[] bytesArray = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                outputStream.write(bytesArray, 0, bytesRead);
            }

            boolean success = mFTPClient.completePendingCommand();
            outputStream.close();
            inputStream.close();
            return success;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mDialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        mDialog.dismiss();
        if(aBoolean){
            Toast.makeText(mContext, "Download success", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(mContext, "Download fail", Toast.LENGTH_SHORT).show();
        }
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
