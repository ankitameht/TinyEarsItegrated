package in.innovatehub.mobile.ankita_mehta.tinyears;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class AnalyticFragment extends Fragment {

    Button mLoadLastButton;
    Button mLoadOverallButton;
    TextView mResTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_analytic, null);

        mLoadLastButton = (Button) view.findViewById(R.id.load_last_results);
        mLoadOverallButton = (Button) view.findViewById(R.id.load_overall_results);
        mResTextView = (TextView) view.findViewById(R.id.res_textView);

        mLoadOverallButton.setEnabled(false);

        mLoadLastButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mLoadOverallButton.setEnabled(true);
                mLoadLastButton.setEnabled(false);
                mResTextView.setText("Clicked Load Last");
            }
        });

        mLoadOverallButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mLoadLastButton.setEnabled(true);
                mLoadOverallButton.setEnabled(false);
                mResTextView.setText("Clicked Load Overall");
            }
        });

        return view;
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
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

}
