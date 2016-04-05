package com.retinoblastoma;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

    // this is the action code we use in our intent,
    // this way we know we're looking at the response from our own action
    private static final int SELECT_PICTURE = 1;

    public static final String RESPONSE = "response";
    public static final String EXTRAS = "extras";

    private ImageView image;

    private DTOJsonResponse data;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button)
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        // in onCreate or any event where your want the user to
                        // select a file
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent,
                                "Select Picture"), SELECT_PICTURE);
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
        image = (ImageView) findViewById(R.id.image);
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
                        .baseUrl("http://192.168.0.16:8080/")
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

}
