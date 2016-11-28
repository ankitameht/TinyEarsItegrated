package in.innovatehub.mobile.ankita_mehta.tinyears;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ErrorActivity extends AppCompatActivity {

    Button mRetryButton;
    String TAG = "Error Activiy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        mRetryButton = (Button) findViewById(R.id.retryButton_error404);
        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0){
                Log.d(TAG, "Inside on create, Navigating to Main Activity!");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
