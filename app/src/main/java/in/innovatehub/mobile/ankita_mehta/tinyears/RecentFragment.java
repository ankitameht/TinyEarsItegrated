package in.innovatehub.mobile.ankita_mehta.tinyears;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;


public class RecentFragment extends Fragment {

    private static String mFilePath = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/TinyEars/"));
    private static final String LOG_TAG = "DownloadService";
    FloatingActionButton mFab;
    LinearLayout mL1;

    public RecentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recent, container, false);
        mL1 = (LinearLayout) view.findViewById(R.id.recent_layout);

        mFab = (FloatingActionButton) view.findViewById(R.id.fab_recent);
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
                        + File.separator + "Screenshot"+SystemClock.currentThreadTimeMillis()+".jpg");
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

        return view;
    }

}
