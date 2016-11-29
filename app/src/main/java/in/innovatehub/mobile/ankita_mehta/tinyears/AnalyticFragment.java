package in.innovatehub.mobile.ankita_mehta.tinyears;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static in.innovatehub.mobile.ankita_mehta.tinyears.DashboardFragment.roomSelected;


public class AnalyticFragment extends Fragment implements DownloadResultReceiver.Receiver {

    private DownloadResultReceiver mReceiver;
    private static final String LOG_TAG = "DownloadService";
    String LastLink = "http://192.168.50.0:5000/divide_form";
    String WeekLink = "http://192.168.50.0:9001/divide_result";
    String MonthLink = "http://192.168.50.0:9002/divide_result";

    private static String mFilePath = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/TinyEars/"));

    private ProgressBar mProgressBar = null;
    LinearLayout mL1;

    private Button mWeekBtn;
    private Button mMonthBtn;


    private final static String MONTH = "month";
    private final static String DAY = "day";
    private final static String YEAR = "year";

    Thread thread;
    View view;

    private LineChart energyLineChart;
    private LineChart moneyLineChart;
    private PieData pData;
    private PieChart pie;

    private BarData mData;
    private BarChart mChart;

    FloatingActionButton mFab;
    String json_string;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_analytic,  container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.marker_progress_analytic);
        mProgressBar.setVisibility(View.INVISIBLE);

        mL1 = (LinearLayout) view.findViewById(R.id.analytics_Result_LinearLayout);

        mFab = (FloatingActionButton) view.findViewById(R.id.fab_analytics);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap;
                View v1 = mL1.getRootView();// get ur root view id
                Log.e(LOG_TAG,"v1"+v1);
                v1.setDrawingCacheEnabled(true);
                bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                Log.e(LOG_TAG,"bitmap"+bitmap);
                v1.setDrawingCacheEnabled(false);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                File f = new File(mFilePath
                        + File.separator + "Screenshot"+ SystemClock.currentThreadTimeMillis()+".jpg");
                try {
                    f.createNewFile();
                    Log.e(LOG_TAG,"file created"+bitmap);
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                    fo.close();
                }catch(Exception e){
                    Log.e(LOG_TAG,e.getMessage());
                }
            }
        });

        //drawPieChart();
        mWeekBtn = (Button) view.findViewById(R.id.week_btn);
        mMonthBtn = (Button) view.findViewById(R.id.month_btn);

        pie = (PieChart) view.findViewById(R.id.pie3);
        energyLineChart = (LineChart) view.findViewById(R.id.graph);
        moneyLineChart = (LineChart) view.findViewById(R.id.graph2);

        mMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int myColor = Color.parseColor("#ff4081");
                //    mMonthBtn.requestFocus();
                mMonthBtn.setBackgroundColor(Color.GRAY);

                mWeekBtn.clearFocus();
                mWeekBtn.setBackgroundColor(myColor);

                //callserver
                mProgressBar.setVisibility(View.VISIBLE);
                callServiceIntent(v,MonthLink);
            }
        });

        mWeekBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int myColor = Color.parseColor("#ff4081");

                mWeekBtn.setBackgroundColor(Color.GRAY);

                mMonthBtn.clearFocus();
                mMonthBtn.setBackgroundColor(myColor);

                //call server
                mProgressBar.setVisibility(View.VISIBLE);
                callServiceIntent(v,WeekLink);
                if(json_string==null)
                Log.d("json_string", "week empty string");
            }
        });

        return view;
    }

    public void drawCharts(String json_string) {
        drawEnergyLineChart(json_string);
        drawMoneyLineChart(json_string);
        drawPieChart(json_string);
        drawBarChart(json_string);
    }

    void drawMonthChart(){
        if (json_string != null) {
            drawEnergyLineChart(json_string);
            drawMoneyLineChart(json_string);
            drawPieChart(json_string);
            drawBarChart(json_string);
        } else {
            Log.d("json_string", "month empty string");
        }
    }

    void drawWeekChart(){
        if (json_string != null) {
            drawEnergyLineChart(json_string);
            drawMoneyLineChart(json_string);
            drawPieChart(json_string);
            drawBarChart(json_string);
        } else {
            Log.d("json_string", "month empty string");
        }
    }

    /*
    Display chart for energy consumption
     */

    public void drawEnergyLineChart(String json) {
        if (json ==null) {
            // handle the error
            Log.e("json_error", "empty jsonstring");
        } else{
            String msg = json.toString();
            msg = msg.replace("u","");

            Log.d("result msg",msg);
            String s1 = Normalizer.normalize(msg, Normalizer.Form.NFKD);
            String regex = Pattern.quote("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

            LineDataSet dataset;
            ArrayList<Entry> entries = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<String>();
            try {
                String s2 = new String(s1.replaceAll(regex, "").getBytes("ascii"), "ascii");
                JSONObject msg2 = new JSONObject(msg);
                Log.d("json", msg2.toString());

                JSONObject jsonObj = new JSONObject(msg2.toString());
                Log.d(" JSON Parsed Data", jsonObj.toString());
                int count = jsonObj.length();

                for (int i = 1; i <= count; i++) {
                    JSONObject rowObj = jsonObj.getJSONObject(String.valueOf(i));
                    Log.d("JSON Parsed Data", "check1");
                    String slot = rowObj.getString("slot");
                    Double fan_time = rowObj.getDouble("fan_time");
                    float ft = Float.valueOf(String.valueOf(fan_time));

                    Double human_time = rowObj.getDouble("hman_time");
                    float ht = Float.valueOf(String.valueOf(human_time));

                    Double energy_consumed = rowObj.getDouble("energy_consmed");
                    Double money_wasted = rowObj.getDouble("money_wasted");
                    String speed = rowObj.getString("speed");
                    Log.d("JSON Parsed Data", "check2");

                    float energy = Float.valueOf(String.valueOf(energy_consumed));
                    float money = Float.valueOf(String.valueOf(money_wasted));
                    Log.d("JSON Parsed Data", "check3");
                    entries.add(new Entry(energy, i));
                    labels.add(slot);
                }

            } catch (Exception e) {
                Log.e("error", e.toString());
            }
            dataset = new LineDataSet(entries, "money wasted");

            LineData data = new LineData(labels, dataset);
            energyLineChart.setData(data);
            energyLineChart.setDescription("Energy cinsumption Line Chart");
            energyLineChart.setTouchEnabled(true);
            energyLineChart.invalidate();
        }
    }


    /*
    Display chart for money that will be charged for running appliance
     */
    public void drawMoneyLineChart(String json) {
        String msg = json.toString();
        msg = msg.replace("u","");

        Log.d("result msg",msg);
        String s1 = Normalizer.normalize(msg, Normalizer.Form.NFKD);
        String regex = Pattern.quote("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

        LineDataSet dataset;
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();
        try {
            String s2 = new String(s1.replaceAll(regex, "").getBytes("ascii"), "ascii");
            JSONObject msg2 = new JSONObject(msg);
            Log.d("json", msg2.toString());

            JSONObject jsonObj = new JSONObject(msg2.toString());
            Log.d(" JSON Parsed Data", jsonObj.toString());
            int count = jsonObj.length();

            for (int i = 1; i <= count; i++) {
                JSONObject rowObj = jsonObj.getJSONObject(String.valueOf(i));
                Log.d("JSON Parsed Data", "check1");
                String slot = rowObj.getString("slot");
                Double fan_time = rowObj.getDouble("fan_time");
                float ft = Float.valueOf(String.valueOf(fan_time));

                Double human_time = rowObj.getDouble("hman_time");
                float ht = Float.valueOf(String.valueOf(human_time));

                Double energy_consumed = rowObj.getDouble("energy_consmed");
                Double money_wasted = rowObj.getDouble("money_wasted");
                String speed = rowObj.getString("speed");
                Log.d("JSON Parsed Data", "check2");

                float energy = Float.valueOf(String.valueOf(energy_consumed));
                float money = Float.valueOf(String.valueOf(money_wasted));
                Log.d("JSON Parsed Data", "check3");

                entries.add(new Entry(money, i));

                labels.add(slot);
            }

        } catch (Exception e) {
            Log.e("error", e.toString());
        }
        dataset = new LineDataSet(entries, "money wasted");

        LineData data = new LineData(labels, dataset);
        moneyLineChart.setData(data);
        moneyLineChart.setDescription("Monthly Money Line Chart");
        moneyLineChart.setTouchEnabled(true);
        moneyLineChart.invalidate();
    }

    /*
    Draw pie chart for fan speed data
     */
    public void drawPieChart(String json){
        String msg = json.toString();
        msg = msg.replace("u","");

        Log.d("result msg",msg);
        String s1 = Normalizer.normalize(msg, Normalizer.Form.NFKD);
        String regex = Pattern.quote("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

        List<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();

        PieDataSet dataset;
        try{
            String s2 = new String(s1.replaceAll(regex, "").getBytes("ascii"), "ascii");
            JSONObject msg2 = new JSONObject(msg);
            Log.d("json", msg2.toString());

            JSONObject jsonObj = new JSONObject(msg2.toString());
            Log.d(" JSON Parsed Data", jsonObj.toString());
            int count = jsonObj.length();

            Log.d("aaaa", jsonObj.getJSONObject(String.valueOf(-1)).toString());
            JSONObject row = jsonObj.getJSONObject(String.valueOf(-1));

            String label1 = "fan1";
            String label2 = "fan2";
            String label3 = "fan3";

            labels.add(label1);
            labels.add(label2);
            labels.add(label3);

            Double v3 = row.getDouble("fan_3");
            float vf3 = Float.valueOf(String.valueOf(v3));
            Double v2 =row.getDouble("fan_2");
            float vf2 = Float.valueOf(String.valueOf(v2));
            Double v1 = row.getDouble("fan_1");
            float vf1 = Float.valueOf(String.valueOf(v1));

            entries.add(new Entry(vf1, 0));
            entries.add(new Entry(vf2, 1));
            entries.add(new Entry(vf3, 2));

//            for (int i = 1; i <= count; i++) {
//                JSONObject rowObj = jsonObj.getJSONObject(String.valueOf(i));
//                Log.d("JSON Parsed Data", "check1");
//                String slot = rowObj.getString("slot");
//                Double fan_time = rowObj.getDouble("fan_time");
//                float ft = Float.valueOf(String.valueOf(fan_time));
//
//                Double human_time = rowObj.getDouble("hman_time");
//                float ht = Float.valueOf(String.valueOf(human_time));
//
//                Double energy_consumed = rowObj.getDouble("energy_consmed");
//                Double money_wasted = rowObj.getDouble("money_wasted");
//                String speed = rowObj.getString("speed");
//                int sp = Integer.parseInt(speed);
//                Log.d("JSON Parsed Data", "check2");
//
//                float energy = Float.valueOf(String.valueOf(energy_consumed));
//                float money = Float.valueOf(String.valueOf(money_wasted));
//                Log.d("JSON Parsed Data", "check3");
//
//                entries.add(new Entry(sp, i));
//
//                labels.add(slot);
//            }

            dataset = new PieDataSet(entries, "");
            dataset.setColors(ColorTemplate.COLORFUL_COLORS);
            pData = new PieData(labels, dataset);
            pie.setData(pData);
            pie.animateXY(2000, 2000);
            pie.setTouchEnabled(true);
            pie.invalidate();
        } catch(Exception e) {
            Log.e("error", e.toString());
        }
    }

    /*
    Draw chart for comparison between fan running time and human presence at certain time
     */
    public void drawBarChart(String json) {
        String msg;
        if(json==null) {
          msg = json.toString();
        }else{
            msg = "{u'0': {u'slot': u'Tuesday', u'fan_time': 0, u'energy_consumed': 0, u'label': u'Fan', u'money_wasted': 0, u'human_time': 0, u'speed': u'0'}}";
        }
        msg = msg.replace("u","");

        Log.d("result msg",msg);
        String s1 = Normalizer.normalize(msg, Normalizer.Form.NFKD);
        String regex = Pattern.quote("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        ArrayList<BarEntry> valueSet2 = new ArrayList<>();

        ArrayList<String> xAxis = new ArrayList<>();
        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        try{
            String s2 = new String(s1.replaceAll(regex, "").getBytes("ascii"), "ascii");
            JSONObject msg2 = new JSONObject(msg);
            Log.d("json", msg2.toString());

            JSONObject jsonObj = new JSONObject(msg2.toString());
            Log.d(" JSON Parsed Data", jsonObj.toString());
            int count = jsonObj.length();

            for (int i = 0; i < count; i++) {
                JSONObject rowObj = jsonObj.getJSONObject(String.valueOf(i));
                String slot = rowObj.getString("slot");
                Double fan_time = rowObj.getDouble("fan_time");
                float ft = Float.valueOf(String.valueOf(fan_time));
                Double human_time = rowObj.getDouble("hman_time");
                float ht = Float.valueOf(String.valueOf(human_time));

                Double energy_consumed = rowObj.getDouble("energy_consmed");
                Double money_wasted = rowObj.getDouble("money_wasted");
                String speed = rowObj.getString("speed");
                int sp = Integer.parseInt(speed);

                xAxis.add(slot);
                BarEntry v = new BarEntry(ft, i);
                valueSet1.add(v);
                BarEntry x = new BarEntry(ht, i);
                valueSet2.add(x);
            }

            BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Fan time");
            barDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);

            BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Human time");
            barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);

            dataSets.add(barDataSet1);
            dataSets.add(barDataSet2);

            ArrayList<String> a1 = xAxis;
            ArrayList<BarDataSet> b1 = dataSets;

            mData = new BarData(xAxis,dataSets);
            mChart.setData(mData);
            mChart.setDescription("My Chart");
            mChart.animateXY(2000, 2000);
            mChart.invalidate();


        }catch(Exception e) {
            Log.e("error", e.toString());
        }

    }

    private void drawPieChart() {
        // generate dataset for x and y axis

        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(4f, 0));
        entries.add(new Entry(8f, 1));
        entries.add(new Entry(6f, 2));
        entries.add(new Entry(12f, 3));
        entries.add(new Entry(18f, 4));
        entries.add(new Entry(9f, 5));

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");
        PieDataSet dataSet = new PieDataSet(entries, "no of cells");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pData = new PieData(labels, dataSet);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(!checkInternet()){
            Intent newIntent = new Intent(getActivity(),ErrorActivity.class);
            startActivity(newIntent);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    boolean checkInternet(){
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public String getMacId(){
        WifiManager manager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();
        return address;
    }

    void callServiceIntent(View view, String url){
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
        intent.putExtra("macid",getMacId());
        intent.putExtra("roomid",roomSelected);
        intent.putExtra("filepath",mFilePath);
        intent.putExtra("directory",mFilePath);
        intent.putExtra("url", url);
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
                Log.d(LOG_TAG,"Res"+results[0]);
                if(results==null || results[0].equalsIgnoreCase("Unable to get your results. Try again!")){
                    Log.d(LOG_TAG,"response fail");
                    Toast.makeText(getActivity().getApplicationContext(), "Response not received.", Toast.LENGTH_LONG).show();
                }else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    json_string = results[0];
                    if(UploadIntentService.URL.equalsIgnoreCase(MonthLink)){
                        drawMonthChart();
                    }else
                        if(UploadIntentService.URL.equalsIgnoreCase(WeekLink)){
                            drawWeekChart();
                        }
                    Log.d("json_string", json_string);
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

}


