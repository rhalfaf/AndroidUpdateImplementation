package com.sailor.shopper;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

public class CheckUpdate {

    private static final String URL = "http://192.168.0.234:8080/update?version=2&link=https://drive.google.com/u/0/uc?id=1wxE_JMfzR7LgeQc-gILjDHAPhd5Lyx3m&export=download";
    private Context mContext;
    private int actualVersion;

    public CheckUpdate(Context context) {
        this.mContext = context;
        this.actualVersion = getActualAppVersion();
        sendRequest();
    }

    private int getActualAppVersion() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return packageInfo.versionCode;
    }

    private void sendRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Uri downloadURI = Uri.parse(jsonObject.getString("link"));
                        int lastVersion = jsonObject.getInt("version");
                        if ( lastVersion > actualVersion){
                            downloadNewVersion(downloadURI);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {

                });
        requestQueue.add(request);
    }

    private void downloadNewVersion(Uri downloadURI){
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(downloadURI);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        request.setTitle("My new version download");
        request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, "app-debug.apk");
        long id = downloadManager.enqueue(request);
        installNewVersion(id, downloadManager);
    }

    private void installNewVersion(long id, DownloadManager downloadManager){
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.setDataAndType(downloadManager.getUriForDownloadedFile(id), "application/vnd.android.package-archive");
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(installIntent);

            }
        };
    }

}
