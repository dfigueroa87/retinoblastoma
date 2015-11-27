package com.retinoblastoma;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    public static final String DATA_FILE = "data_file";
    public static final String DATA_MESSAGE = "data_message";

    private DTOJson data;

    private TextView details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        String file = getIntent().getExtras().getString(DATA_FILE);
        String message = getIntent().getExtras().getString(DATA_MESSAGE);
        data = new DTOJson(file, message);

        details = (TextView) findViewById(R.id.details);

        details.setText(data.getFile() + "\n" + data.getExtension());
    }

}
