package in.innovatehub.mobile.ankita_mehta.tinyears;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class UploadIntentService extends IntentService {
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    public static String URL = "";

    private static final String TAG = "UploadIntentService";

    public UploadIntentService() {
        super("UploadIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d(TAG, "Service Started!");
            final ResultReceiver receiver = intent.getParcelableExtra("receiver");
            String url = intent.getStringExtra("url");
            String path = intent.getStringExtra("filepath");
            String mPath = intent.getStringExtra("directory");
            String mMac = intent.getStringExtra("macid");
            String mRoomId = intent.getStringExtra("roomid");

            Bundle bundle = new Bundle();
            if (!TextUtils.isEmpty(url)) {
            /* Update UI: Download Service is Running */
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
                try {
                    URL = url;
                    String[] results = downloadData(url,path,mPath,mMac,mRoomId);
                /* Sending result back to activity */
                    if (null != results && results.length > 0) {
                        bundle.putStringArray("result", results);

                        // here you are sending progress into ResultReceiver located in your Activity
                        receiver.send(STATUS_FINISHED, bundle);
                    }
                } catch (Exception e) {
                /* Sending error message back to activity */
                    bundle.putString(Intent.EXTRA_TEXT, e.toString());
                    receiver.send(STATUS_ERROR, bundle);
                }
            }
            Log.d(TAG, "Service Stopping!");
            this.stopSelf();
        }
    }
    public void writeToFile(String data,String mPath) {
        // Get the directory for the user's public pictures directory.
        final File path = new File(mPath);
        // Make sure the path directory exists.
        if (!path.exists()) {
            // Make it, if it doesn't exit
            path.mkdirs();
        }
        final File file = new File(path, "encoding.txt");
        // Save your stream, don't forget to flush() it before closing it.
        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);
            myOutWriter.close();
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append((line + "\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private String[] downloadData(String requestUrl,String path, String dir, String mMac, String mRoomId) throws IOException {
        String[] results = new String[1];
        String encoded = "";
        //THIS IS FILE ENCODING CODE
        try {
            File file = new File(path);
            byte[] bytes = FileUtils.readFileToByteArray(file);
            encoded = Base64.encodeToString(bytes, 0);
            Log.d("~~~~~~~~ Encoded: ", encoded);
            writeToFile(encoded, dir);
        }catch(Exception e){
            Log.d(TAG,"Error"+e.getMessage());
        }
        //THIS IS URL CONN CODE
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(requestUrl);
        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("macid", mMac));
            nameValuePairs.add(new BasicNameValuePair("mp3", encoded));
            nameValuePairs.add(new BasicNameValuePair("roomid", mRoomId));
            nameValuePairs.add(new BasicNameValuePair("whichrec", "overall"));
            nameValuePairs.add(new BasicNameValuePair("category", "daily"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String sb = convertStreamToString(response.getEntity().getContent());
                Log.d(TAG, "MESSAGE NOW" + sb);
                results[0]=sb;
                Log.d(TAG,"service results"+results[0]);
            }else{
                //handle code 500
                String sb = "Unable to get your results. Try again!";
                results[0] = sb;
                Log.d(TAG, "MESSAGE NOW" + sb);
            }
        } catch (ClientProtocolException e) {
            Log.d(TAG, e.getMessage());
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.d(TAG, e.getMessage());
        }
        return results;
    }
}
