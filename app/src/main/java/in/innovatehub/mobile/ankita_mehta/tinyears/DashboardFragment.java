package in.innovatehub.mobile.ankita_mehta.tinyears;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private static final String LOG_TAG = "DownloadService";

    GridView gridView;
    private ArrayList<Bitmap> images;

    final String[] items = {"Room1","Room2","Room3","Room4","Room5"};
    Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, null);

        gridView = (GridView) view.findViewById(R.id.gridview1);


        images = new ArrayList<Bitmap>();
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.plus_room_square);
        images.add(bm);

        Bitmap bm4 = BitmapFactory.decodeResource(getResources(), R.drawable.plus_room_square);
        images.add(bm4);

        GridAdapter adapter=new GridAdapter(getActivity(),images);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Log.d(LOG_TAG,parent.toString());
                Log.d(LOG_TAG,v.toString());
                Log.d(LOG_TAG, String.valueOf(position));
                Log.d(LOG_TAG, String.valueOf(id));
                if(id==0){
                    createDialog();
                }
            }
        });

        return view;
    }

    void createDialog(){  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        final ArrayList itemsSelected = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Room Type");
        builder.setSingleChoiceItems(items, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(LOG_TAG, String.valueOf(i));
                    }
                })
                .setPositiveButton("Done!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Your logic when OK button is clicked
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        dialog = builder.create();
        dialog.show();
    }
}


