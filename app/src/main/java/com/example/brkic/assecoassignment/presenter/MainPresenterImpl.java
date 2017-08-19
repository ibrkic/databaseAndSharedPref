package com.example.brkic.assecoassignment.presenter;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.brkic.assecoassignment.db.DBHelper;
import com.example.brkic.assecoassignment.fragments.MainView;
import com.example.brkic.assecoassignment.models.WebPageModel;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by brka on 19.08.2017..
 */
public class MainPresenterImpl implements MainPresenter {

    public static final String TAG = MainPresenterImpl.class.getSimpleName();
    public static final String INVALID_URL = "invalid_url";
    public static final String ERROR_SAVING_TO_DB = "error_saving_to_db";
    public static final String ERROR_SAVING_TO_SP = "error_saving_to_sp";
    public static final String SAVED_TO_DB = "saved_to_db";
    public static final String SAVED_TO_SP = "saved_to_sp";
    private String webPageUrl;
    private MainView view;
    private SharedPreferences sp;
    private DBHelper db;

    public MainPresenterImpl(MainView view, SharedPreferences sp, DBHelper db) {
        this.view = view;
        this.sp = sp;
        this.db = db;
    }


    @Override
    public void bind() {

    }

    @Override
    public void unbind() {

    }

    @Override
    public void parseWebPage(String webPageUrl) {
        if (webPageUrl.startsWith("www.")) {        // Add protocol to avoid MalformedURLException
            webPageUrl = "http://" + webPageUrl;
        }
        if (sp.getString(webPageUrl, null) == null && !db.checkIfWebPageExists(webPageUrl)) {   //Check if web page url is already saved to sp or db
            this.webPageUrl = webPageUrl;
            new ParseWebPage().execute();
        } else if (sp.getString(webPageUrl, null) != null) {
            String hash = sp.getString(webPageUrl, null);
            view.showAlreadySavedMessage(SAVED_TO_SP, webPageUrl, hash.getBytes(StandardCharsets.ISO_8859_1));
        } else {
            String hash = db.getHashForUrl(webPageUrl);
            view.showAlreadySavedMessage(SAVED_TO_DB, webPageUrl, hash.getBytes(StandardCharsets.ISO_8859_1));
        }
    }

    private String getWebPageContent(String webPageUrl) {
        HttpURLConnection urlConnection;
        InputStream is = null;
        InputStreamReader isr = null;
        String content = null;
        try {
            URL url = new URL(webPageUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(urlConnection.getInputStream());
            isr = new InputStreamReader(is);

            int numCharsRead;
            char[] charArray = new char[1024];
            StringBuffer sb = new StringBuffer();
            while ((numCharsRead = isr.read(charArray)) > 0) {
                sb.append(charArray, 0, numCharsRead);
            }
            content = sb.toString();
        } catch (MalformedURLException e) {
            view.showErrorMessage(INVALID_URL);
        } catch (UnknownHostException e) {
            view.showErrorMessage(INVALID_URL);
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    Log.e(TAG, "Exception", e);
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (Exception e) {
                    Log.e(TAG, "Exception", e);
                }
            }
            return content;
        }
    }

    private byte[] generateHashForContent(String content) {
        byte[] hashData = null;
        try {
            MessageDigest mdSha1 = MessageDigest.getInstance("SHA-1");
            mdSha1.update(content.getBytes("ASCII"));
            hashData = mdSha1.digest();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "NoSuchAlgorithmException", e);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "UnsupportedEncodingException", e);
        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
        }
        return hashData;
    }

    private void parseAndSaveHashData(byte[] hashData) {
        if (hashData[0] % 2 == 0) {  //save to database
            if (db.addWebPage(new WebPageModel(webPageUrl, new String(hashData, StandardCharsets.ISO_8859_1)))) {
                view.showSuccessMessage(SAVED_TO_DB, webPageUrl, hashData);
            } else {
                view.showErrorMessage(ERROR_SAVING_TO_DB);
            }
        } else {                    //save to shared SharedPreferences
            if (saveToSharedPreferences(hashData)) {
                view.showSuccessMessage(SAVED_TO_SP, webPageUrl, hashData);
            } else {
                view.showErrorMessage(ERROR_SAVING_TO_SP);
            }
        }
    }

    private boolean saveToSharedPreferences(byte[] hashData) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(webPageUrl, webPageUrl);
        editor.putString("hash_" + webPageUrl, new String(hashData, StandardCharsets.ISO_8859_1));
        editor.commit();
        return true;
    }

    private class ParseWebPage extends AsyncTask<Void, Void, byte[]> {

        byte[] hashData;

        @Override
        protected byte[] doInBackground(Void... params) {
            String content = getWebPageContent(webPageUrl);
            if(content != null) {
                hashData = generateHashForContent(content);
            }
            return hashData;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            if (bytes != null) {
                parseAndSaveHashData(bytes);
            }
        }
    }
}
