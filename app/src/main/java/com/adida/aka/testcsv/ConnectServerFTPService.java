package com.adida.aka.testcsv;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import static android.content.ContentValues.TAG;

/**
 * Created by Aka on 7/12/2017.
 */

public class ConnectServerFTPService extends Service{
    FTPClient mFTPClient = new FTPClient();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public boolean ftpConnect(String host,
                              String username,
                              String password,
                              int port) {
        boolean result = false;
        try {
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

    public class  MyBinder extends Binder{
        public ConnectServerFTPService getService(){
            return ConnectServerFTPService.this;
        }
    }
}
