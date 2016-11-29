package in.innovatehub.mobile.ankita_mehta.tinyears;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;

import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.regex.Pattern;

import static in.innovatehub.mobile.ankita_mehta.tinyears.DashboardFragment.roomSelected;


public class RecorderFragment extends Fragment implements DownloadResultReceiver.Receiver{
    private static final String LOG_TAG = "DownloadService";

    private static String mFileName = "music.mp3";
    private static String mFilePath = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/TinyEars/"));
    private static String mFullPath = mFilePath + "/" + mFileName;
    String LastLink = "http://192.168.50.0:5000/divide_form";

    private DownloadResultReceiver mReceiver;

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    private ImageButton mRecordImageButton = null;
    private ImageButton mPlayImageButton = null;

    boolean mStartRecording = true;
    boolean mStartPlaying = true;

    private Button mShowStatsButton = null;
    private TextView mStopCountTimer = null;
    private ProgressBar mProgressBar = null;

    private RelativeLayout mLayout;
    private LineChart mChart;

    int maxAmplitude = 0;

    Thread thread = null;

    CountDownTimer t = new CountDownTimer( Long.MAX_VALUE , 1000) {
        Integer cnt = -1;
        @Override
        public void onTick(long millisUntilFinished) {
            Log.d(LOG_TAG, "Inside CountDownTimer onTick");
            cnt++;
            long millis = cnt;
            int seconds = (int) (millis / 60);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            mStopCountTimer.setText(String.format("%d:%02d:%02d", minutes, seconds, millis));
        }

        @Override
        public void onFinish() {
            Log.d(LOG_TAG,"Inside CountDownTimer finish");
            cnt = 0;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG,"Inside onCreateView");
        //getActivity().requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recorder, container, false);

        mRecordImageButton = (ImageButton) view.findViewById(R.id.imageButton2);
        mPlayImageButton = (ImageButton) view.findViewById(R.id.imageButton3);
        mShowStatsButton = (Button) view.findViewById(R.id.showMeStats);
        mStopCountTimer = (TextView) view.findViewById(R.id.stopCountTimer);
        mProgressBar = (ProgressBar) view.findViewById(R.id.marker_progress);

        mRecordImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(LOG_TAG,"Inside setOnClickListener");
                // Perform action on click
                onRecord(mStartRecording);
                if (mStartRecording) {
                    t.start();
                    mRecordImageButton.setImageResource(R.drawable.stopicon);
                    mPlayImageButton.setEnabled(false);
                    mShowStatsButton.setEnabled(false);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mShowStatsButton.setVisibility(View.INVISIBLE);
                    //setText("Stop recording");
                } else {
                    t.cancel();
                    t.onFinish();
                    mRecordImageButton.setImageResource(R.drawable.micicon);
                    mPlayImageButton.setEnabled(true);
                    mShowStatsButton.setEnabled(true);
                    Log.d(LOG_TAG,"calling service setOnClickListener");
                    callServiceIntent(v);
                    //setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        });
        mPlayImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    mPlayImageButton.setImageResource(R.drawable.pauseicon);
                    mRecordImageButton.setEnabled(false);
                    mShowStatsButton.setEnabled(false);
                    //setText("Stop playing");
                } else {
                    mPlayImageButton.setImageResource(R.drawable.playicon);
                    mRecordImageButton.setEnabled(true);
                    mShowStatsButton.setEnabled(false);
                    //setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        });

        return view;
    }

    private Handler handler = new Handler();
    final Runnable updater = new Runnable() {
        public void run() {
            handler.postDelayed(this, 1);
            if (mRecorder != null) {
               maxAmplitude = mRecorder.getMaxAmplitude();
            }
        }
    };

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFilePath + "/" + mFileName);
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    Log.i("Completion Listener", "Song Complete");
                    stopPlaying();
                    mRecordImageButton.setEnabled(true);
                }
            });

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        if (mPlayer != null) {
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
            mPlayImageButton.setImageResource(R.drawable.playicon);
            //  mStartPlaying = true;
        } else {
            mPlayImageButton.setImageResource(R.drawable.pauseicon);
            //   mStartPlaying = false;
        }
    }

    private void startRecording() {
        AudioRecordTest(String.valueOf(System.currentTimeMillis()));
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFullPath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        try {
            mRecorder.start();
        } catch (Exception e) {
            Log.e(LOG_TAG, "start() failed");
        }
    }

    private void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            mRecordImageButton.setImageResource(R.drawable.micicon);

            // mStartRecording = true;
        } else {
            mRecordImageButton.setImageResource(R.drawable.stopicon);

            // mStartRecording = false;
        }
    }

    public void AudioRecordTest(String text) {
        boolean exists = (new File(mFullPath)).exists();
        if (!exists) {
            new File(mFileName).mkdirs();
        }
    }

    int getMediaDuration(){
        MediaPlayer mp = MediaPlayer.create(getActivity().getApplicationContext(), Uri.parse((String) mFullPath));
        int duration = mp.getDuration();
        return duration;
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if(!checkInternet()){
            Intent newIntent = new Intent(getActivity(),ErrorActivity.class);
            startActivity(newIntent);
        }
    }

    boolean checkInternet(){
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void sendResults(String res) {
        Log.d(LOG_TAG, "Inside on create, Navigating to Result Screen Activity!");
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mRecorder!=null) {
            mRecorder.stop();     // stop recording
            mRecorder.reset();    // set state to idle
            mRecorder.release();  // release resources back to the system
            mRecorder = null;
        }
        if (mPlayer != null) {
            mPlayer.stop();     // stop playing
            mPlayer.release();
            mPlayer = null;
        }
        if(thread!=null)
            thread.stop();
        //   getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updater);
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public String getMacId(){
        WifiManager manager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();
        return address;
    }

    void callServiceIntent(View view){
        if(!checkInternet()){
            Intent newIntent = new Intent(getActivity(),ErrorActivity.class);
            startActivity(newIntent);
        }
        /* Starting Download Service */
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        Intent intent = new Intent(getActivity(), UploadIntentService.class);
        Log.d(LOG_TAG,"Inside service setOnClickListener");
        /* Send optional extras to Download IntentService */
        intent.putExtra("macID",getMacId());
        intent.putExtra("url", LastLink);
        intent.putExtra("filepath", mFullPath);
        intent.putExtra("directory",mFilePath);
        intent.putExtra("roomid", roomSelected);
        intent.putExtra("receiver", mReceiver);

        getActivity().startService(intent);
        Log.d(LOG_TAG,"Inside service startService");
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case UploadIntentService.STATUS_RUNNING:
                mProgressBar.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity().getApplicationContext(), "Please stay tuned while we process.", Toast.LENGTH_LONG).show();
                break;
            case UploadIntentService.STATUS_FINISHED:
                /* Hide progress & extract result from bundle */
                String[] results = resultData.getStringArray("result");
                if (results == null) {
                    Toast.makeText(getActivity().getApplicationContext(), "Server not responding.", Toast.LENGTH_LONG).show();
                }else {
                    Log.d(LOG_TAG, "ABle to get res" + results[0]);
                /* Update ListView with result */
                    Log.d(LOG_TAG, "ABle to get ar");
                    mProgressBar.setVisibility(View.INVISIBLE);
                    try {
                        sample(results[0]);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getActivity().getApplicationContext(), "Successfully downloaded.", Toast.LENGTH_LONG).show();
                }
                break;
            case UploadIntentService.STATUS_ERROR:
                /* Handle the error */
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Log.d(LOG_TAG,error);
                Toast.makeText(getActivity().getApplicationContext(), error, Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void sample(String json) throws UnsupportedEncodingException, JSONException {
        TextView t1, t2,t3,t4,t5,t6,t7,t8;
        t1 = (TextView) getActivity().findViewById(R.id.id1);
        t2 = (TextView) getActivity().findViewById(R.id.id2);
        t3 = (TextView) getActivity().findViewById(R.id.id3);
        t4 = (TextView) getActivity().findViewById(R.id.id4);
        t5 = (TextView) getActivity().findViewById(R.id.id5);
        t6 = (TextView) getActivity().findViewById(R.id.id6);
        t7 = (TextView) getActivity().findViewById(R.id.id7);

       String msg = json.toString();
        msg = msg.replace("u","");
        String s1 = Normalizer.normalize(json, Normalizer.Form.NFKD);
        String regex = Pattern.quote("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

        String s2 = new String(s1.replaceAll(regex, "").getBytes("ascii"), "ascii");
        JSONObject msg2 = new JSONObject(msg);
        JSONObject jsonObj = new JSONObject(msg2.toString());
        Log.d(" JSON Parsed Data", jsonObj.toString());
        int count = jsonObj.length();
        JSONObject rowObj = jsonObj.getJSONObject(String.valueOf(0));

        Log.d("ssss", rowObj.getString("slot"));
        Log.d("ssss", rowObj.getString("fan_time"));
        Log.d("ssss", rowObj.getString("energy_consmed"));
        Log.d("ssss", rowObj.getString("label"));
        Log.d("ssss", rowObj.getString("money_wasted"));
        Log.d("ssss", rowObj.getString("hman_time"));
        Log.d("ssss", rowObj.getString("speed"));

        t1.setText("slot:"+rowObj.getString("slot"));
        t2.setText("fan_time:"+rowObj.getString("fan_time"));
        t3.setText("energy_consmed"+rowObj.getString("energy_consmed"));
        t4.setText("label:"+rowObj.getString("label"));
        t5.setText("money_wasted:"+rowObj.getString("money_wasted"));
        t6.setText("human_time:"+rowObj.getString("hman_time"));
        t7.setText("speed:"+rowObj.getString("speed"));
    }
}
