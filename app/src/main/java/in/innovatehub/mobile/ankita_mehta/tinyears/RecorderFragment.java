package in.innovatehub.mobile.ankita_mehta.tinyears;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
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

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.File;
import java.io.IOException;


public class RecorderFragment extends Fragment implements DownloadResultReceiver.Receiver{
    private static final String LOG_TAG = "DownloadService";

    private static String mFileName = "music.mp3";
    private static String mFilePath = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/TinyEars/"));
    private static String mFullPath = mFilePath + "/" + mFileName;
    String link = "http://192.168.50.0:9000/divide_form";



    private DownloadResultReceiver mReceiver;

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    private ImageButton mRecordImageButton = null;
    private ImageButton mPlayImageButton = null;

    boolean mStartRecording = true;
    boolean mStartPlaying = true;

    private Button mShowStatsButton = null;
    private TextView mStopCountTimer = null;

    private RelativeLayout mLayout;
    private LineChart mChart;

    Thread thread = null;

    CountDownTimer t = new CountDownTimer( Long.MAX_VALUE , 1000) {
        Integer cnt = -1;
        @Override
        public void onTick(long millisUntilFinished) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(LOG_TAG, "Inside CountDownTimer onTick");
                            cnt++;
                            long millis = cnt;
                            int seconds = (int) (millis / 60);
                            int minutes = seconds / 60;
                            seconds = seconds % 60;
                            mStopCountTimer.setText(String.format("%d:%02d:%02d", minutes, seconds, millis));
                        }
                    });
                }
            }).start();
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
                    //setText("Stop recording");
                } else {
                    t.cancel();
                    t.onFinish();
                    mRecordImageButton.setImageResource(R.drawable.micicon);
                    mPlayImageButton.setEnabled(true);
                    mShowStatsButton.setEnabled(true);
                    mShowStatsButton.setVisibility(View.VISIBLE);
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

        mLayout = (RelativeLayout) view.findViewById(R.id.live_chart);
        realLiveChart(view);

        return view;
    }

    private Handler handler = new Handler();
    final Runnable updater = new Runnable() {
        public void run() {
            handler.postDelayed(this, 1);
            if (mRecorder != null) {
                int maxAmplitude = mRecorder.getMaxAmplitude();
                if (maxAmplitude != 0) {
                    // visualizerView.addAmplitude(maxAmplitude);
                }
            } else {

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
        mRecorder.setOutputFile(mFilePath + "/" + mFileName);
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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRecordImageButton.setImageResource(R.drawable.micicon);
                        }
                    });
                }
            }).start();

            // mStartRecording = true;
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRecordImageButton.setImageResource(R.drawable.stopicon);
                }
            });
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

    protected void realLiveChart(View view){
        mLayout = (RelativeLayout) view.findViewById(R.id.live_chart);
        mChart = new LineChart(view.getContext());
        mLayout.addView(mChart);

        mChart.setDescription("");
        mChart.setNoDataTextDescription("No Data for this moment");

        mChart.setHighlightEnabled(true);
        mChart.setTouchEnabled(true);

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        mChart.setDrawGridBackground(false);

        mChart.setPinchZoom(true);

        //mChart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);
        //  Legend l = mChart.getLegend();
        //  l.setForm(Legend.LegendForm.LINE);
        //  l.setTextColor(Color.WHITE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntry();
                        }
                    });
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    private void addEntry() {
        LineData data = mChart.getData();

        if (data != null) {
            LineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            data.addXValue("");
            data.addEntry(new Entry((float)(Math.random()*120), set.getEntryCount()), 0);

            mChart.notifyDataSetChanged();
            mChart.setVisibleXRange(50);

            mChart.moveViewToX(data.getXValCount() - 11);
        }
    }
    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "spl db");
        // set.setDrawCubic(true);
        // set.setCubicIntensity(0.2f);
        set.setLineWidth(2f);
        set.setCircleSize(1f);
        return set;
    }

    void callServiceIntent(View view){
        /* Starting Download Service */
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        Intent intent = new Intent(getActivity(), UploadIntentService.class);
        Log.d(LOG_TAG,"Inside service setOnClickListener");
        /* Send optional extras to Download IntentService */
        intent.putExtra("url", link);
        intent.putExtra("filepath", mFullPath);
        intent.putExtra("directory",mFilePath);
        intent.putExtra("receiver", mReceiver);
        intent.putExtra("requestId", 101);

        getActivity().startService(intent);
        Log.d(LOG_TAG,"Inside service startService");
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case UploadIntentService.STATUS_RUNNING:
                break;
            case UploadIntentService.STATUS_FINISHED:
                /* Hide progress & extract result from bundle */
                String[] results = resultData.getStringArray("result");
                Log.d(LOG_TAG,"ABle to get res"+results[0]);
                /* Update ListView with result */
                Log.d(LOG_TAG,"ABle to get ar");

                break;
            case UploadIntentService.STATUS_ERROR:
                /* Handle the error */
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Log.d(LOG_TAG,error);
                Toast.makeText(getActivity().getApplicationContext(), error, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
