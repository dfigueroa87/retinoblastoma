package com.retinoblastoma;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import org.apache.commons.codec.binary.Base64;
import org.parceler.Parcels;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;

    public static final String RESPONSE = "response";
    public static final String EXTRAS = "extras";
    public static final String SETTINGS_FRAGMENT_TAG = "SettingsFragment";
    public static final String IP_ADDRESS_KEY = "IP_ADDRESS_KEY";

    private static String ipAddress;

    private ImageView image;
    private DTOJsonResponse data;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupButtons();
        image = (ImageView) findViewById(R.id.image);

        loadIpAddress();
    }

    private void loadIpAddress() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        ipAddress = sharedPref.getString(IP_ADDRESS_KEY, null);
    }

    private void setupButtons() {
        findViewById(R.id.button)
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        if (ipAddress != null) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent,
                                    "Select Picture"), SELECT_PICTURE);
                        } else {
                            Toast.makeText(MainActivity.this, "El servidor no esta configurado",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        findViewById(R.id.details_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (data == null) {
                            Toast.makeText(MainActivity.this, getString(R.string.imagen_no_cargada), Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(RESPONSE, Parcels.wrap(data));
                            intent.putExtra(EXTRAS, bundle);

                            startActivity(intent);
                        }
                    }
                });
        findViewById(R.id.reset)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        image.setImageResource(0);

                        data = null;
                    }
                });
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                String base64 = getEncodedString(data);

                DTOJsonRequest params = new DTOJsonRequest(base64, ".jpg");

                OkHttpClient client = new OkHttpClient();
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                client.interceptors().add(interceptor);

                Retrofit retrofit = new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(ipAddress)
                        .client(client)
                        .build();

                Service service = retrofit.create(Service.class);
                Call<DTOJsonResponse> call = service.getResponse(params);
                call.enqueue(new Callback<DTOJsonResponse>() {
                    @Override
                    public void onResponse(Response<DTOJsonResponse> response, Retrofit retrofit) {
                        storeData(response.body());
                        ImageUtils.showImage(image, response.body().getFile());
                    }
                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(MainActivity.this, "Error en el servidor",
                                Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            }
        }
    }

    private void storeData(DTOJsonResponse jsonResponse) {
        data = jsonResponse;
    }

    private String getEncodedString(Intent data) {
        Uri uri = data.getData();
        String path = getRealPathFromURI(this, uri);

        try {
            File mFile = new File(path);

            FileInputStream imageInFile = new FileInputStream(mFile);
            byte imageData[] = new byte[(int) mFile.length()];
            imageInFile.read(imageData);

            Base64 base64 = new Base64();
            return new String(base64.encode(imageData));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            showSettingsDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showSettingsDialog() {
        DialogFragment dialog = new SettingsFragment();
        dialog.show(getSupportFragmentManager(), SETTINGS_FRAGMENT_TAG);
    }

    public static class SettingsFragment extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();

            View view = inflater.inflate(R.layout.settings_dialog, null);
            final EditText ipAddressEditText = (EditText) view.findViewById(R.id.ipAddress);
            builder.setView(view)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            ipAddress = String.format("http://%s/", ipAddressEditText.getText().toString());
                            saveServerAddress(ipAddress);
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SettingsFragment.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }

        private void saveServerAddress(String ipAddress) {
            SharedPreferences sharedPreferences = getActivity().getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(IP_ADDRESS_KEY, ipAddress);
            editor.apply();
        }

    }

}
