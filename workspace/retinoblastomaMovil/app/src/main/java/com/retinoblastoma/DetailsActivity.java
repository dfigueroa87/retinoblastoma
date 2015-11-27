package com.retinoblastoma;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    public static final String DATA_FILE = "data_file";
    public static final String DATA_MESSAGE = "data_message";
    public static final String DATA_WHITE = "data_white";
    public static final String DATA_BLACK = "data_black";
    public static final String DATA_RED = "data_red";
    public static final String DATA_YELLOW = "data_yellow";

    private DTOJsonResponse data;

    private TextView details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        String message = getIntent().getExtras().getString(DATA_MESSAGE);
        String white = getIntent().getExtras().getString(DATA_WHITE);
        String black = getIntent().getExtras().getString(DATA_BLACK);
        String red = getIntent().getExtras().getString(DATA_RED);
        String yellow = getIntent().getExtras().getString(DATA_YELLOW);

        data = new DTOJsonResponse("file", white, black, red, yellow, message);

        details = (TextView) findViewById(R.id.details);

        details.setText("Blanco: " + data.getWhite() + "\nNegro: " + data.getBlack() + "\nRojo: " + data.getRed() + "\nAmarillo: " + data.getYellow() + "\nMensaje: " + data.getMesssage());
    }

}
