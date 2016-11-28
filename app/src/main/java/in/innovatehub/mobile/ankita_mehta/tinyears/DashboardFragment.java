package in.innovatehub.mobile.ankita_mehta.tinyears;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    private static final String LOG_TAG = "DownloadService";
    static final String STATE_LEVEL = "GRID_IMAGES";


    GridView gridView;
    private ArrayList<Bitmap> images;
    private ArrayList<Integer> imageIndex;

    final String[] items = {"Hallway","Bedroom","Living Room","Play Room","Kitchen"};
    Dialog dialog;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, null);
        gridView = (GridView) view.findViewById(R.id.gridview1);
        if (savedInstanceState != null) {
            Log.d(LOG_TAG,"savedInstanceState not null");
            imageIndex = savedInstanceState.getIntegerArrayList("STATE_LEVEL");
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.plus);
            images.add(bm);
            for(int i=0; i<imageIndex.size(); i++){
                int index  = imageIndex.get(i);
                switch (index){
                    case 0:
                        Bitmap bm0 = BitmapFactory.decodeResource(getResources(), R.drawable.hallway);
                        images.add(bm0);
                        break;
                    case 1:
                        Bitmap bm1 = BitmapFactory.decodeResource(getResources(), R.drawable.bedroom);
                        images.add(bm1);
                        break;
                    case 2:
                        Bitmap bm2 = BitmapFactory.decodeResource(getResources(), R.drawable.livingroom);
                        images.add(bm2);
                        break;
                    case 3:
                        Bitmap bm3 = BitmapFactory.decodeResource(getResources(), R.drawable.playroom);
                        images.add(bm3);
                        break;
                    case 4:
                        Bitmap bm4 = BitmapFactory.decodeResource(getResources(), R.drawable.kitchen);
                        images.add(bm4);
                        break;
                }
            }
        }else {
            Log.d(LOG_TAG,"savedInstanceState null");
            images = new ArrayList<Bitmap>();
            imageIndex = new ArrayList<Integer>();
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.plus);
            images.add(bm);
        }
        GridAdapter adapter = new GridAdapter(getActivity(), images);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Log.d(LOG_TAG, parent.toString());
                Log.d(LOG_TAG, v.toString());
                Log.d(LOG_TAG, String.valueOf(position));
                Log.d(LOG_TAG, String.valueOf(id));
                if (id == 0) {
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
                        switch (i){
                            case 0:
                                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.hallway);
                                images.add(bm);
                                imageIndex.add(0);
                                break;
                            case 1:
                                Bitmap bm1 = BitmapFactory.decodeResource(getResources(), R.drawable.bedroom);
                                images.add(bm1);
                                imageIndex.add(1);
                                break;
                            case 2:
                                Bitmap bm2 = BitmapFactory.decodeResource(getResources(), R.drawable.livingroom);
                                images.add(bm2);
                                imageIndex.add(2);
                                break;
                            case 3:
                                Bitmap bm3 = BitmapFactory.decodeResource(getResources(), R.drawable.playroom);
                                images.add(bm3);
                                imageIndex.add(3);
                                break;
                            case 4:
                                Bitmap bm4 = BitmapFactory.decodeResource(getResources(), R.drawable.kitchen);
                                images.add(bm4);
                                imageIndex.add(4);
                                break;
                        }
                    }
                })
                .setPositiveButton("Done!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Your logic when OK button is clicked
                        GridAdapter adapter=new GridAdapter(getActivity(),images);
                        gridView.setAdapter(adapter);
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putIntegerArrayList(STATE_LEVEL, imageIndex);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

}


