package com.retinoblastoma;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.parceler.Parcels;

public class DetailsActivity extends AppCompatActivity {
    
    private TextView pupila1Message;
    private TextView pupila1Negro;
    private TextView pupila1Blanco;
    private TextView pupila1Rojo;
    private TextView pupila1Amarillo;
    private ImageView pupila1Imagen;

    private TextView pupila2Message;
    private TextView pupila2Negro;
    private TextView pupila2Blanco;
    private TextView pupila2Rojo;
    private TextView pupila2Amarillo;
    private ImageView pupila2Imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViews();
        setData(getExtras());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private DTOJsonResponse getExtras() {
        Bundle extras = getIntent().getBundleExtra(MainActivity.EXTRAS);
        Parcelable data = extras.getParcelable(MainActivity.RESPONSE);
        return Parcels.unwrap(data);
    }

    private void findViews() {
        pupila1Message = (TextView) findViewById(R.id.pupila1_mensaje);
        pupila1Negro = (TextView) findViewById(R.id.pupila1_negro);
        pupila1Blanco = (TextView) findViewById(R.id.pupila1_blanco);
        pupila1Rojo = (TextView) findViewById(R.id.pupila1_rojo);
        pupila1Amarillo = (TextView) findViewById(R.id.pupila1_amarillo);
        pupila1Imagen = (ImageView) findViewById(R.id.pupila1_imagen);

        pupila2Message = (TextView) findViewById(R.id.pupila2_mensaje);
        pupila2Negro = (TextView) findViewById(R.id.pupila2_negro);
        pupila2Blanco = (TextView) findViewById(R.id.pupila2_blanco);
        pupila2Rojo = (TextView) findViewById(R.id.pupila2_rojo);
        pupila2Amarillo = (TextView) findViewById(R.id.pupila2_amarillo);
        pupila2Imagen = (ImageView) findViewById(R.id.pupila2_imagen);
    }

    private void setData(DTOJsonResponse response) {
        PupilResult pupilResult1 = response.getList().get(0);
        PupilResult pupilResult2 = response.getList().get(1);

        pupila1Message.setText(pupila1Message.getText().toString().replace("@", pupilResult1.getMessage()));
        pupila1Negro.setText(pupila1Negro.getText().toString().replace("@", pupilResult1.getBlack()));
        pupila1Blanco.setText(pupila1Blanco.getText().toString().replace("@", pupilResult1.getWhite()));
        pupila1Rojo.setText(pupila1Rojo.getText().toString().replace("@", pupilResult1.getRed()));
        pupila1Amarillo.setText(pupila1Amarillo.getText().toString().replace("@", pupilResult1.getYellow()));
        ImageUtils.showImage(pupila1Imagen, pupilResult1.getFile());

        pupila2Message.setText(pupila2Message.getText().toString().replace("@", pupilResult2.getMessage()));
        pupila2Negro.setText(pupila2Negro.getText().toString().replace("@", pupilResult2.getBlack()));
        pupila2Blanco.setText(pupila2Blanco.getText().toString().replace("@", pupilResult2.getWhite()));
        pupila2Rojo.setText(pupila2Rojo.getText().toString().replace("@", pupilResult2.getRed()));
        pupila2Amarillo.setText(pupila2Amarillo.getText().toString().replace("@", pupilResult2.getYellow()));
        ImageUtils.showImage(pupila2Imagen, pupilResult2.getFile());
    }

}
